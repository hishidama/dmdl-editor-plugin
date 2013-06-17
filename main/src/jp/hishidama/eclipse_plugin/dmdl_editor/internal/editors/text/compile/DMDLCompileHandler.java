package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.compile;

import jp.hishidama.eclipse_plugin.util.ProjectUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

public class DMDLCompileHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (!PlatformUI.getWorkbench().saveAllEditors(true)) {
			return null;
		}

		IProject project = ProjectUtil.getProject(event);
		if (project == null) {
			MessageDialog.openInformation(null, "DMDL compile", "IProjectが見つかりませんでした。\nプロジェクトを選択してから実行して下さい。");
			return null;
		}

		final DMDLCompileTask task = new DMDLCompileTask(project);

		WorkspaceJob job = new WorkspaceJob("DMDL compile") {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				// IWorkspace workspace = ResourcesPlugin.getWorkspace();
				// workspace.run(task, monitor);
				task.run(monitor);
				return Status.OK_STATUS;
			}
		};
		job.schedule();

		return null;
	}
}
