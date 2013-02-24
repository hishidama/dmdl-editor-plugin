package jp.hishidama.eclipse_plugin.dmdl_editor.editors;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.form.DMDLFormEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLTextEditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

public class DMDLMultiPageEditor extends FormEditor {

	private DMDLTextEditor editor;

	@Override
	protected void addPages() {
		editor = new DMDLTextEditor();
		try {
			{
				int n = addPage(editor, getEditorInput());
				setPageText(n, "editor");
			}
			{
				int n = addPage(new DMDLFormEditor(this, editor));
				setPageText(n, "table");
			}
		} catch (PartInitException e) {
			throw new IllegalStateException(e);
		}
		setPartName(editor.getTitle());
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		editor.doSave(monitor);
	}

	@Override
	public void doSaveAs() {
		editor.doSaveAs();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return editor.isSaveAsAllowed();
	}
}
