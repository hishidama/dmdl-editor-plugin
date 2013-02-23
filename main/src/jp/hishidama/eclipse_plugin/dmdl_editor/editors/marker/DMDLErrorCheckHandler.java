package jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker.DMDLErrorCheckTask.FileList;
import jp.hishidama.eclipse_plugin.util.FileUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.WorkbenchJob;

public class DMDLErrorCheckHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		FileList list = new FileList();

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TextSelection) {
			IEditorPart editor = HandlerUtil.getActiveEditor(event);
			IFile file = FileUtil.getFile(editor);
			search(list, file);
		} else if (selection instanceof TreeSelection) {
			TreeSelection tree = (TreeSelection) selection;
			TreePath[] paths = tree.getPaths();
			for (TreePath path : paths) {
				Object segment = path.getLastSegment();
				if (segment instanceof IFile) {
					search(list, (IFile) segment);
				} else if (segment instanceof IFolder) {
					search(list, (IFolder) segment);
				}
			}
		}

		execute(list, true, true);
		return null;
	}

	public void execute(IFile file, boolean createIndex, boolean checkMark) {
		FileList list = new FileList();
		search(list, file);
		execute(list, createIndex, checkMark);
	}

	public DMDLErrorCheckTask createTask(IFolder folder) {
		FileList list = new FileList();
		search(list, folder);
		return new DMDLErrorCheckTask(list, true, true);
	}

	private void search(FileList list, IFile file) {
		if (file == null) {
			return;
		}
		IFolder folder = null;
		{
			IProject project = file.getProject();
			Properties properties = ParserClassUtil.getBuildProperties(project);
			if (properties != null) {
				String dir = properties.getProperty("asakusa.dmdl.dir");
				if (dir != null) {
					folder = FileUtil.getFolder(project, dir);
				}
			}
		}
		if (folder == null) {
			IPath path = file.getFullPath();
			IPath parent = path.removeLastSegments(1);
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			folder = root.getFolder(parent);
		}
		search(list, folder);
	}

	private void search(FileList list, IFolder folder) {
		IResource[] rs;
		try {
			rs = folder.members();
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		for (IResource r : rs) {
			if (r instanceof IFile) {
				String ext = r.getFileExtension();
				if ("dmdl".equalsIgnoreCase(ext)) {
					list.add((IFile) r);
				}
			} else if (r instanceof IFolder) {
				search(list, (IFolder) r);
			}
		}
	}

	private void execute(FileList projects, boolean createIndex,
			boolean checkMark) {
		final DMDLErrorCheckTask task = new DMDLErrorCheckTask(projects,
				createIndex, checkMark);
		WorkbenchJob job = new WorkbenchJob("DMDL create index") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				try {
					task.run(monitor);
				} catch (InvocationTargetException e) {
					return new Status(Status.WARNING, Activator.PLUGIN_ID,
							"DMDL error check error.", e);
				} catch (InterruptedException e) {
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
}
