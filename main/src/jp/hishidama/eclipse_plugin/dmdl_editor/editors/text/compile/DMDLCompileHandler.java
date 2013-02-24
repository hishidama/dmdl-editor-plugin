package jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.compile;

import jp.hishidama.eclipse_plugin.util.FileUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

public class DMDLCompileHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IProject project = getProject(event);
		if (project == null) {
			MessageDialog.openInformation(null, "DMDL compile",
					"IProjectが見つかりませんでした。\nプロジェクトを選択してから実行して下さい。");
			return null;
		}

		final DMDLCompileTask task = new DMDLCompileTask(project);

		WorkspaceJob job = new WorkspaceJob("DMDL compile") {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				// IWorkspace workspace = ResourcesPlugin.getWorkspace();
				// workspace.run(task, monitor);
				task.run(monitor);
				return Status.OK_STATUS;
			}
		};
		job.schedule();

		return null;
	}

	private IProject getProject(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TextSelection) {
			IEditorPart editor = HandlerUtil.getActiveEditor(event);
			IFile file = FileUtil.getFile(editor);
			if (file != null) {
				return file.getProject();
			}
		} else if (selection instanceof TreeSelection) {
			TreeSelection tree = (TreeSelection) selection;
			TreePath[] paths = tree.getPaths();
			for (TreePath treePath : paths) {
				Object segment = treePath.getLastSegment();
				if (segment instanceof IResource) {
					return ((IResource) segment).getProject();
				} else if (segment instanceof IJavaElement) {
					return ((IJavaElement) segment).getJavaProject()
							.getProject();
				} else if (segment instanceof IAdaptable) {
					IAdaptable adaptable = (IAdaptable) segment;
					return (IProject) adaptable.getAdapter(IProject.class);
				}
			}
		}
		return null;
	}
}
