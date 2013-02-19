package jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker;

import java.lang.reflect.InvocationTargetException;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker.DMDLErrorCheckTask.FileList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class DMDLErrorCheckHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		FileList list = new FileList();

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TextSelection) {
			DMDLEditor editor = (DMDLEditor) HandlerUtil.getActiveEditor(event);
			if (editor != null) {
				IFile file = editor.getFile();
				search(list, file);
			}
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

	private void search(FileList list, IFile file) {
		IPath path = file.getFullPath();
		IPath parent = path.removeLastSegments(1);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFolder folder = root.getFolder(parent);
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
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				Shell shell = null;
				IWorkbench workbench = PlatformUI.getWorkbench();
				if (workbench != null) {
					IWorkbenchWindow window = workbench
							.getActiveWorkbenchWindow();
					if (window != null) {
						shell = window.getShell();
					}
				}
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
				try {
					dialog.run(true, true, task);
				} catch (InvocationTargetException e) {
					ILog log = Activator.getDefault().getLog();
					log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
							"DMDL error check error.", e));
				} catch (InterruptedException e) {
					MessageDialog.openInformation(shell, "DMDL error check",
							"canceled.");
				}
			}
		});
	}
}
