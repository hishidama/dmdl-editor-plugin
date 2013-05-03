package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SetAttributePage extends WizardPage {
	private static final String SETTINGS_REMOVE = "AttributeWizard.add/remove";
	private static final String SETTINGS_MODEL_ATTR = "AttributeWizard.modelAttribute";
	private static final String SETTINGS_PROP_ATTR = "AttributeWizard.propertyAttribute";

	private Text modelText;
	private Text propertyText;
	private Button addButton;

	public SetAttributePage() {
		super("SetAttributePage");
		setTitle("属性の更新内容の指定");

		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		setDescription("属性の更新内容を指定して下さい。");

		IDialogSettings settings = getDialogSettings();

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		GridData compositeGrid = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(compositeGrid);
		{
			Group group = new Group(composite, SWT.SHADOW_IN);
			group.setText("属性を追加するのか削除するのか");
			group.setLayout(new FillLayout(SWT.HORIZONTAL));
			addButton = new Button(group, SWT.RADIO);
			addButton.setText("追加");
			Button delButton = new Button(group, SWT.RADIO);
			delButton.setText("削除");
			boolean add = !settings.getBoolean(SETTINGS_REMOVE);
			if (add) {
				addButton.setSelection(true);
			} else {
				delButton.setSelection(true);
			}
		}
		{
			Group group = new Group(composite, SWT.SHADOW_IN);
			group.setText("属性名");
			group.setLayout(new GridLayout(1, false));
			Composite groupComposite = new Composite(group, SWT.NONE);
			groupComposite.setLayout(new GridLayout(2, false));
			{
				Label label = new Label(groupComposite, SWT.NONE);
				label.setText("モデル名の属性");
				modelText = new Text(groupComposite, SWT.BORDER | SWT.MULTI
						| SWT.V_SCROLL);
				modelText.setText(nonNull(settings.get(SETTINGS_MODEL_ATTR)));
				GridData data = new GridData(GridData.FILL_HORIZONTAL);
				data.widthHint = 512;
				data.heightHint = 18 * 9;
				modelText.setLayoutData(data);
				modelText.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
						validate();
					}
				});
			}
			{
				Label label = new Label(groupComposite, SWT.NONE);
				label.setText("プロパティー名の属性");
				propertyText = new Text(groupComposite, SWT.BORDER | SWT.MULTI
						| SWT.V_SCROLL);
				propertyText.setText(nonNull(settings.get(SETTINGS_PROP_ATTR)));
				GridData data = new GridData(GridData.FILL_HORIZONTAL);
				data.widthHint = 512;
				data.heightHint = 18 * 4;
				propertyText.setLayoutData(data);
				propertyText.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
						validate();
					}
				});

			}
			{
				Label label = new Label(groupComposite, SWT.NONE);
				label.setText("属性のデフォルトを設定");
				Composite field = new Composite(groupComposite, SWT.NONE);
				field.setLayout(new FillLayout(SWT.HORIZONTAL));
				{
					Button button = new Button(field, SWT.PUSH);
					button.setText("directio.csv");
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							if (isAdd()) {
								String s = "@directio.csv(\n"
										+ "  charset = \"UTF-8\",\n"
										+ "  allow_linefeed = FALSE,\n"
										+ "  has_header = FALSE,\n"
										+ "  true = \"true\",\n"
										+ "  false = \"false\",\n"
										+ "  date = \"yyyy-MM-dd\",\n"
										+ "  datetime = \"yyyy-MM-dd HH:mm:ss\",\n"
										+ ")";
								modelText.setText(s);
								propertyText
										.setText("@directio.csv.field(name = \"${name}\")");
							} else {
								modelText.setText("@directio.csv");
								String s = "@directio.csv.field\n"
										+ "@directio.csv.file_name\n"
										+ "@directio.csv.line_number\n"
										+ "@directio.csv.record_number";
								propertyText.setText(s);
							}
						}
					});
				}
				{
					Button button = new Button(field, SWT.PUSH);
					button.setText("windgate.csv");
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							if (isAdd()) {
								String s = "@windgate.csv(\n"
										+ "  charset = \"UTF-8\",\n"
										+ "  has_header = FALSE,\n"
										+ "  true = \"true\",\n"
										+ "  false = \"false\",\n"
										+ "  date = \"yyyy-MM-dd\",\n"
										+ "  datetime = \"yyyy-MM-dd HH:mm:ss\",\n"
										+ ")";
								modelText.setText(s);
								propertyText
										.setText("@windgate.csv.field(name = \"${name}\")");
							} else {
								modelText.setText("@windgate.csv");
								String s = "@windgate.csv.field\n"
										+ "@windgate.csv.file_name\n"
										+ "@windgate.csv.line_number\n"
										+ "@windgate.csv.record_number";
								propertyText.setText(s);
							}
						}
					});
				}
				{
					Button button = new Button(field, SWT.PUSH);
					button.setText("windgate.jdbc");
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							if (isAdd()) {
								String s = "@windgate.jdbc.table(name =\"${modelName.toUpper}\")";
								modelText.setText(s);
								propertyText
										.setText("@windgate.jdbc.column(name = \"${name.toUpper}\")");
							} else {
								modelText.setText("@windgate.jdbc.table");
								propertyText.setText("@windgate.jdbc.column");
							}
						}
					});
				}
			}
		}
		validate();

		setControl(composite);
	}

	private static String nonNull(String s) {
		return (s != null) ? s : "";
	}

	private void validate() {
		boolean complete = !modelText.getText().trim().isEmpty()
				|| !propertyText.getText().trim().isEmpty();
		setPageComplete(complete);
	}

	public boolean isAdd() {
		boolean add = addButton.getSelection();
		IDialogSettings settings = getDialogSettings();
		settings.put(SETTINGS_REMOVE, !add);
		return add;
	}

	public String getModelAttribute() {
		String text = modelText.getText();
		IDialogSettings settings = getDialogSettings();
		settings.put(SETTINGS_MODEL_ATTR, text);
		return text;
	}

	public String getPropertyAttribute() {
		String text = propertyText.getText();
		IDialogSettings settings = getDialogSettings();
		settings.put(SETTINGS_PROP_ATTR, text);
		return text;
	}
}
