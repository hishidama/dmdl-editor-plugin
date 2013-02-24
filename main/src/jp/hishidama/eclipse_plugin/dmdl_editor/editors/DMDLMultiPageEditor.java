package jp.hishidama.eclipse_plugin.dmdl_editor.editors;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLEditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

public class DMDLMultiPageEditor extends MultiPageEditorPart {

	private DMDLEditor editor;

	@Override
	protected void createPages() {
		editor = new DMDLEditor();
		try {
			int n = addPage(new DMDLEditor(), getEditorInput());
			setPageText(n, "editor");
		} catch (PartInitException e) {
			throw new IllegalStateException(e);
		}
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
