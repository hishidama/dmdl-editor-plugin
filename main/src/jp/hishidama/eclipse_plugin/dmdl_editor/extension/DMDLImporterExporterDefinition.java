package jp.hishidama.eclipse_plugin.dmdl_editor.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.NewImporterExporterWizard;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen.ImporterExporterGenerator;

/**
 * Importer/Exporter作成ウィザードの定義.
 * 
 * @see NewImporterExporterWizard
 * @since 2013.07.14
 */
public abstract class DMDLImporterExporterDefinition {
	public static final String GROUP_IMPORTER = "Importer";
	public static final String GROUP_EXPORTER = "Exporter";

	public static final String KEY_DATA_SIZE = "Importer.dataSize";

	public final String getDisplayName() {
		return getName() + " " + (isExporter() ? "Exporter" : "Importer");
	}

	public abstract String getName();

	public abstract boolean isExporter();

	public abstract String getDefaultClassName();

	public abstract void initializeFields();

	protected final void addImporterDataSize() {
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

	private Map<String, List<FieldData>> map = new LinkedHashMap<String, List<FieldData>>();

	public final Map<String, List<FieldData>> getFields() {
		return map;
	}

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
		List<FieldData> list = map.get(group);
		if (list == null) {
			list = new ArrayList<FieldData>();
			map.put(group, list);
		}
		list.add(data);
	}

	public abstract ImporterExporterGenerator getGenerator();
}
