package jp.hishidama.eclipse_plugin.dmdl_editor.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class DMDLEditor extends TextEditor {
	private ColorManager colorManager = new ColorManager();

	/**
	 * コンストラクター.
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
