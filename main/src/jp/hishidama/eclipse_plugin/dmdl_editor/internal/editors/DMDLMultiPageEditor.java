package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.form.DMDLFormEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.outline.DMDLOutlinePage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLTextEditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

public class DMDLMultiPageEditor extends FormEditor {

	private DMDLTextEditor editor;
	private DMDLFormEditor form;
	private DMDLOutlinePage outlinePage;

	public DMDLMultiPageEditor() {
	}

	@Override
	protected void addPages() {
		editor = new DMDLTextEditor();
		outlinePage = new DMDLOutlinePage(this);
		editor.setOutlinePage(outlinePage);
		form = new DMDLFormEditor(this, editor);
		try {
			{
				int n = addPage(editor, getEditorInput());
				setPageText(n, "editor");
			}
			{
				int n = addPage(form);
				setPageText(n, "table");
			}
		} catch (PartInitException e) {
			throw new IllegalStateException(e);
		}
		setPartName(editor.getTitle());
	}

	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);

		Object page = getSelectedPage();
		if (page instanceof DMDLFormEditor) {
			DMDLFormEditor editor = (DMDLFormEditor) page;
			editor.refreshData();
		}
	}

	public DMDLTextEditor getTextEditor() {
		return editor;
	}

	public DMDLFormEditor getFormEditor() {
		return form;
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

	public IProject getProject() {
		return editor.getProject();
	}

	public IFile getFile() {
		return editor.getFile();
	}

	public DMDLDocument getDocument() {
		return editor.getDocument();
	}

	public void refresh() {
		editor.refresh();
	}
}
