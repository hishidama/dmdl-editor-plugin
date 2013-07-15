package jp.hishidama.eclipse_plugin.dmdl_editor.extension;

public abstract class WindgateDefinition extends DMDLImporterExporterDefinition {
	public static final String GROUP_WINDGATE = "@windgate";
	public static final String GROUP_WINDGATE_CSV = "@windgate.csv";
	public static final String GROUP_WINDGATE_JDBC = "@windgate.jdbc";
	public static final String GROUP_WINDGATE_JDBC_IMPORTER = "@windgate.jdbc Importer";
	public static final String GROUP_WINDGATE_JDBC_EXPORTER = "@windgate.jdbc Exporter";

	public static final String KEY_PROFILE_NAME = "windate.profileName";
	public static final String KEY_PATH = "windate.path";
	public static final String KEY_TABLE_NAME = "windate.jdbc.tableName";
	public static final String KEY_COLUMN_NAMES = "windate.jdbc.columnNames";
	public static final String KEY_CONDITION = "windate.jdbc.condition";

	protected final void addWindgate() {
		addTextField(GROUP_WINDGATE, KEY_PROFILE_NAME, true, "getProfileName()", "プロファイル", "プロファイル\n"
				+ "「example」と入力すると\nreturn \"example\";\nになります。\n"
				+ "この例の場合、$ASAKUSA_HOME/windgate/profile/example.properties が使われることになります。");
	}

	protected final void addWindgateCsv() {
		addTextField(GROUP_WINDGATE_CSV, KEY_PATH, true, "getPath()", "ファイルのパス",
				"プロファイル内で指定されているresource.local.basePath からの相対パス\n" + "「data.csv」と入力すると\nreturn \"data.csv\";\nになります。");
	}

	protected final void addWindgateJdbc() {
		addTextField(GROUP_WINDGATE_JDBC, KEY_TABLE_NAME, false, "getTableName()", "テーブル名", "テーブル名\n"
				+ "「TABLE1」と入力すると\nreturn \"TABLE1\";\n未入力だとgetTableName()は生成（オーバーライド）されず、DMDLで指定されたテーブル名が使われます。");
		addTextField(GROUP_WINDGATE_JDBC, KEY_COLUMN_NAMES, false, "getColumnNames()", "カラム名", "絞り込むカラム名（カンマ区切り）\n"
				+ "「COL1, COL2」と入力すると\nreturn Arrays.asList(\"COL1\", \"COL2\");\n"
				+ "未入力だと\nreturn super.getColumnNames();\nとなります。");
	}

	protected final void addWindgateJdbcImporter() {
		addTextField(GROUP_WINDGATE_JDBC_IMPORTER, KEY_CONDITION, false, "getCondition()", "WHERE条件",
				"インポーターが利用する抽出条件（SQLの条件式）\n" + "「COL1 = 123」と入力すると\nreturn \"COL1 = 123\";\nになります。");
	}
}
