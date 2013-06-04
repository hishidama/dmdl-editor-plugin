package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.form;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index.IndexContainer;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelUtil;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
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

public abstract class DataModelPage {
	protected DMDLFormEditor editor;

	protected ModelToken model;

	protected Text descText;
	protected Text typeText;
	protected Text nameText;
	protected Table table;

	private ModelDescriptionListener firstFocus;

	public DataModelPage(DMDLFormEditor editor) {
		this.editor = editor;
	}

	public final void setModel(ModelToken model) {
		this.model = model;
	}

	public final ModelToken getModel() {
		return model;
	}

	public void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		Composite body = form.getBody();
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
		body.setLayout(new FormLayout());

		FormToolkit kit = managedForm.getToolkit();

		Composite headerArea = kit.createComposite(body, SWT.NONE);
		{
			FormData data = new FormData();
			data.left = new FormAttachment(0, 8);
			data.right = new FormAttachment(100, -80);
			data.top = new FormAttachment(0, 8);
			headerArea.setLayoutData(data);

			headerArea.setLayout(new GridLayout(2, false));
			createHeader(kit, headerArea);
		}

		Composite tableArea = kit.createComposite(body, SWT.NONE);
		{
			{
				FormData data = new FormData();
				data.top = new FormAttachment(headerArea, 8);
				data.left = new FormAttachment(0, 8);
				data.right = new FormAttachment(100, -8);
				data.bottom = new FormAttachment(100, -8);
				tableArea.setLayoutData(data);
			}

			tableArea.setLayout(new FormLayout());
			table = kit.createTable(tableArea, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL);
			{
				FormData data = new FormData();
				data.top = new FormAttachment(0, 0);
				data.left = new FormAttachment(0, 0);
				data.right = new FormAttachment(100, -0);
				data.bottom = new FormAttachment(100, -0);
				data.height = 32; // 最小の高さ
				table.setLayoutData(data);
			}
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			createTable(table);
		}

		refreshData();
	}

	protected void createHeader(FormToolkit kit, Composite parent) {
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);

		kit.createLabel(parent, "description");
		descText = kit.createText(parent, "");
		descText.setLayoutData(gd);
		firstFocus = new ModelDescriptionListener(this);
		descText.addFocusListener(firstFocus);
		kit.createLabel(parent, "model type");
		typeText = kit.createText(parent, "");
		typeText.setLayoutData(gd);
		kit.createLabel(parent, "model name");
		nameText = kit.createText(parent, "");
		nameText.setLayoutData(gd);
		nameText.addFocusListener(new ModelNameListener(this));

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
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				Point point = new Point(e.x, e.y);

				Table table = (Table) e.getSource();
				TableItem item = table.getItem(point);
				if (item == null) {
					return;
				}
				for (int i = 0; i < table.getColumnCount(); i++) {
					if (item.getBounds(i).contains(point)) {
						int row = table.indexOf(item);
						ModelTableEditor te = new ModelTableEditor(DataModelPage.this, table);
						if (te.isEditable(i)) {
							te.setEditor(row, i, 0);
						}
						break;
					}
				}
			}
		});
	}

	public void refreshData() {
		String desc = getModelDescription();
		firstFocus.setOldValue(desc);
		descText.setText(desc);
		typeText.setText(getModelType());
		nameText.setText(getModelName());

		IndexContainer ic = IndexContainer.getContainer(editor.getProject(), new ProgressMonitorDialog(null));

		table.removeAll();
		for (PropertyToken prop : getProperties()) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, decodeDescription(nonNull(prop.getPropertyDescription())));
			item.setText(1, prop.getPropertyName());
			item.setText(2, nonNull(prop.getDataType(ic)));
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

	public static String decodeDescription(String s) {
		return DataModelUtil.decodeDescription(s);
	}

	public static String encodeDescription(String s) {
		return DataModelUtil.encodeDescription(s);
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

	public void selectProperty(String propertyName) {
		List<PropertyToken> list = getProperties();
		int i = 0;
		for (PropertyToken prop : list) {
			if (propertyName.equals(prop.getPropertyName())) {
				table.setSelection(i);
				table.showSelection();
				break;
			}
			i++;
		}
	}

	public void replaceDocument(int pos, int length, String text) {
		ModelToken m = editor.replaceDocument(pos, length, text);
		if (m == null) {
			throw new IllegalStateException(MessageFormat.format(
					"replace error. pos={0}, len={1}, text=\"{2}\", model={3}", pos, length, text, model.toString()));
		}
		setModel(m);
	}
}
