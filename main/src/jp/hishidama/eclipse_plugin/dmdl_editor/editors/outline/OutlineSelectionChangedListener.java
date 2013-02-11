package jp.hishidama.eclipse_plugin.dmdl_editor.editors.outline;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public class OutlineSelectionChangedListener implements
		ISelectionChangedListener {
	protected DMDLEditor editor;

	public OutlineSelectionChangedListener(DMDLEditor editor) {
		this.editor = editor;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (editor.inSelect()) {
			return;
		}

		IStructuredSelection sel = (IStructuredSelection) event.getSelection();
		Object element = sel.getFirstElement();
		DMDLToken token;
		if (element instanceof ModelToken) {
			ModelToken model = (ModelToken) element;
			token = model.getModelNameToken();
		} else if (element instanceof PropertyToken) {
			PropertyToken prop = (PropertyToken) element;
			token = prop.getPropertyNameToken();
		} else {
			token = (DMDLToken) element;
		}
		if (token != null) {
			int offset = token.getStart();
			int length = token.getLength();
			editor.selectAndReveal(offset, length);
		}
	}
}
