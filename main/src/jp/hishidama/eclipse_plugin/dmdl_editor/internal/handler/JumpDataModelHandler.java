package jp.hishidama.eclipse_plugin.dmdl_editor.internal.handler;

import jp.hishidama.eclipse_plugin.dmdl_editor.dialog.DmdlAnySelectionDialog;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DMDLHyperlinkUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelPosition;
import jp.hishidama.eclipse_plugin.util.FileUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

public class JumpDataModelHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IProject project = null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TextSelection) {
			IEditorPart editor = HandlerUtil.getActiveEditor(event);
			IFile file = FileUtil.getFile(editor);
			project = file.getProject();
		} else if (selection instanceof TreeSelection) {
			TreeSelection tree = (TreeSelection) selection;
			TreePath[] paths = tree.getPaths();
			for (TreePath path : paths) {
				Object segment = path.getLastSegment();
				if (segment instanceof IResource) {
					project = ((IResource) segment).getProject();
					break;
				}
			}
		}

		if (project != null) {
			DmdlAnySelectionDialog dialog = new DmdlAnySelectionDialog(null, project, "ジャンプ先 データモデル/プロパティー の選択");
			if (dialog.open() == Window.OK) {
				DataModelPosition index = dialog.getSelectedDataModel();
				if (index != null) {
					DMDLHyperlinkUtil.gotoPosition(index);
				}
			}
		} else {
			MessageDialog.openInformation(null, "information", "プロジェクトを選択して下さい。");
		}

		return null;
	}
}
