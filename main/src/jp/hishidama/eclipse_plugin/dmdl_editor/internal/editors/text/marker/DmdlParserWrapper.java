package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.marker;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

public class DmdlParserWrapper {
	private static final String CALLER_CLASS = "jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker.DmdlParserCaller";

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
		log.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, MessageFormat.format("DmdlParser caller classpath={0}",
				StringUtil.toString(parserClassList))));

		parserLoader = URLClassLoader.newInstance(parserClassList.toArray(new URL[parserClassList.size()]));
	}

	protected void findClassPath(List<URL> list, IJavaProject javaProject) {
		ParserClassUtil.getProjectClassPath(list, javaProject);
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
			log.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, "DmdlParser#findMyClassPath() error.", e));
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
				String message = MessageFormat.format("DmdlParser#parse({0}) error.", f);
				IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, message, e);
				Activator.getDefault().getLog().log(status);
			}
		}
		String taskName = "DMDLパーサーのロード中";
		try {
			Class<?> c = parserLoader.loadClass(CALLER_CLASS);
			taskName = "DMDLパーサーの準備中";
			Object caller = c.newInstance();
			taskName = "DMDLパーサーのメソッド準備中";
			Method method = c.getMethod("parse", List.class);
			taskName = "DMDLパーサーの実行中";
			@SuppressWarnings("unchecked")
			List<Object[]> list = (List<Object[]>) method.invoke(caller, files);

			taskName = "DMDLパーサーの実行結果のまとめ中";
			List<ParseErrorInfo> result = new ArrayList<ParseErrorInfo>(list.size());
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
		} catch (final Throwable e) {
			{
				String message = MessageFormat.format("DmdlParser#parse() error. classpath={0}",
						StringUtil.toString(parserClassList));
				IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, message, e);
				Activator.getDefault().getLog().log(status);
			}
			final String taskName0 = taskName;
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					Throwable t = e;
					while (t.getCause() != null) {
						t = t.getCause();
					}
					IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, MessageFormat.format(
							"DmdlParser#parse() error.\nexception={0}\nmessage={1}", t.getClass().getName(),
							t.getMessage()), t);
					ErrorDialog.openError(null, "error", taskName0 + "にエラーが発生しました。", status);
				}
			});
		}
		return null;
	}
}