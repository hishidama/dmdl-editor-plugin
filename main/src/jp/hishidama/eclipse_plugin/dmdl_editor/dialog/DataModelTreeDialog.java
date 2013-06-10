package jp.hishidama.eclipse_plugin.dmdl_editor.dialog;

import jp.hishidama.eclipse_plugin.dialog.EditDialog;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelInfo;
import jp.hishidama.eclipse_plugin.dmdl_editor.viewer.DMDLTreeData;
import jp.hishidama.eclipse_plugin.dmdl_editor.viewer.DataModelTreeViewer;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

public abstract class DataModelTreeDialog extends EditDialog {
	private IProject project;
	private String name;
	protected DMDLTreeData treeData;

	private DataModelTreeViewer tree;

	public DataModelTreeDialog(Shell parentShell, IProject project, String windowTitle) {
		super(parentShell, windowTitle);
		this.project = project;
	}

	public void setInitialModel(String name) {
		this.name = name;
	}

	@Override
	protected void createFields(Composite composite) {
		tree = createDataModelTreeField(composite, "data model");
		tree.setInputAll(project);
		tree.expandToLevel(getInitialExpandLevel());
	}

	private DataModelTreeViewer createDataModelTreeField(Composite composite, String label) {
		createLabel(composite, label);

		DataModelTreeViewer viewer = createDataModelTree(composite);
		return viewer;
	}

	private DataModelTreeViewer createDataModelTree(Composite composite) {
		DataModelTreeViewer viewer = new DataModelTreeViewer(composite, SWT.BORDER | SWT.SINGLE);
		initializeTree(viewer.getTree());

		return viewer;
	}

	protected int getInitialExpandLevel() {
		return 1;
	}

	@Override
	protected void refresh() {
		for (TreeItem row : tree.getTree().getItems()) {
			for (TreeItem item : row.getItems()) {
				DMDLTreeData data = (DMDLTreeData) item.getData();
				if (data == null) {
					continue;
				}
				Object obj = data.getData();
				if (obj instanceof DataModelInfo) {
					DataModelInfo info = (DataModelInfo) obj;
					if (info.getModelName().equals(name)) {
						this.treeData = data;
						tree.getTree().setSelection(item);
						return;
					}
				}
			}
		}
	}

	@Override
	protected boolean validate() {
		TreeItem[] select = tree.getTree().getSelection();
		if (select.length != 1) {
			return false;
		}
		TreeItem item = select[0];
		DMDLTreeData data = (DMDLTreeData) item.getData();
		Object obj = data.getData();
		if (!validateData(obj)) {
			return false;
		}

		treeData = data;
		if (obj instanceof DataModelInfo) {
			DataModelInfo info = (DataModelInfo) obj;
			name = info.getModelName();
		}
		return true;
	}

	protected abstract boolean validateData(Object obj);

	protected final DMDLTreeData getSelectionData() {
		return treeData;
	}
}
