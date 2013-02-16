package jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

public class DmdlParserWrapper {
	public DmdlParserWrapper(IJavaProject project) {
		initClassLoader(project);
	}

	private ClassLoader parserLoader;

	public boolean isValid() {
		return parserLoader != null;
	}

	private List<URL> parserClassList;

	protected void initClassLoader(IJavaProject project) {
		parserClassList = new ArrayList<URL>();
		URL parserUrl = findClassPath(parserClassList, project,
				"com.asakusafw.dmdl.parser.DmdlParser");
		if (parserUrl == null) {
			return;
		}
		findClassPath(parserClassList, project, "org.slf4j.LoggerFactory");
		findClassPath(parserClassList, project,
				"com.asakusafw.utils.collections.Lists");
		findClassPath(parserClassList, project,
				"com.asakusafw.utils.graph.Graphs");

		// Direct I/O, WindGate, etc
		findDmdlService(parserClassList, project);

		findMyClassPath(parserClassList, "resource/dmdlparser-caller.jar");

		ILog log = Activator.getDefault().getLog();
		log.log(new Status(Status.INFO, Activator.PLUGIN_ID,
				"DMDLMarker classpath=" + parserClassList));

		parserLoader = URLClassLoader.newInstance(parserClassList
				.toArray(new URL[parserClassList.size()]));
	}

	protected URL findClassPath(List<URL> list, IJavaProject project,
			String className) {
		try {
			IType type = project.findType(className);
			if (type != null) {
				IClassFile f = type.getClassFile();
				URL url = f.getPath().toFile().toURI().toURL();
				list.add(url);
				return url;
			} else {
				// ILog log = Activator.getDefault().getLog();
				// log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
				// "DMDLMarker#findClassPath(" + className + ")"));
			}
			return null;
		} catch (Exception e) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
					"DMDLMarker#findClassPath(" + className + ")", e));
			return null;
		}
	}

	protected void findDmdlService(List<URL> list, IJavaProject project) {
		IClasspathEntry[] entries;
		try {
			entries = project.getRawClasspath();
		} catch (JavaModelException e) {
			return;
		}
		for (IClasspathEntry ce : entries) {
			try {
				File file = JavaCore.getResolvedClasspathEntry(ce).getPath()
						.toFile();
				if (!file.getName().endsWith(".jar")) {
					continue;
				}
				JarFile jf = new JarFile(file);
				try {
					ZipEntry driver = jf
							.getEntry("META-INF/services/com.asakusafw.dmdl.spi.AttributeDriver");
					if (driver == null) {
						driver = jf
								.getEntry("META-INF/services/com.asakusafw.dmdl.spi.TypeDriver");
					}
					if (driver != null) {
						list.add(file.toURI().toURL());
					}
				} finally {
					jf.close();
				}
			} catch (Exception e) {
			}
		}
	}

	protected URL findMyClassPath(List<URL> list, String jarName) {
		try {
			URL bundleUrl = getClass().getClassLoader().getResource(jarName);
			URL url = FileLocator.resolve(bundleUrl);
			String s = url.toExternalForm();
			if (s.startsWith("jar:")) {
				File dir = Activator.getDefault().getStateLocation().toFile();
				File tempFile = new File(dir, "dmdlparser-caller.jar");
				InputStream is = url.openStream();
				try {
					OutputStream os = new FileOutputStream(tempFile);
					try {
						byte[] buf = new byte[8192];
						for (;;) {
							int len = is.read(buf);
							if (len > 0) {
								os.write(buf, 0, len);
							} else {
								break;
							}
						}
					} finally {
						os.close();
					}
				} finally {
					is.close();
				}
				url = tempFile.toURI().toURL();
			}

			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.INFO, Activator.PLUGIN_ID,
					"DMDLMarker#findMyClassPath(" + jarName + ")-> " + url));

			list.add(url);
			return url;
		} catch (Exception e) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
					"DMDLMarker#findMyClassPath() error.", e));
			return null;
		}
	}

	public List<ParseError> parse(List<IFile> ifiles) {
		List<Object[]> files = new ArrayList<Object[]>(ifiles.size());
		for (IFile f : ifiles) {
			try {
				Object[] arr = { f.getLocationURI(), f.getCharset() };
				files.add(arr);
			} catch (Exception e) {
				ILog log = Activator.getDefault().getLog();
				log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
						"DMDLMarker#parse(" + f + ") error.", e));
			}
		}
		try {
			Class<?> c = parserLoader
					.loadClass("jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker.DmdlParserCaller");
			Object caller = c.newInstance();
			Method method = c.getMethod("parse", List.class);
			@SuppressWarnings("unchecked")
			List<Object[]> list = (List<Object[]>) method.invoke(caller, files);

			List<ParseError> result = new ArrayList<ParseError>(list.size());
			for (Object[] r : list) {
				ParseError pe = new ParseError();
				pe.file = (URI) r[0];
				pe.level = (Integer) r[1];
				pe.message = (String) r[2];
				pe.beginLine = (Integer) r[3];
				pe.beginColumn = (Integer) r[4];
				pe.endLine = (Integer) r[5];
				pe.endColumn = (Integer) r[6];
				result.add(pe);
			}
			return result;
		} catch (Exception e) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
					"DMDLMarker#parse(" + parserClassList + ") error.", e));
		} catch (Error e) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
					"DMDLMarker#parse(" + parserClassList + ") error.", e));
		}
		return null;
	}
}