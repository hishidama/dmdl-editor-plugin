package jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.osgi.framework.Bundle;

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

		findClassPath(parserClassList, project);

		findMyClassPath(parserClassList, "resource/dmdlparser-caller.jar");

		ILog log = Activator.getDefault().getLog();
		log.log(new Status(Status.INFO, Activator.PLUGIN_ID,
				"DmdlParser caller classpath=" + parserClassList));

		parserLoader = URLClassLoader.newInstance(parserClassList
				.toArray(new URL[parserClassList.size()]));
	}

	protected void findClassPath(List<URL> list, IJavaProject javaProject) {
		IProject project = javaProject.getProject();
		ParserClassUtil.getClassPath(list, project);
	}

	protected URL findMyClassPath(List<URL> list, String jarName) {
		try {
			Bundle bundle = Activator.getDefault().getBundle();
			IPath path = Path.fromPortableString(jarName);
			URL bundleUrl = FileLocator.find(bundle, path, null);
			URL url = FileLocator.resolve(bundleUrl);

			list.add(url);
			return url;
		} catch (Exception e) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
					"DMDLMarker#findMyClassPath() error.", e));
			return null;
		}
	}

	public List<ParseErrorInfo> parse(List<IFile> ifiles) {
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

			List<ParseErrorInfo> result = new ArrayList<ParseErrorInfo>(
					list.size());
			for (Object[] r : list) {
				ParseErrorInfo pe = new ParseErrorInfo();
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