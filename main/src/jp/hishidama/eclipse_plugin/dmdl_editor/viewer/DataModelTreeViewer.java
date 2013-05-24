package jp.hishidama.eclipse_plugin.dmdl_editor.viewer;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

public class DataModelTreeViewer extends TreeViewer {

	public DataModelTreeViewer(Composite parent, int style) {
		super(parent, style);
		setLabelProvider(new DMDLLabelProvider());
	}

	public void setInputAll(IProject project) {
		setContentProvider(new DMDLTreeContentProvider());

		List<IFile> files = DataModelUtil.getDmdlFiles(project);
		List<DMDLTreeData> list = new ArrayList<DMDLTreeData>(files.size());
		for (IFile file : files) {
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
