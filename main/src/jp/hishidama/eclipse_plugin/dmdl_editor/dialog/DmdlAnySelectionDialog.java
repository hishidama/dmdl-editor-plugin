package jp.hishidama.eclipse_plugin.dmdl_editor.dialog;

import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelPosition;
import jp.hishidama.eclipse_plugin.dmdl_editor.viewer.DMDLTreeData;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Shell;

public class DmdlAnySelectionDialog extends DataModelTreeDialog {

	public DmdlAnySelectionDialog(Shell parentShell, IProject project) {
		this(parentShell, project, "データモデル選択");
	}

	public DmdlAnySelectionDialog(Shell parentShell, IProject project, String windowTitle) {
		super(parentShell, project, windowTitle);
	}

	@Override
	protected int getInitialExpandLevel() {
		return 1;
	}

	@Override
	protected boolean validateData(Object obj) {
		return true;
	}

	public DataModelPosition getSelectedDataModel() {
		DMDLTreeData data = getSelectionData();
		Object obj = data.getData();
		if (obj instanceof DataModelPosition) {
			return (DataModelPosition) obj;
		}
		return null;
	}
}
