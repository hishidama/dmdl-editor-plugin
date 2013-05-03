package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.outline;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.DMDLMultiPageEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.form.DMDLFormEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLTextEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public class DMDLOutlineSelectionChangedListener implements
		ISelectionChangedListener {
	protected DMDLMultiPageEditor multiEditor;

	public DMDLOutlineSelectionChangedListener(DMDLMultiPageEditor editor) {
		this.multiEditor = editor;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		{
			DMDLTextEditor editor = multiEditor.getTextEditor();
			selectChangedTextEditor(event, editor);
		}
		{
			DMDLFormEditor editor = multiEditor.getFormEditor();
			selectChangedFormEditor(event, editor);
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

	protected void selectChangedFormEditor(SelectionChangedEvent event,
			DMDLFormEditor editor) {
		IStructuredSelection selection = (IStructuredSelection) event
				.getSelection();
		Object element = selection.getFirstElement();
		selectByEditor(editor, element);
	}

	protected void selectByEditor(DMDLFormEditor editor, Object element) {
		if (element instanceof ModelToken) {
			ModelToken model = (ModelToken) element;
			editor.setModel(model);
		} else if (element instanceof PropertyToken) {
			PropertyToken prop = (PropertyToken) element;
			ModelToken model = prop.getModelToken();
			editor.setModel(model);
			editor.selectProperty(prop.getPropertyName());
		}
	}
}
