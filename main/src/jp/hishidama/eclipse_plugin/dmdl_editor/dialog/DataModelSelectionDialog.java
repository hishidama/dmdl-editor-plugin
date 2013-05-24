package jp.hishidama.eclipse_plugin.dmdl_editor.dialog;

import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelInfo;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelProperty;
import jp.hishidama.eclipse_plugin.dmdl_editor.viewer.DMDLTreeData;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Shell;

public class DataModelSelectionDialog extends DataModelTreeDialog {

	public DataModelSelectionDialog(Shell parentShell, IProject project) {
		super(parentShell, project, "データモデル選択");
	}

	@Override
	protected int getInitialExpandLevel() {
		return 2;
	}

	@Override
	protected boolean validateData(Object obj) {
		return (obj instanceof DataModelInfo) || (obj instanceof DataModelProperty);
	}

	public DataModelInfo getSelectedDataModel() {
		for (DMDLTreeData data = getSelectionData(); data != null; data = data.getParent()) {
			Object obj = data.getData();
			if (obj instanceof DataModelInfo) {
				return (DataModelInfo) obj;
			}
		}
		return null;
	}
}
