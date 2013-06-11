package jp.hishidama.eclipse_plugin.dmdl_editor.dialog;

import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelInfo;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelPosition;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelProperty;
import jp.hishidama.eclipse_plugin.dmdl_editor.viewer.DMDLTreeData;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Shell;

public class DmdlModelPropertySelectionDialog extends DataModelTreeDialog {

	public DmdlModelPropertySelectionDialog(Shell parentShell, IProject project) {
		this(parentShell, project, "データモデル/プロパティー選択");
	}

	public DmdlModelPropertySelectionDialog(Shell parentShell, IProject project, String windowTitle) {
		super(parentShell, project, windowTitle);
	}

	@Override
	protected int getInitialExpandLevel() {
		return 1;
	}

	@Override
	protected boolean validateData(Object obj) {
		return (obj instanceof DataModelInfo) || (obj instanceof DataModelProperty);
	}

	public DataModelPosition getSelectedDataModel() {
		DMDLTreeData data = getSelectionData();
		Object obj = data.getData();
		if (obj instanceof DataModelInfo) {
			return (DataModelInfo) obj;
		}
		if (obj instanceof DataModelProperty) {
			return (DataModelProperty) obj;
		}
		return null;
	}
}
