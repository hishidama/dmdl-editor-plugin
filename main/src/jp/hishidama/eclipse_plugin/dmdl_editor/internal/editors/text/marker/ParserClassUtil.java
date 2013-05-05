package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.marker;

import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.PARSER_JAR_CHECKED;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.PARSER_JAR_FILES;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index.IndexContainer;
import jp.hishidama.eclipse_plugin.util.FileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.TableItem;

public class ParserClassUtil {

	public static void initTable(CheckboxTableViewer viewer, IProject project) {
		String jars = getValue(project, PARSER_JAR_FILES);
		String checks = getValue(project, PARSER_JAR_CHECKED);
		setTable(viewer, jars, checks);
	}

	public static void initTable(CheckboxTableViewer viewer, IPreferenceStore store) {
		String jars = store.getString(PARSER_JAR_FILES);
		String checks = store.getString(PARSER_JAR_CHECKED);
		setTable(viewer, jars, checks);
	}

	private static void setTable(CheckboxTableViewer viewer, String jars, String checks) {
		viewer.setContentProvider(new IStructuredContentProvider() {
			@Override
			public Object[] getElements(Object inputElement) {
				return (String[]) inputElement;
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}
		});

		String[] path = jars.split(",");
		viewer.setInput(path);

		String[] check = checks.split(",");
		for (int i = 0; i < path.length; i++) {
			String element = path[i];
			boolean state = Boolean.parseBoolean(check[i]);
			viewer.setChecked(element, state);
		}
	}

	public static void save(CheckboxTableViewer viewer, IProject project) {
		TableItem[] items = viewer.getTable().getItems();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		for (TableItem item : items) {
			String element = (String) item.getData();
			sb1.append(element);
			sb1.append(",");
			sb2.append(viewer.getChecked(element));
			sb2.append(",");
		}
		setValue(project, PARSER_JAR_FILES, sb1.toString());
		setValue(project, PARSER_JAR_CHECKED, sb2.toString());

		try {
			project.setSessionProperty(DMDLErrorCheckTask.KEY, null);
		} catch (CoreException e) {
			ILog log = Activator.getDefault().getLog();
			log.log(e.getStatus());
		}

		IndexContainer.remove(project);
	}

	public static void getClassPath(List<URL> list, IProject project) {
		String jars = getValue(project, PARSER_JAR_FILES);
		if (jars == null) {
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			jars = store.getString(PARSER_JAR_FILES);
		}
		String checks = getValue(project, PARSER_JAR_CHECKED);
		if (checks == null) {
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			checks = store.getString(PARSER_JAR_CHECKED);
		}

		String[] path = jars.split(",");
		String[] check = checks.split(",");
		for (int i = 0; i < path.length; i++) {
			if (Boolean.parseBoolean(check[i])) {
				try {
					IPath pp = Path.fromPortableString(path[i]);
					if (pp.toFile().exists()) {
						URL url = pp.toFile().toURI().toURL();
						list.add(url);
						continue;
					}
					IPath vp = JavaCore.getResolvedVariablePath(pp);
					if (vp != null) {
						URL url = vp.toFile().toURI().toURL();
						list.add(url);
						continue;
					}
					IFile file = FileUtil.getFile(project, path[i]);
					if (file != null) {
						URI uri = file.getLocationURI();
						if (uri != null) {
							list.add(uri.toURL());
							continue;
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getValue(IProject project, String key) {
		try {
			String value = project.getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, key));
			if (value != null) {
				return value;
			}
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			return store.getString(key);
		} catch (CoreException e) {
			ILog log = Activator.getDefault().getLog();
			log.log(e.getStatus());
			return null;
		}
	}

	private static void setValue(IProject project, String key, String value) {
		try {
			project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, key), value);
		} catch (CoreException e) {
			ILog log = Activator.getDefault().getLog();
			log.log(e.getStatus());
		}
	}
}
