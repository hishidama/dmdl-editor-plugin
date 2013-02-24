package jp.hishidama.eclipse_plugin.dmdl_editor.editors.form;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;

public abstract class DataModelPage {

	protected ModelToken model;

	protected Text descText;
	protected Text typeText;
	protected Text nameText;
	protected Table table;

	public final void setModel(ModelToken model) {
		this.model = model;
	}

	public void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		Composite body = form.getBody();
		body.setLayout(new GridLayout(1, false));
		body.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

		FormToolkit kit = managedForm.getToolkit();
		{
			Composite composite = kit.createComposite(body, SWT.NONE);
			composite.setLayout(new GridLayout(2, true));
			composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false,
					false));
			createHeader(kit, composite);
		}
		{
			Composite composite = kit.createComposite(body, SWT.NONE);
			composite.setLayout(new GridLayout(1, true));
			composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
					false));
			table = kit.createTable(composite, SWT.BORDER | SWT.FULL_SELECTION);
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			createTable(table);
		}

		refreshData();
	}

	protected void createHeader(FormToolkit kit, Composite parent) {
		GridData gd = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL);

		kit.createLabel(parent, "description");
		descText = kit.createText(parent, "");
		descText.setLayoutData(gd);
		kit.createLabel(parent, "model type");
		typeText = kit.createText(parent, "");
		typeText.setLayoutData(gd);
		kit.createLabel(parent, "model name");
		nameText = kit.createText(parent, "");
		nameText.setLayoutData(gd);

		createHeader2(kit, parent);
	}

	protected void createHeader2(FormToolkit kit, Composite parent) {
		// override
	}

	protected void createTable(Table table) {
		{
			TableColumn col = new TableColumn(table, SWT.LEFT);
			col.setText("description");
			col.setWidth(130);
		}
		{
			TableColumn col = new TableColumn(table, SWT.LEFT);
			col.setText("name");
			col.setWidth(140);
		}
		{
			TableColumn col = new TableColumn(table, SWT.LEFT);
			col.setText("type");
			col.setWidth(100);
		}
	}

	public void refreshData() {
		descText.setText(getModelDescription());
		typeText.setText(getModelType());
		nameText.setText(getModelName());

		table.clearAll();
		for (PropertyToken prop : getProperties()) {
			TableItem item = new TableItem(table, SWT.NONE);
			String[] ss = { decodeDescription(prop.getPropertyDescription()),
					prop.getPropertyName(), prop.getDataType(null) };
			item.setText(ss);
		}
	}

	protected String getModelName() {
		if (model != null) {
			return model.getModelName();
		}
		return "";
	}

	protected String getModelDescription() {
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

	protected String getModelType() {
		if (model != null) {
			return nonNull(model.getModelType());
		}
		return "";
	}

	protected static String nonNull(String s) {
		return (s != null) ? s : "";
	}

	protected List<PropertyToken> getProperties() {
		if (model != null) {
			return model.getPropertyList();
		}
		return Collections.emptyList();
	}
}
