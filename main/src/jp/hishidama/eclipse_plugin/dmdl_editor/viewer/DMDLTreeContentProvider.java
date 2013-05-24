package jp.hishidama.eclipse_plugin.dmdl_editor.viewer;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class DMDLTreeContentProvider implements ITreeContentProvider {

	public DMDLTreeContentProvider() {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<?> list = (List<?>) inputElement;
		return toArray(list);
	}

	@Override
	public boolean hasChildren(Object element) {
		DMDLTreeData data = (DMDLTreeData) element;
		return data.hasChildren();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		DMDLTreeData data = (DMDLTreeData) parentElement;
		List<DMDLTreeData> list = data.getChildren();
		return toArray(list);
	}

	protected static Object[] toArray(List<?> list) {
		return (list != null) ? list.toArray() : null;
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