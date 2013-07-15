package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen;

public abstract class DirectioGenerator extends ImporterExporterGenerator {
	public static final String GROUP_DIRECTIO_CSV = "@directio.csv";
	public static final String GROUP_DIRECTIO_CSV_EXPORTER = "@directio.csv Exporter";

	public static final String KEY_BASE_PATH = "directio.basePath";
	public static final String KEY_RESOURCE_PATTERN = "directio.resourcePattern";
	public static final String KEY_ORDER = "directio.order";
	public static final String KEY_DELETE_PATTERN = "directio.deletePattern";

	protected final void addDirectioCsv() {
		addTextField(GROUP_DIRECTIO_CSV, KEY_BASE_PATH, true, "getBasePath()", "ベースパス", "論理パス\n"
				+ "「example」と入力すると\nreturn \"example\";\nになります。");
		addTextField(GROUP_DIRECTIO_CSV, KEY_RESOURCE_PATTERN, true, "getResourcePattern()", "リソースパターン", "ファイル名のパターン\n"
				+ "「data.csv」と入力すると\nreturn \"data.csv\";\nになります。");
	}

	protected final void addDirectioCsvExporter() {
		addTextField(GROUP_DIRECTIO_CSV_EXPORTER, KEY_ORDER, false, "getOrder()", "ソート順", "出力ファイルのソート用カラム名（カンマ区切り）\n"
				+ "「+id1, -id2」と入力すると\nreturn Arrays.asList(\"+id1\", \"-id2\");\nになります。");
		addTextField(GROUP_DIRECTIO_CSV_EXPORTER, KEY_DELETE_PATTERN, false, "getDeletePatterns()", "削除パターン",
				"出力を行う前に削除するファイル名パターン（カンマ区切り）\n"
						+ "「data*.csv, test*.csv」と入力すると\nreturn Arrays.asList(\"data*.csv\", \"test*.csv\");\nになります。");
	}

	protected final void appendMethodBasePath(StringBuilder sb) {
		appendMethod(sb, "getBasePath", map.get(KEY_BASE_PATH));
	}

	protected final void appendMethodResourcePattern(StringBuilder sb) {
		appendMethod(sb, "getResourcePattern", map.get(KEY_RESOURCE_PATTERN));
	}

	protected final void appendMethodOrder(StringBuilder sb) {
		appendMethodList(sb, "getOrder", map.get(KEY_ORDER));
	}

	protected final void appendMethodDeletePatterns(StringBuilder sb) {
		appendMethodList(sb, "getDeletePatterns", map.get(KEY_DELETE_PATTERN));
	}
}
