package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.update.AttributeUpdater;

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

public abstract class SetAttributePage extends WizardPage {
	private static final String SETTINGS_MODEL_ATTR = "AttributeWizard.modelAttribute";
	private static final String SETTINGS_PROP_ATTR = "AttributeWizard.propertyAttribute";

	protected static enum AttributeType {
		DIRECTIO_CSV, WINDGATE_CSV, WINDGATE_JDBC
	}

	private Text modelText;
	private Text propertyText;

	public SetAttributePage(String pageName) {
		super(pageName);

		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		Composite parent0 = new Composite(parent, SWT.NONE);
		{
			parent0.setLayout(new GridLayout(1, false));
			GridData grid = new GridData(GridData.FILL_HORIZONTAL);
			parent0.setLayoutData(grid);
		}
		Composite composite = new Composite(parent0, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData compositeGrid = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(compositeGrid);
		{
			createDefaultButtonField(composite);

			{
				Label label = new Label(composite, SWT.NONE);
				label.setText("モデルの属性");
				modelText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
				modelText.setText(nonNull(getSetting(SETTINGS_MODEL_ATTR)));
				GridData data = new GridData(GridData.FILL_HORIZONTAL);
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
				Label label = new Label(composite, SWT.NONE);
				label.setText("プロパティーの属性");
				propertyText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
				propertyText.setText(nonNull(getSetting(SETTINGS_PROP_ATTR)));
				GridData data = new GridData(GridData.FILL_HORIZONTAL);
				data.heightHint = 18 * 4;
				propertyText.setLayoutData(data);
				propertyText.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
						validate();
					}
				});
			}
		}
		{
			Group group = new Group(parent0, SWT.SHADOW_IN);
			group.setText("note");
			GridData grid = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(grid);
			createNoteArea(group);
		}
		validate();

		setControl(parent0);
	}

	private void createDefaultButtonField(Composite composite) {
		Label label = new Label(composite, SWT.NONE);
		label.setText("属性のデフォルトを設定");
		Composite field = new Composite(composite, SWT.NONE);
		field.setLayout(new FillLayout(SWT.HORIZONTAL));
		{
			Button button = new Button(field, SWT.PUSH);
			button.setText("directio.csv");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setDefaultAttribute(AttributeType.DIRECTIO_CSV);
				}
			});
		}
		{
			Button button = new Button(field, SWT.PUSH);
			button.setText("windgate.csv");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setDefaultAttribute(AttributeType.WINDGATE_CSV);
				}
			});
		}
		{
			Button button = new Button(field, SWT.PUSH);
			button.setText("windgate.jdbc");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setDefaultAttribute(AttributeType.WINDGATE_JDBC);
				}
			});
		}
	}

	protected abstract void createNoteArea(Group group);

	private static String nonNull(String s) {
		return (s != null) ? s : "";
	}

	private void setDefaultAttribute(AttributeType type) {
		modelText.setText(nonNull(getDefaultModelAttribute(type)));
		propertyText.setText(nonNull(getDefaultPropertyAttribute(type)));
	}

	protected abstract String getDefaultModelAttribute(AttributeType type);

	protected abstract String getDefaultPropertyAttribute(AttributeType type);

	private void validate() {
		boolean complete = !modelText.getText().trim().isEmpty() || !propertyText.getText().trim().isEmpty();
		setPageComplete(complete);
	}

	public String getModelAttribute() {
		String text = modelText.getText();
		setSetting(SETTINGS_MODEL_ATTR, text);
		return text;
	}

	public String getPropertyAttribute() {
		String text = propertyText.getText();
		setSetting(SETTINGS_PROP_ATTR, text);
		return text;
	}

	private String getSetting(String key) {
		IDialogSettings settings = getDialogSettings();
		return settings.get(getKey(key));
	}

	private void setSetting(String key, String value) {
		IDialogSettings settings = getDialogSettings();
		settings.put(getKey(key), value);
	}

	private String getKey(String key) {
		return String.format("%s.%s", key, getName());
	}

	public abstract AttributeUpdater getUpdater(SelectAddRemovePage selectPage);
}
