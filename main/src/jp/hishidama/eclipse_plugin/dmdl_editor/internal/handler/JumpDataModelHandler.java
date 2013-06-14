package jp.hishidama.eclipse_plugin.dmdl_editor.internal.handler;

import jp.hishidama.eclipse_plugin.dmdl_editor.dialog.DmdlAnySelectionDialog;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DMDLHyperlinkUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelPosition;
import jp.hishidama.eclipse_plugin.util.ProjectUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

public class JumpDataModelHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IProject project = ProjectUtil.getProject(event);
		if (project == null) {
			MessageDialog.openInformation(null, "jump DMDL", "IProjectが見つかりませんでした。\nプロジェクトを選択してから実行して下さい。");
			return null;
		}

		DmdlAnySelectionDialog dialog = new DmdlAnySelectionDialog(null, project, "ジャンプ先 データモデル/プロパティー の選択");
		if (dialog.open() == Window.OK) {
			DataModelPosition index = dialog.getSelectedDataModel();
			if (index != null) {
				DMDLHyperlinkUtil.gotoPosition(index);
			}
		}

		return null;
	}
}
