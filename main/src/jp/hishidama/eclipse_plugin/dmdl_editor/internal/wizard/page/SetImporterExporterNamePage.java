package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.BuildPropertiesUtil;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SetImporterExporterNamePage extends WizardPage {
	private static final String SETTINGS_SRC = "SetImporterExporterNamePage.src";
	private static final String SETTINGS_PACKAGE = "SetImporterExporterNamePage.package";

	private Text srcText;
	private Text packageText;
	private List<Field> fieldList = new ArrayList<Field>();
	private ModifyListener listener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			validate(true);
		}
	};

	public SetImporterExporterNamePage() {
		super("SetImporterExporterNamePage");
		setTitle("Importer/Exporterクラス名の指定");
		setDescription("作成するImporter/Exporterの種類を選択し、生成するクラス名を入力して下さい。");
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData compositeGrid = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(compositeGrid);

		{
			Label label = new Label(composite, SWT.NONE);
			label.setText("ソースディレクトリー");

			srcText = new Text(composite, SWT.BORDER);
			GridData grid = new GridData(GridData.FILL_HORIZONTAL);
			srcText.setLayoutData(grid);
			srcText.setText(getSetting(SETTINGS_SRC, "src/main/java"));
			srcText.addModifyListener(listener);
		}
		{
			Label label = new Label(composite, SWT.NONE);
			label.setText("パッケージ名");

			packageText = new Text(composite, SWT.BORDER);
			GridData grid = new GridData(GridData.FILL_HORIZONTAL);
			packageText.setLayoutData(grid);
			packageText.setText(getSetting(SETTINGS_PACKAGE, ""));
			packageText.addModifyListener(listener);
		}

		createField(composite, ImporterExporterType.DIRECTIO_CSV_IMPORTER, "$(modelName.toCamelCase)FromCsv");
		createField(composite, ImporterExporterType.DIRECTIO_CSV_EXPORTER, "$(modelName.toCamelCase)ToCsv");
		createField(composite, ImporterExporterType.WINDGATE_CSV_IMPORTER, "$(modelName.toCamelCase)FromCsv");
		createField(composite, ImporterExporterType.WINDGATE_CSV_EXPORTER, "$(modelName.toCamelCase)ToCsv");
		createField(composite, ImporterExporterType.WINDGATE_JDBC_IMPORTER, "$(modelName.toCamelCase)FromTable");
		createField(composite, ImporterExporterType.WINDGATE_JDBC_EXPORTER, "$(modelName.toCamelCase)ToTable");

		validate(false);
		setControl(composite);
	}

	private void createField(Composite composite, ImporterExporterType type, String defaultValue) {
		Field f = new Field();
		f.type = type;

		f.check = new Button(composite, SWT.CHECK);
		f.check.setText(type.displayName());
		f.check.setSelection(getSettingBoolean(type));
		f.check.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate(true);
			}
		});

		f.text = new Text(composite, SWT.BORDER);
		f.text.setText(nonNull(getSetting(type, defaultValue)));
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		f.text.setLayoutData(grid);
		f.text.addModifyListener(listener);

		fieldList.add(f);
	}

	private static class Field {
		public ImporterExporterType type;
		public Button check;
		public Text text;
	}

	private void validate(boolean setError) {
		setPageComplete(false);
		if (srcText.getText().trim().isEmpty()) {
			if (setError) {
				setErrorMessage("ソースディレクトリーを入力して下さい。");
			}
			return;
		}
		if (packageText.getText().trim().isEmpty()) {
			if (setError) {
				setErrorMessage("パッケージ名を入力して下さい。");
			}
			return;
		}
		int checked = 0;
		for (Field f : fieldList) {
			boolean check = f.check.getSelection();
			if (check) {
				checked++;
				String value = f.text.getText().trim();
				if (value.isEmpty()) {
					if (setError) {
						setErrorMessage("選択した種類のクラス名を入力して下さい。");
					}
					return;
				}
			}
		}
		if (checked == 0) {
			if (setError) {
				setErrorMessage("Importer/Exporterの種類を選択して下さい。");
			}
			return;
		}

		setErrorMessage(null);
		setPageComplete(true);
	}

	public void setProperties(Properties properties) {
		if (properties != null && packageText.getText().isEmpty()) {
			String s = getSetting(SETTINGS_PACKAGE, null);
			if (s == null) {
				s = BuildPropertiesUtil.getPackageDefault(properties);
				packageText.setText(nonNull(s));
			}
		}
	}

	public String getSrcDirectory() {
		String value = srcText.getText().trim();
		setSetting(SETTINGS_SRC, value);
		return value;
	}

	public String getPackageName() {
		String value = packageText.getText().trim();
		setSetting(SETTINGS_PACKAGE, value);
		return value;
	}

	public Map<ImporterExporterType, String> getClassName() {
		Map<ImporterExporterType, String> map = new EnumMap<ImporterExporterType, String>(ImporterExporterType.class);
		for (Field f : fieldList) {
			boolean check = f.check.getSelection();
			String value = f.text.getText().trim();

			setSetting(f.type, check);
			setSetting(f.type, value);

			if (check) {
				map.put(f.type, value);
			}
		}
		return map;
	}

	// DialogSettings
	private String getSetting(String key, String defalutValue) {
		IDialogSettings settings = getDialogSettings();
		String value = settings.get(key);
		return (value != null) ? value : defalutValue;
	}

	private void setSetting(String key, String value) {
		IDialogSettings settings = getDialogSettings();
		settings.put(key, value);
	}

	private boolean getSettingBoolean(ImporterExporterType type) {
		IDialogSettings settings = getDialogSettings();
		return settings.getBoolean(getKey(type, "checked"));
	}

	private void setSetting(ImporterExporterType type, boolean value) {
		IDialogSettings settings = getDialogSettings();
		settings.put(getKey(type, "checked"), value);
	}

	private String getSetting(ImporterExporterType type, String defalutValue) {
		IDialogSettings settings = getDialogSettings();
		String value = settings.get(getKey(type, "text"));
		return (value != null) ? value : defalutValue;
	}

	private void setSetting(ImporterExporterType type, String value) {
		IDialogSettings settings = getDialogSettings();
		settings.put(getKey(type, "text"), value);
	}

	private String getKey(ImporterExporterType type, String suffix) {
		return String.format("SetImporterExporterNamePage.%s.%s", type.name(), suffix);
	}

	protected static String nonNull(String s) {
		return (s != null) ? s : "";
	}
}
