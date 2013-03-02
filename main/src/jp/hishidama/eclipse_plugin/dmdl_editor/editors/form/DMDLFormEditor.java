package jp.hishidama.eclipse_plugin.dmdl_editor.editors.form;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLTextEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

public class DMDLFormEditor extends FormPage {

	private DMDLTextEditor editor;
	private DataModelPage page;

	public DMDLFormEditor(FormEditor formEditor, DMDLTextEditor editor) {
		super(formEditor, "DMDLFormEditor", editor.getTitle());
		this.editor = editor;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ModelToken model = findModel();

		page = new NormalPage(this);

		page.setModel(model);
		page.createFormContent(managedForm);
	}

	private ModelToken findModel() {
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

	public void setModel(ModelToken newModel) {
		if (newModel == null) {
			return;
		}
		if (page == null) {
			return;
		}
		ModelToken oldModel = page.getModel();
		if (oldModel != null) {
			if (newModel.getModelName().equals(oldModel.getModelName())) {
				return;
			}
		}

		page.setModel(newModel);
		page.refreshData();
	}

	public void selectProperty(String propertyName) {
		if (page == null) {
			return;
		}
		page.selectProperty(propertyName);
	}

	public IProject getProject() {
		return editor.getProject();
	}

	public IFile getFile() {
		return editor.getFile();
	}

	public ModelToken replaceDocument(int pos, int length, String text) {
		DMDLDocument document = editor.getDocument();
		try {
			document.replace(pos, length, text);
		} catch (BadLocationException e) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
					"DMDLFormEditor#replace bad location.", e));
			return null;
		}
		ModelList models = document.getModelList();
		return models.getModelByOffset(pos);
	}
}
