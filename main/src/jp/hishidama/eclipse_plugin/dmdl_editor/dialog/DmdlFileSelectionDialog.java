package jp.hishidama.eclipse_plugin.dmdl_editor.dialog;

import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelFile;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelInfo;
import jp.hishidama.eclipse_plugin.dmdl_editor.viewer.DMDLTreeData;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Shell;

public class DmdlFileSelectionDialog extends DataModelTreeDialog {

	public DmdlFileSelectionDialog(Shell parentShell, IProject project) {
		super(parentShell, project, "DMDLファイル選択");
	}

	@Override
	protected boolean validateData(Object obj) {
		return true;
	}

	public IFile getSelectedFile() {
		for (DMDLTreeData data = getSelectionData(); data != null; data = data.getParent()) {
			Object obj = data.getData();
			if (obj instanceof DataModelFile) {
				return ((DataModelFile) obj).getFile();
			}
		}
		return null;
	}

	public String getSelectedModelName() {
		for (DMDLTreeData data = getSelectionData(); data != null; data = data.getParent()) {
			Object obj = data.getData();
			if (obj instanceof DataModelInfo) {
				return ((DataModelInfo) obj).getModelName();
			}
		}
		return null;
	}
}
