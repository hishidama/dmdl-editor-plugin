package jp.hishidama.eclipse_plugin.dmdl_editor.viewer;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelFile;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;

public class DataModelTreeViewer extends TreeViewer {

	public DataModelTreeViewer(Composite parent, int style) {
		this(parent, style, false);
	}

	public DataModelTreeViewer(Composite parent, int style, boolean enableDrag) {
		super(parent, style);
		setLabelProvider(new DMDLLabelProvider());

		if (enableDrag) {
			int operations = DND.DROP_COPY | DND.DROP_DEFAULT;
			Transfer[] transferTypes = { DMDLTreeDataTransfer.getInstance() };
			addDragSupport(operations, transferTypes, new DMDLTreeDataDragListener(this));
		}
	}

	public void setInputAll(IProject project) {
		setContentProvider(new DMDLTreeContentProvider());

		List<DataModelFile> files = DataModelUtil.getDmdlFiles(project);
		List<DMDLTreeData> list = new ArrayList<DMDLTreeData>(files.size());
		for (DataModelFile file : files) {
			list.add(new DMDLTreeData.File(file));
		}
		setInput(list);
	}

	public void setInputList(List<DMDLTreeData> list) {
		setContentProvider(new DMDLTreeContentProvider());
		setInput(list);
	}

	@Override
	public ITreeSelection getSelection() {
		return (ITreeSelection) super.getSelection();
	}
}
