package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class SetImporterExporterMethodPage extends WizardPage {
	private static final String KEY_BASEPATH = "basePath";
	private static final String KEY_RESOUCE_PATTERN = "resourcePattern";
	private static final String KEY_ORDER = "order";
	private static final String KEY_DELETE_PATTERN = "deletePatterns";
	private static final String KEY_PROFILE_NAME = "profileName";
	private static final String KEY_PATH = "path";
	private static final String KEY_TABLE_NAME = "tableName";
	private static final String KEY_COLUMN_NAMES = "columnNames";
	private static final String KEY_CONDITION = "condition";
	private static final String KEY_DATA_SIZE = "dataSize";

	private final ImporterExporterType type;
	private final Map<String, Field> fieldMap = new LinkedHashMap<String, Field>();

	private ModifyListener listener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			validate(true);
		}
	};

	public SetImporterExporterMethodPage(ImporterExporterType type) {
		super("SetImporterExporterMethodPage." + type);
		setTitle("メソッドの内容の指定");
		setDescription(MessageFormat.format("{0}クラスの各メソッドの内容を入力して下さい。", type.displayName()));
		this.type = type;
	}

	public ImporterExporterType getType() {
		return type;
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridData compositeGrid = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(compositeGrid);
		composite.setLayout(new GridLayout(1, false));

		rebuild(composite);
		validate(false);

		setControl(composite);
	}

	private void rebuild(Composite composite) {
		if (isDirectio()) {
			Group group = createGroup(composite, "@directio.csv");
			createTextField(group, KEY_BASEPATH, true, "getBasePath()", "ベースパス", "論理パス\n"
					+ "「example」と入力すると\nreturn \"example\";\nになります。");
			createTextField(group, KEY_RESOUCE_PATTERN, true, "getResourcePattern()", "リソースパターン", "ファイル名のパターン\n"
					+ "「data.csv」と入力すると\nreturn \"data.csv\";\nになります。");
		}
		if (type == ImporterExporterType.DIRECTIO_CSV_EXPORTER) {
			Group group = createGroup(composite, type.displayName());
			createTextField(group, KEY_ORDER, false, "getOrder()", "ソート順", "出力ファイルのソート用カラム名（カンマ区切り）\n"
					+ "「+id1, -id2」と入力すると\nreturn Arrays.asList(\"+id1\", \"-id2\");\nになります。");
			createTextField(
					group,
					KEY_DELETE_PATTERN,
					false,
					"getDeletePatterns()",
					"削除パターン",
					"出力を行う前に削除するファイル名パターン（カンマ区切り）\n"
							+ "「data*.csv, test*.csv」と入力すると\nreturn Arrays.asList(\"data*.csv\", \"test*.csv\");\nになります。");
		}
		if (isWindgate()) {
			Group group = createGroup(composite, "@windgate");
			createTextField(group, KEY_PROFILE_NAME, true, "getProfileName()", "プロファイル", "プロファイル\n"
					+ "「example」と入力すると\nreturn \"example\";\nになります。\n"
					+ "この例の場合、$ASAKUSA_HOME/windgate/profile/example.properties が使われることになります。");
		}
		if (isWindgateCsv()) {
			Group group = createGroup(composite, "@windgate.csv");
			createTextField(group, KEY_PATH, true, "getPath()", "ファイルのパス",
					"プロファイル内で指定されているresource.local.basePath からの相対パス\n"
							+ "「data.csv」と入力すると\nreturn \"data.csv\";\nになります。");
		}
		if (isWindgateJdbc()) {
			Group group = createGroup(composite, "@windgate.jdbc");
			createTextField(group, KEY_TABLE_NAME, false, "getTableName()", "テーブル名", "テーブル名\n"
					+ "「TABLE1」と入力すると\nreturn \"TABLE1\";\n未入力だとgetTableName()は生成（オーバーライド）されず、DMDLで指定されたテーブル名が使われます。");
			createTextField(group, KEY_COLUMN_NAMES, false, "getColumnNames()", "カラム名", "絞り込むカラム名（カンマ区切り）\n"
					+ "「COL1, COL2」と入力すると\nreturn Arrays.asList(\"COL1\", \"COL2\");\n"
					+ "未入力だと\nreturn super.getColumnNames();\nとなります。");
		}
		if (type == ImporterExporterType.WINDGATE_JDBC_IMPORTER) {
			Group group = createGroup(composite, type.displayName());
			createTextField(group, KEY_CONDITION, false, "getCondition()", "WHERE条件", "インポーターが利用する抽出条件（SQLの条件式）\n"
					+ "「COL1 = 123」と入力すると\nreturn \"COL1 = 123\";\nになります。");
		}
		if (isImporter()) {
			Group group = createGroup(composite, "Importer");
			createComboField(group, KEY_DATA_SIZE, true, "getDataSize()", "データサイズ", "入力の推定データサイズ", "UNKNOWN", "TINY",
					"SMALL", "LARGE");
		}
	}

	private Group createGroup(Composite composite, String text) {
		Group group = new Group(composite, SWT.SHADOW_IN);
		group.setText(text);
		GridData groupGrid = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(groupGrid);

		group.setLayout(new GridLayout(3, false));

		return group;
	}

	private void createTextField(Group group, String key, boolean required, String label, String desc, String tip) {
		createLabel(group, required, label, desc, tip);

		Text text = new Text(group, SWT.BORDER);
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(grid);
		String value = getSetting(key);
		text.setText(nonNull(value));
		text.addModifyListener(listener);

		fieldMap.put(key, new Field(required, label, text));
	}

	private void createComboField(Group group, String key, boolean required, String label, String desc, String tip,
			String... values) {
		createLabel(group, required, label, desc, tip);

		Combo combo = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		combo.setLayoutData(grid);

		String value = getSetting(key);
		if (value == null) {
			value = values[0];
		}
		boolean found = false;
		for (String s : values) {
			combo.add(s);
			if (s.equals(value)) {
				found = true;
			}
		}
		if (!found) {
			value = values[0];
		}
		combo.setText(nonNull(value));
		combo.addModifyListener(listener);

		fieldMap.put(key, new Field(required, label, combo));
	}

	private void createLabel(Group group, boolean required, String label, String desc, String tip) {
		Label label1 = new Label(group, SWT.NONE);
		label1.setText(label);

		Label label2 = new Label(group, SWT.NONE);
		label2.setText(desc + (required ? "（必須）" : "（任意）"));
		label2.setToolTipText(tip);
	}

	private static class Field {
		public boolean required;
		public String label;
		public Widget widget;

		public Field(boolean required, String label, Widget widget) {
			this.required = required;
			this.label = label;
			this.widget = widget;
		}

		public String getText() {
			if (widget instanceof Text) {
				return ((Text) widget).getText().trim();
			} else if (widget instanceof Combo) {
				return ((Combo) widget).getText().trim();
			}
			throw new UnsupportedOperationException("widgetClass=" + widget.getClass());
		}
	}

	private void validate(boolean setError) {
		setPageComplete(false);
		for (Field f : fieldMap.values()) {
			if (f.required) {
				String s = f.getText();
				if (s.isEmpty()) {
					if (setError) {
						setErrorMessage(MessageFormat.format("{0}の値を入力して下さい。", f.label));
					}
					return;
				}
			}
		}

		setErrorMessage(null);
		setPageComplete(true);
	}

	private boolean isImporter() {
		return type.name().endsWith("IMPORTER");
	}

	private boolean isDirectio() {
		return type.name().startsWith("DIRECTIO");
	}

	private boolean isWindgate() {
		return type.name().startsWith("WINDGATE");
	}

	private boolean isWindgateCsv() {
		return type.name().startsWith("WINDGATE_CSV");
	}

	private boolean isWindgateJdbc() {
		return type.name().startsWith("WINDGATE_JDBC");
	}

	public final String getBasePath() {
		return getValue(KEY_BASEPATH);
	}

	public final String getResourcePattern() {
		return getValue(KEY_RESOUCE_PATTERN);
	}

	public final String getOrder() {
		return getValue(KEY_ORDER);
	}

	public final String getDeletePatterns() {
		return getValue(KEY_DELETE_PATTERN);
	}

	public final String getProfileName() {
		return getValue(KEY_PROFILE_NAME);
	}

	public final String getPath() {
		return getValue(KEY_PATH);
	}

	public final String getTableName() {
		return getValue(KEY_TABLE_NAME);
	}

	public final String getColumnNames() {
		return getValue(KEY_COLUMN_NAMES);
	}

	public final String getCondition() {
		return getValue(KEY_CONDITION);
	}

	public final String getDataSize() {
		return getValue(KEY_DATA_SIZE);
	}

	private String getValue(String key) {
		Field f = fieldMap.get(key);
		if (f == null) {
			return null;
		}
		String value = f.getText();
		setSetting(key, value);
		return value;
	}

	// DialogSettings
	private String getSetting(String key) {
		IDialogSettings settings = getDialogSettings();
		String value = settings.get(getKey1(key));
		if (value == null) {
			value = settings.get(getKey2(key));
		}
		return value;
	}

	private void setSetting(String key, String value) {
		IDialogSettings settings = getDialogSettings();
		settings.put(getKey1(key), value);
		settings.put(getKey2(key), value);
	}

	private String getKey1(String key) {
		return String.format("SetImporterExporterMethodPage.%s.%s", type.name(), key);
	}

	private String getKey2(String key) {
		return String.format("SetImporterExporterMethodPage.%s", key);
	}

	private static String nonNull(String s) {
		return (s != null) ? s : "";
	}
}
