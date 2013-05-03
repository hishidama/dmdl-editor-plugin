package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.form;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLTextEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;

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

	public void refreshData() {
		if (page == null) {
			return;
		}
		ModelToken model = findModel();
		if (model == null) {
			return;
		}
		page.setModel(model);
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

		// textが空文字列の場合は、削除という意味になる。
		// 削除文字列の前後が改行の場合は末尾の改行も一緒に削除する。
		int end = pos + length;
		if (text.isEmpty() && pos > 0 && end < document.getLength()) {
			char prev = 0, next = 0;
			try {
				prev = document.getChar(pos - 1);
				next = document.getChar(end);
			} catch (BadLocationException e) {
			}
			if ((prev == '\r' || prev == '\n')
					&& (next == '\r' || next == '\n')) {
				end++;
				if (next == '\r') {
					try {
						if (document.getChar(end) == '\n') {
							end++;
						}
					} catch (BadLocationException e) {
					}
				}
				length = end - pos;
			}
		}

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
