package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.marker;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLEditorConfiguration;
import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLEditorConfiguration.Library;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.property.DMDLPropertyPageUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index.IndexContainer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.TableItem;

public class ParserClassUtil {
	public static void initTable(CheckboxTableViewer viewer, IProject project) {
		List<Library> libs = DMDLPropertyPageUtil.getLibraries(project);
		setTable(viewer, libs);
	}

	public static void initTableDefault(CheckboxTableViewer viewer, IProject project) {
		List<Library> libs = DMDLPropertyPageUtil.getDefaultLibraries(project);
		setTable(viewer, libs);
	}

	public static void initTableDefault(CheckboxTableViewer viewer, IProject project, DMDLEditorConfiguration c) {
		List<Library> libs = DMDLPropertyPageUtil.getDefaultLibraries(project, c);
		setTable(viewer, libs);
	}

	private static void setTable(CheckboxTableViewer viewer, List<Library> libs) {
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

		List<String> path = new ArrayList<String>();
		for (Library lib : libs) {
			path.add(lib.path);
		}
		viewer.setInput(path.toArray(new String[path.size()]));

		TableItem[] items = viewer.getTable().getItems();
		for (int i = 0; i < libs.size(); i++) {
			boolean state = libs.get(i).selected;
			items[i].setChecked(state);
		}
	}

	public static void save(CheckboxTableViewer viewer, IProject project) {
		List<Library> list = new ArrayList<Library>();

		TableItem[] items = viewer.getTable().getItems();
		for (TableItem item : items) {
			String path = (String) item.getData();
			boolean check = item.getChecked();
			Library lib = new Library(path, check);
			list.add(lib);
		}
		DMDLPropertyPageUtil.setLibraries(project, list);

		try {
			project.setSessionProperty(DMDLErrorCheckTask.KEY, null);
		} catch (CoreException e) {
			ILog log = Activator.getDefault().getLog();
			log.log(e.getStatus());
		}

		IndexContainer.remove(project);
	}

	public static void getClassPath(List<URL> list, IProject project) {
		List<Library> libs = DMDLPropertyPageUtil.getLibraries(project);

		for (Library lib : libs) {
			if (lib.selected) {
				try {
					IPath pp = Path.fromPortableString(lib.path);
					URL url = toURL(project, pp);
					if (url != null) {
						list.add(url);
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static URL toURL(IProject project, IPath path) throws MalformedURLException {
		if (path == null) {
			return null;
		}
		if (path.toFile().exists()) {
			URL url = path.toFile().toURI().toURL();
			return url;
		}
		IPath vp = JavaCore.getResolvedVariablePath(path);
		if (vp != null) {
			URL url = vp.toFile().toURI().toURL();
			return url;
		}
		try {
			IFile file = project.getFile(path);
			if (file.exists()) {
				URI uri = file.getLocationURI();
				if (uri != null) {
					return uri.toURL();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			IFile file = project.getParent().getFile(path);
			if (file.exists()) {
				URI uri = file.getLocationURI();
				if (uri != null) {
					return uri.toURL();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void getProjectClassPath(List<URL> list, IJavaProject project) {
		try {
			IClasspathEntry[] cp = project.getRawClasspath();
			getClassPath(list, project, cp);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private static void getClassPath(List<URL> list, IJavaProject project, IClasspathEntry[] cp) {
		for (IClasspathEntry ce : cp) {
			URL url = null;
			try {
				switch (ce.getEntryKind()) {
				case IClasspathEntry.CPE_SOURCE:
					url = toURL(project.getProject(), ce.getOutputLocation());
					break;
				case IClasspathEntry.CPE_VARIABLE:
					url = toURL(project.getProject(), JavaCore.getResolvedVariablePath(ce.getPath()));
					break;
				case IClasspathEntry.CPE_LIBRARY:
					url = toURL(project.getProject(), ce.getPath());
					break;
				case IClasspathEntry.CPE_CONTAINER:
					if (!ce.getPath().toPortableString().contains("JRE_CONTAINER")) {
						IClasspathContainer cr = JavaCore.getClasspathContainer(ce.getPath(), project);
						getClassPath(list, project, cr.getClasspathEntries());
					}
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (url != null) {
				list.add(url);
			}
		}
	}
}
