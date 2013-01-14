package jp.hishidama.eclipse_plugin.dmdl_editor.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class DMDLEditor extends TextEditor {
	private ColorManager colorManager = new ColorManager();

	/**
	 * �R���X�g���N�^�[.
	 */
	public DMDLEditor() {
		setDocumentProvider(new DMDLDocumentProvider());
		setSourceViewerConfiguration(new DMDLConfiguration(colorManager));
	}

	@Override
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
}
