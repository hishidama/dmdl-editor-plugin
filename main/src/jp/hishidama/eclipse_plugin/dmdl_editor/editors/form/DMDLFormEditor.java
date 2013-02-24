package jp.hishidama.eclipse_plugin.dmdl_editor.editors.form;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLTextEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

public class DMDLFormEditor extends FormPage {

	private DMDLTextEditor editor;

	public DMDLFormEditor(FormEditor formEditor, DMDLTextEditor editor) {
		super(formEditor, "DMDLFormEditor", editor.getTitle());
		this.editor = editor;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ModelToken model = getModel();

		DataModelPage page = new NormalPage();

		page.setModel(model);
		page.createFormContent(managedForm);
	}

	private ModelToken getModel() {
		DMDLDocument document = editor.getDocument();
		if (document == null) {
			return null;
		}
		ModelList models = document.getModelList();
		if (models == null) {
			return null;
		}

		ITextSelection selection = (ITextSelection) editor
				.getSelectionProvider().getSelection();
		if (selection == null) {
			return null;
		}

		int offset = selection.getOffset();
		ModelToken model = models.getModelByOffset(offset);

		return model;
	}
}
