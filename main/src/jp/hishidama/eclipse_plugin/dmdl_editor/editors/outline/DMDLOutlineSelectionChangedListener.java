package jp.hishidama.eclipse_plugin.dmdl_editor.editors.outline;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLMultiPageEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLTextEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorPart;

public class DMDLOutlineSelectionChangedListener implements
		ISelectionChangedListener {
	protected DMDLMultiPageEditor editor;

	public DMDLOutlineSelectionChangedListener(DMDLMultiPageEditor editor) {
		this.editor = editor;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		IEditorPart active = editor.getActiveEditor();
		if (active instanceof DMDLTextEditor) {
			selectChangedTextEditor(event, (DMDLTextEditor) active);
		}
	}

	protected void selectChangedTextEditor(SelectionChangedEvent event,
			DMDLTextEditor editor) {
		if (editor.inSelect()) {
			return;
		}

		IStructuredSelection selection = (IStructuredSelection) event
				.getSelection();
		Object element = selection.getFirstElement();
		selectByEditor(editor, element);
	}

	protected void selectByEditor(DMDLTextEditor editor, Object element) {
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
