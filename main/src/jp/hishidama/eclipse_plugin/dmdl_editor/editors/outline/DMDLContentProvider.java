package jp.hishidama.eclipse_plugin.dmdl_editor.editors.outline;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class DMDLContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		RootData root = (RootData) inputElement;
		if (root.element == null) {
			return new Object[] {};
		}

		return root.element.getNamedModelList().toArray();
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ModelToken) {
			ModelToken model = (ModelToken) element;
			List<PropertyToken> list = model.getPropertyList();
			return !list.isEmpty();
		}
		return false;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ModelToken) {
			ModelToken model = (ModelToken) parentElement;
			List<PropertyToken> list = model.getPropertyList();
			return list.toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public void dispose() {
	}
}
