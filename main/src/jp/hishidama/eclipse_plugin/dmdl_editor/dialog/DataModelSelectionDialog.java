package jp.hishidama.eclipse_plugin.dmdl_editor.dialog;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import jp.hishidama.eclipse_plugin.dialog.EditDialog;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelInfo;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class DataModelSelectionDialog extends EditDialog {
	private IProject project;
	private String name;
	private String description;
	private String className;

	private Tree tree;

	public DataModelSelectionDialog(Shell parentShell, IProject project) {
		super(parentShell, "データモデル選択");
		this.project = project;
	}

	public void setInitialModel(String name) {
		this.name = name;
	}

	@Override
	protected void createFields(Composite composite) {
		tree = createTreeField(composite, "data model");

		TreeMap<String, List<DataModelInfo>> map = new TreeMap<String, List<DataModelInfo>>();
		List<DataModelInfo> list = DataModelUtil.getModels(project);
		if (list != null) {
			for (DataModelInfo info : list) {
				String file = info.getFile().getProjectRelativePath().toPortableString();
				List<DataModelInfo> group = map.get(file);
				if (group == null) {
					group = new ArrayList<DataModelInfo>();
					map.put(file, group);
				}
				group.add(info);
			}
		}
		for (Entry<String, List<DataModelInfo>> entry : map.entrySet()) {
			String file = entry.getKey();
			TreeItem row = new TreeItem(tree, SWT.NONE);
			row.setText(file);
			row.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE));

			for (DataModelInfo info : entry.getValue()) {
				String name = info.getName();
				String desc = info.getDescription();
				String title = (desc != null) ? MessageFormat.format("{0} : {1}", name, desc) : name;
				TreeItem item = new TreeItem(row, SWT.NONE);
				item.setText(title);
				item.setData(info);
				item.setImage(JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CLASS));
			}

			row.setExpanded(true);
		}
	}

	@Override
	protected void refresh() {
		for (TreeItem row : tree.getItems()) {
			for (TreeItem item : row.getItems()) {
				DataModelInfo info = (DataModelInfo) item.getData();
				if (info.getName().equals(name)) {
					description = info.getDescription();
					className = DataModelUtil.getModelClass(project, name);
					tree.setSelection(item);
					return;
				}
			}
		}
	}

	@Override
	protected boolean validate() {
		TreeItem[] select = tree.getSelection();
		if (select.length != 1) {
			return false;
		}
		TreeItem item = select[0];
		DataModelInfo info = (DataModelInfo) item.getData();
		if (info != null) {
			name = info.getName();
			description = info.getDescription();
			className = DataModelUtil.getModelClass(project, name);
			return true;
		} else {
			return false;
		}
	}

	public String getModelName() {
		return name;
	}

	public String getModelDescription() {
		return description;
	}

	public String getModelClassName() {
		return className;
	}
}
