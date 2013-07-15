package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLImporterExporterDefinition;
import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DmdlCompilerProperties;

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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SetImporterExporterNamePage extends WizardPage {
	private static final String SETTINGS_SRC = "SetImporterExporterNamePage.src";
	private static final String SETTINGS_PACKAGE = "SetImporterExporterNamePage.package";

	private List<DMDLImporterExporterDefinition> defList;

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

	public void setDefinitions(List<DMDLImporterExporterDefinition> defList) {
		this.defList = defList;
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData compositeGrid = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(compositeGrid);

		{
			Label label = new Label(composite, SWT.NONE);
			label.setText("生成先ソースディレクトリー");

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

		for (DMDLImporterExporterDefinition def : defList) {
			createField(composite, def);
		}

		{
			Group group = new Group(composite, SWT.NONE);
			group.setText("例");
			GridData grid = new GridData(GridData.FILL_HORIZONTAL);
			grid.horizontalSpan = 2;
			group.setLayoutData(grid);
			group.setLayout(new GridLayout(1, false));

			Label label = new Label(group, SWT.NONE);
			label.setText("対象となるデータモデル（前のページで指定したデータモデル）が「word_count」、\n"
					+ "「パッケージ名」が「com.example」、「@directio.csv Importer」が「job.$(modelName.toCamelCase)FromCsv」のとき、\n"
					+ "「com.example.job.WordCountFromCsv」というクラスが生成されます。\n"
					+ "親クラスは、build.proerptiesの「asakusa.modelgen.package」で指定されているパッケージが「com.modelgen」だとすると\n"
					+ "「com.modelgen.dmdl.csv.AbstractWordCountCsvInputDescription」となります。（このクラスが無いと、生成されたソースはコンパイルエラーになります）");
		}

		validate(false);
		setControl(composite);
	}

	private void createField(Composite composite, DMDLImporterExporterDefinition def) {
		Field f = new Field();
		f.def = def;

		f.check = new Button(composite, SWT.CHECK);
		f.check.setText(def.getDisplayName());
		f.check.setSelection(getSettingBoolean(def));
		f.check.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate(true);
			}
		});

		f.text = new Text(composite, SWT.BORDER);
		f.text.setText(nonNull(getSetting(def, def.getDefaultClassName())));
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		f.text.setLayoutData(grid);
		f.text.addModifyListener(listener);

		fieldList.add(f);
	}

	private static class Field {
		public DMDLImporterExporterDefinition def;
		public Button check;
		public Text text;
	}

	private void validate(boolean setError) {
		setPageComplete(false);
		if (srcText.getText().trim().isEmpty()) {
			if (setError) {
				setErrorMessage("生成先ソースディレクトリーを入力して下さい。");
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

	public void setProperties(DmdlCompilerProperties properties) {
		if (properties != null && packageText.getText().isEmpty()) {
			String s = getSetting(SETTINGS_PACKAGE, null);
			if (s == null) {
				s = properties.getPackageDefault();
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

	public Map<DMDLImporterExporterDefinition, String> getClassName() {
		Map<DMDLImporterExporterDefinition, String> map = new HashMap<DMDLImporterExporterDefinition, String>();
		for (Field f : fieldList) {
			boolean check = f.check.getSelection();
			String value = f.text.getText().trim();

			setSetting(f.def, check);
			setSetting(f.def, value);

			if (check) {
				map.put(f.def, value);
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

	private boolean getSettingBoolean(DMDLImporterExporterDefinition def) {
		IDialogSettings settings = getDialogSettings();
		return settings.getBoolean(getKey(def, "checked"));
	}

	private void setSetting(DMDLImporterExporterDefinition def, boolean value) {
		IDialogSettings settings = getDialogSettings();
		settings.put(getKey(def, "checked"), value);
	}

	private String getSetting(DMDLImporterExporterDefinition def, String defalutValue) {
		IDialogSettings settings = getDialogSettings();
		String value = settings.get(getKey(def, "text"));
		return (value != null) ? value : defalutValue;
	}

	private void setSetting(DMDLImporterExporterDefinition def, String value) {
		IDialogSettings settings = getDialogSettings();
		settings.put(getKey(def, "text"), value);
	}

	private String getKey(DMDLImporterExporterDefinition def, String suffix) {
		return String.format("SetImporterExporterNamePage.%s.%s", def.getName(), suffix);
	}

	protected static String nonNull(String s) {
		return (s != null) ? s : "";
	}
}
