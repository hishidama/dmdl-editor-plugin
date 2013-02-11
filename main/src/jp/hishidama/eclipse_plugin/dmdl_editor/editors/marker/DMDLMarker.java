package jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLDocument;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.IDocument;

public class DMDLMarker {

	protected Map<IJavaProject, ParserWrapper> map = new HashMap<IJavaProject, ParserWrapper>();

	public void parse(IFile file, DMDLDocument document) {
		IJavaProject project = getJavaProject(file);
		if (project == null) {
			return;
		}

		ParserWrapper wrapper = map.get(project);
		if (wrapper == null) {
			wrapper = new ParserWrapper(project);
			map.put(project, wrapper);
		}
		if (wrapper.isValid()) {
			wrapper.call(file, document);
		}
	}

	protected final IJavaProject getJavaProject(IFile file) {
		IProject project = file.getProject();
		return JavaCore.create(project);
	}

	protected static class ParserWrapper {
		public ParserWrapper(IJavaProject project) {
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
			findMyClassPath(parserClassList,
					"jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker.DmdlParserCaller");
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
					ILog log = Activator.getDefault().getLog();
					log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
							"DMDLMarker#findClassPath(" + className + ")"));
				}
				return null;
			} catch (Exception e) {
				ILog log = Activator.getDefault().getLog();
				log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
						"DMDLMarker#findClassPath(" + className + ")", e));
				return null;
			}
		}

		protected URL findMyClassPath(List<URL> list, String className) {
			try {
				URL url = null;

				String path = className.replace('.', '/');
				URL bundleUrl = getClass().getResource("/" + path + ".class");
				URL resolveUrl = FileLocator.resolve(bundleUrl);
				String s = resolveUrl.toExternalForm();
				if (s.startsWith("jar:")) {
					int n = s.lastIndexOf("!/jp/hishidama");
					if (n >= 0) {
						s = s.substring(4, n);
					}
				} else if (s.startsWith("file:")) {
					int n = s.lastIndexOf("/jp/hishidama");
					if (n >= 0) {
						s = s.substring(0, n) + "/";
					}
				} else {
					ILog log = Activator.getDefault().getLog();
					log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
							"DMDLMarker#findMyClassPath() unsupported URL: "
									+ s));
				}
				url = new URL(s);
				list.add(url);
				return url;
			} catch (Exception e) {
				ILog log = Activator.getDefault().getLog();
				log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
						"DMDLMarker#findMyClassPath() error.", e));
				return null;
			}
		}

		public void call(IFile file, IDocument document) {
			URI uri;
			try {
				uri = new URI("file:" + file.getFullPath().toFile().getName());
			} catch (URISyntaxException e1) {
				uri = null;
			}
			try {
				Class<?> c = parserLoader
						.loadClass("jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker.DmdlParserCaller");
				Object caller = c.newInstance();
				Method method = c.getMethod("parse", URI.class, String.class);
				@SuppressWarnings("unchecked")
				List<Object[]> list = (List<Object[]>) method.invoke(caller,
						uri, document.get());
				for (Object[] r : list) {
					String message = (String) r[0];
					int beginLine = (Integer) r[1];
					int beginColumn = (Integer) r[2];
					int endLine = (Integer) r[3];
					int endColumn = (Integer) r[4];
					createErrorMarker(file, document, message, beginLine,
							beginColumn, endLine, endColumn);
				}
			} catch (Exception e) {
				ILog log = Activator.getDefault().getLog();
				log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
						"DMDLMarker#call(" + parserClassList + ") error.", e));
			} catch (Error e) {
				ILog log = Activator.getDefault().getLog();
				log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
						"DMDLMarker#call(" + parserClassList + ") error.", e));
			}
		}

		protected void createErrorMarker(IFile file, IDocument document,
				String message, int beginLine, int beginColumn, int endLine,
				int endColumn) {
			try {

				int beginOffset = document.getLineOffset(beginLine - 1)
						+ beginColumn - 1;
				int endOffset = document.getLineOffset(endLine - 1) + endColumn;

				IMarker marker = file.createMarker(IMarker.PROBLEM);
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				marker.setAttribute(IMarker.MESSAGE, message);
				// marker.setAttribute(IMarker.LINE_NUMBER, beginLine - 1);
				marker.setAttribute(IMarker.CHAR_START, beginOffset);
				marker.setAttribute(IMarker.CHAR_END, endOffset);
				marker.setAttribute(IMarker.LOCATION, String.format(
						"%d:%d-%d:%d", beginLine, beginColumn, endLine,
						endColumn));
			} catch (Exception e) {
				ILog log = Activator.getDefault().getLog();
				log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
						"DMDLMarker#createErrorMarker() error.", e));
			}
		}
	}
}
