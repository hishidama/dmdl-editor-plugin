package jp.hishidama.eclipse_plugin.dmdl_editor.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.portergen.ClassGenerator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.BuildPropertiesUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.NewImporterExporterWizard;
import jp.hishidama.eclipse_plugin.util.FileUtil;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * DMDLからImporter/Exporterクラスのソースを生成する.
 * 
 * @see NewImporterExporterWizard
 * @since 2013.07.15
 */
public abstract class DMDLImporterExporterGenerator extends ClassGenerator {
	public static final String GROUP_IMPORTER = "Importer";
	public static final String GROUP_EXPORTER = "Exporter";

	public static final String KEY_DATA_SIZE = "Importer.dataSize";

	public final String getFullName() {
		return getName() + "_" + (isExporter() ? "Exporter" : "Importer");
	}

	public final String getDisplayName() {
		return getName() + " " + (isExporter() ? "Exporter" : "Importer");
	}

	/**
	 * Generator名.
	 * <p>
	 * 例：「@directio.csv」（末尾に「Importer」や「Exporter」を含めない）
	 * </p>
	 * 
	 * @return Generator名
	 */
	public abstract String getName();

	/**
	 * ImporterかExporterか.
	 * 
	 * @return true：Exporter
	 */
	public abstract boolean isExporter();

	/**
	 * デフォルトクラス名.
	 * 
	 * @return デフォルトクラス名
	 */
	public abstract String getDefaultClassName();

	/**
	 * 設定項目初期化.
	 * 
	 * @see #addTextField(String, String, boolean, String, String, String)
	 * @see #addComboField(String, String, boolean, String, String, String,
	 *      String...)
	 */
	public abstract void initializeFields();

	// フィールド定義用
	/**
	 * ImporterのdataSize項目の追加.
	 */
	protected final void addFieldImporterDataSize() {
		addComboField(GROUP_IMPORTER, KEY_DATA_SIZE, true, "getDataSize()", "データサイズ", "入力の推定データサイズ", "UNKNOWN", "TINY",
				"SMALL", "LARGE");
	}

	public static class FieldData {
		public String groupName;
		public String keyName;
		public boolean required;
		public String displayName;
		public String description;
		public String toolTip;
		public List<String> combo;
	}

	private Map<String, List<FieldData>> fieldMap = new LinkedHashMap<String, List<FieldData>>();

	public final Map<String, List<FieldData>> getFields() {
		return fieldMap;
	}

	/**
	 * Text項目追加.
	 * 
	 * @param groupName
	 *            グループ名
	 * @param keyName
	 *            キー
	 * @param required
	 *            必須かどうか
	 * @param displayName
	 *            表示名（メソッド名）
	 * @param description
	 *            説明（日本語名）
	 * @param toolTip
	 *            ツールチップテキスト
	 */
	protected final void addTextField(String groupName, String keyName, boolean required, String displayName,
			String description, String toolTip) {
		FieldData data = new FieldData();
		data.groupName = groupName;
		data.keyName = keyName;
		data.required = required;
		data.displayName = displayName;
		data.description = description;
		data.toolTip = toolTip;
		addField(data);
	}

	/**
	 * ComboBox項目追加.
	 * 
	 * @param groupName
	 *            グループ名
	 * @param keyName
	 *            キー
	 * @param required
	 *            必須かどうか
	 * @param displayName
	 *            表示名（メソッド名）
	 * @param description
	 *            説明（日本語名）
	 * @param toolTip
	 *            ツールチップテキスト
	 * @param value
	 *            コンボボックスの選択肢
	 */
	protected final void addComboField(String groupName, String keyName, boolean required, String displayName,
			String description, String toolTip, String... value) {
		FieldData data = new FieldData();
		data.groupName = groupName;
		data.keyName = keyName;
		data.required = required;
		data.displayName = displayName;
		data.description = description;
		data.toolTip = toolTip;
		data.combo = Arrays.asList(value);
		addField(data);
	}

	private void addField(FieldData data) {
		String group = data.groupName;
		List<FieldData> list = fieldMap.get(group);
		if (list == null) {
			list = new ArrayList<FieldData>();
			fieldMap.put(group, list);
		}
		list.add(data);
	}

	protected Map<String, String> map;
	protected IProject project;
	protected ModelToken model;
	protected String dir;
	protected DmdlCompilerProperties properties;

	public void generate(IProject project, DmdlCompilerProperties properties, Map<String, String> map,
			ModelToken model, String dir, String name, boolean open) throws CoreException {
		this.properties = properties;
		this.map = map;
		this.project = project;
		this.model = model;
		this.dir = dir;
		String resolvedName = StringUtil.replace(name, model.getModelName(), "", "");
		String packageName = StringUtil.getPackageName(resolvedName);
		String simpleName = StringUtil.getSimpleName(resolvedName);
		String contents = super.generate(packageName, simpleName);

		IFile file = getFile(packageName);
		FileUtil.save(file, contents);
		if (open) {
			FileUtil.openFile(file, resolvedName);
		}
	}

	private IFile getFile(String packageName) throws CoreException {
		IFolder folder = project.getFolder(dir);
		folder = folder.getFolder(packageName.replace('.', '/'));
		IFile file = folder.getFile(className + ".java");
		FileUtil.createFolder(project, folder.getProjectRelativePath());
		return file;
	}

	@Override
	protected void initialize() {
		properties = BuildPropertiesUtil.getBuildProperties(project, true);
	}

	protected String getGeneratedClassName(String middle, String simpleName) {
		String pack = properties.getModelgenPackage();

		StringBuilder sb = new StringBuilder(64);
		sb.append(pack);
		sb.append(middle);
		sb.append(simpleName);
		return sb.toString();
	}

	@Override
	protected void appendClass(StringBuilder sb) {
		sb.append("public class ");
		sb.append(className);
		sb.append(" extends ");
		sb.append(getExtendsClassName());
		sb.append(" {\n");

		appendMethods(sb);

		sb.append("}\n");
	}

	protected String getExtendsClassName() {
		String camelName = StringUtil.toCamelCase(model.getModelName());
		String fullName = getExtendsClassName(camelName);
		return getCachedClassName(fullName);
	}

	protected abstract String getExtendsClassName(String modelCamelName);

	protected abstract void appendMethods(StringBuilder sb);

	// メソッド生成用
	/**
	 * getDataSize()メソッド生成.
	 * 
	 * @param sb
	 *            生成先
	 */
	protected final void appendMethodDataSize(StringBuilder sb) {
		// DataSizeは親クラスで定義されている内部クラスなので、importしなくてよい。
		// getCachedClassName("com.asakusafw.vocabulary.external.ImporterDescription.DataSize");
		String name = "DataSize";
		String size = String.format("%s.%s", name, map.get(KEY_DATA_SIZE));
		appendMethod(sb, name, "getDataSize", size, "");
	}

	/**
	 * メソッド生成.
	 * <p>
	 * 戻り型がStringのメソッドを生成する。
	 * </p>
	 * 
	 * @param sb
	 *            生成先
	 * @param method
	 *            メソッド名
	 * @param value
	 *            値
	 */
	protected final void appendMethod(StringBuilder sb, String method, String value) {
		appendMethod(sb, "String", method, StringUtil.escapeQuote(value), "\"");
	}

	/**
	 * メソッド生成.
	 * <p>
	 * 戻り型がStringで戻り値がnullのメソッドを生成する。
	 * </p>
	 * 
	 * @param sb
	 *            生成先
	 * @param method
	 *            メソッド名
	 */
	protected final void appendMethodNull(StringBuilder sb, String method) {
		appendMethod(sb, "String", method, "null", "");
	}

	/**
	 * メソッド生成.
	 * <p>
	 * 戻り型がListのメソッドを生成する。
	 * </p>
	 * 
	 * @param sb
	 *            生成先
	 * @param method
	 *            メソッド名
	 * @param value
	 *            値（カンマ区切りで複数指定）
	 */
	protected final void appendMethodList(StringBuilder sb, String method, String value) {
		StringBuilder buf = new StringBuilder(value.length());
		if (value.trim().isEmpty()) {
			buf.append("super.");
			buf.append(method);
			buf.append("()");
		} else {
			buf.append(getCachedClassName("java.util.Arrays"));
			buf.append(".asList(");
			String[] ss = value.split(",");
			boolean first = true;
			for (String s : ss) {
				if (first) {
					first = false;
				} else {
					buf.append(", ");
				}
				buf.append("\"");
				buf.append(StringUtil.escapeQuote(s.trim()));
				buf.append("\"");
			}
			buf.append(")");
		}

		String rtype = getCachedClassName("java.util.List") + "<String>";
		appendMethod(sb, rtype, method, buf, "");
	}

	private void appendMethod(StringBuilder sb, String rtype, String method, CharSequence value, String quote) {
		sb.append("\n\t@Override\n");
		sb.append("\tpublic ");
		sb.append(rtype);
		sb.append(" ");
		sb.append(method);
		sb.append("() {\n");
		sb.append("\t\treturn ");
		sb.append(quote);
		sb.append(value);
		sb.append(quote);
		sb.append(";\n");
		sb.append("\t}\n");
	}
}
