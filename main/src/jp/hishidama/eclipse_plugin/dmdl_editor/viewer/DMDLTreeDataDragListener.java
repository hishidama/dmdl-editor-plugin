package jp.hishidama.eclipse_plugin.dmdl_editor.viewer;

import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;

public class DMDLTreeDataDragListener extends DragSourceAdapter {

	private DataModelTreeViewer viewer;

	public DMDLTreeDataDragListener(DataModelTreeViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		ITreeSelection selection = viewer.getSelection();
		event.data = selection;
	}
}
