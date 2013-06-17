package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.marker;

import java.lang.reflect.InvocationTargetException;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DmdlCompilerProperties;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.marker.DMDLErrorCheckTask.FileList;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.BuildPropertiesUtil;
import jp.hishidama.eclipse_plugin.util.FileUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class DMDLErrorCheckHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (!PlatformUI.getWorkbench().saveAllEditors(true)) {
			return null;
		}

		FileList list = new FileList();

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TextSelection) {
			IEditorPart editor = HandlerUtil.getActiveEditor(event);
			IFile file = FileUtil.getFile(editor);
			search(list, file.getProject());
		} else if (selection instanceof TreeSelection) {
			TreeSelection tree = (TreeSelection) selection;
			TreePath[] paths = tree.getPaths();
			for (TreePath path : paths) {
				Object segment = path.getLastSegment();
				if (segment instanceof IResource) {
					search(list, ((IResource) segment).getProject());
				}
			}
		}

		execute(list, true, true, new ProgressMonitorDialog(null));
		return null;
	}

	public void execute(IProject project, boolean createIndex, boolean checkMark, IRunnableContext runner) {
		FileList list = new FileList();
		search(list, project);
		execute(list, createIndex, checkMark, runner);
	}

	public DMDLErrorCheckTask createTask(IFolder folder) {
		FileList list = new FileList();
		search(list, folder);
		return new DMDLErrorCheckTask(list, true, true);
	}

	private void search(FileList list, IProject project) {
		if (project == null) {
			return;
		}
		IFolder folder = null;
		{
			DmdlCompilerProperties bp = BuildPropertiesUtil.getBuildProperties(project, true);
			if (bp == null) {
				return;
			}
			String dir = bp.getDmdlDir();
			if (dir != null) {
				folder = FileUtil.getFolder(project, dir);
			}
		}
		if (folder != null) {
			search(list, folder);
		} else {
			search(list, (IContainer) project);
		}
	}

	private void search(FileList list, IContainer folder) {
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

	private void execute(FileList projects, boolean createIndex, boolean checkMark, IRunnableContext runner) {
		final DMDLErrorCheckTask task = new DMDLErrorCheckTask(projects, createIndex, checkMark);
		// IWorkbench workbench = PlatformUI.getWorkbench();
		// IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		try {
			if (runner != null) {
				runner.run(true, true, task);
			} else {
				task.run(new NullProgressMonitor());
			}
		} catch (InvocationTargetException e) {
			IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, "DMDL error check error.", e);
			Activator.getDefault().getLog().log(status);
			ErrorDialog.openError(null, "error", status.getMessage(), status);
		} catch (InterruptedException e) {
			// return Status.CANCEL_STATUS;
		}
	}
}
