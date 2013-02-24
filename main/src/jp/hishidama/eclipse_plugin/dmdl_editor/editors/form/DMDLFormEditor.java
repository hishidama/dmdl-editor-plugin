package jp.hishidama.eclipse_plugin.dmdl_editor.editors.form;

import java.util.Collections;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLTextEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class DMDLFormEditor extends FormPage {

	private DMDLTextEditor editor;
	private Text descText;
	private Text typeText;
	private Text nameText;

	public DMDLFormEditor(FormEditor formEditor, DMDLTextEditor editor) {
		super(formEditor, "DMDLFormEditor", editor.getTitle());
		this.editor = editor;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ModelToken model = getModel();

		FormToolkit kit = managedForm.getToolkit();

		ScrolledForm form = managedForm.getForm();
		Composite body = form.getBody();
		body.setLayout(new GridLayout(1, true));
		// body.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

		{
			Composite parent = kit.createComposite(body);
			parent.setLayout(new GridLayout(2, true));
			parent.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

			GridData gd = new GridData(GridData.VERTICAL_ALIGN_FILL
					| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
					| GridData.GRAB_VERTICAL);
			kit.createLabel(parent, "description");
			descText = kit.createText(parent, getModelDescription(model));
			descText.setLayoutData(gd);
			kit.createLabel(parent, "model type");
			typeText = kit.createText(parent, getModelType(model));
			typeText.setLayoutData(gd);
			kit.createLabel(parent, "model name");
			nameText = kit.createText(parent, getModelName(model));
			nameText.setLayoutData(gd);
		}
		{
			Table table = kit
					.createTable(body, SWT.BORDER | SWT.FULL_SELECTION);
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			{
				TableColumn col = new TableColumn(table, SWT.LEFT);
				col.setText("description");
				col.setWidth(120);
			}
			{
				TableColumn col = new TableColumn(table, SWT.LEFT);
				col.setText("name");
				col.setWidth(120);
			}
			{
				TableColumn col = new TableColumn(table, SWT.LEFT);
				col.setText("type");
				col.setWidth(80);
			}
			for (PropertyToken prop : getProperties(model)) {
				TableItem item = new TableItem(table, SWT.NONE);
				String[] ss = {
						decodeDescription(prop.getPropertyDescription()),
						prop.getPropertyName(), prop.getDataType(null) };
				item.setText(ss);
			}
		}
	}

	static class Data {
		@Override
		public String toString() {
			return "zzz";
		}
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

	protected String getModelName(ModelToken model) {
		if (model != null) {
			return model.getModelName();
		}
		return "";
	}

	protected String getModelDescription(ModelToken model) {
		if (model != null) {
			String desc = nonNull(model.getDescription());
			return decodeDescription(desc);
		}
		return "";
	}

	protected static String decodeDescription(String s) {
		if (s.startsWith("\"")) {
			s = s.substring(1);
		}
		if (s.endsWith("\"")) {
			s = s.substring(0, s.length() - 1);
		}
		return s;
	}

	protected String getModelType(ModelToken model) {
		if (model != null) {
			return nonNull(model.getModelType());
		}
		return "";
	}

	protected List<PropertyToken> getProperties(ModelToken model) {
		if (model != null) {
			return model.getPropertyList();
		}
		return Collections.emptyList();
	}

	protected static String nonNull(String s) {
		return (s != null) ? s : "";
	}
}
