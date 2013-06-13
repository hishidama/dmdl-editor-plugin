package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.format.DMDLContentFormatter;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelUtil;
import jp.hishidama.eclipse_plugin.util.StringUtil;

public class DataModelTextGenerator {
	private String modelName;
	private String modelDescription;
	private String modelType;

	public void setModelName(String name) {
		this.modelName = name;
	}

	public void setModelDescription(String description) {
		this.modelDescription = description;
	}

	public void setModelType(String type) {
		this.modelType = type;
	}

	private StringBuilder sb = new StringBuilder(1024);
	private String indent = "  ";

	private String blockName = NOTHING;
	private static final String NOTHING = "\0nothing\0";
	private String refNameOnly = null;

	public void appendProperty(String name, String desc, String type) {
		block("", null, true, true);

		appendDescription(desc);
		sb.append(indent);
		sb.append(name);
		sb.append(" : ");
		sb.append(type);
		sb.append(";\n");
	}

	public void appendRefProperty(String refModelName) {
		if (!refModelName.equals(this.refNameOnly)) {
			block(refModelName, null, false, false);
		}
	}

	public void appendRefProperty(String name, String desc, String refModelName, String refName) {
		block(refModelName, "->", true, true);

		appendDescription(desc);
		sb.append(indent);
		sb.append(StringUtil.nonEmpty(refName) ? refName : name);
		sb.append(" -> ");
		sb.append(StringUtil.nonEmpty(name) ? name : refName);
		sb.append(";\n");
	}

	public void appendSumProperty(String name, String desc, String type, String refModelName, String refName) {
		block(refModelName, "=>", true, true);

		appendDescription(desc);
		sb.append(indent);
		sb.append(type);
		sb.append(" ");
		sb.append(StringUtil.nonEmpty(refName) ? refName : name);
		sb.append(" -> ");
		sb.append(StringUtil.nonEmpty(name) ? name : refName);
		sb.append(";\n");
	}

	private StringBuilder key = new StringBuilder(128);

	public void appendKey(String name, String refName) {
		key.append(key.length() == 0 ? " % " : ", ");
		key.append(StringUtil.nonEmpty(name) ? name : refName);
	}

	private void block(String refModelName, String arrow, boolean block, boolean lf) {
		if (refModelName == null) {
			refModelName = "";
		}

		if (!refModelName.equals(blockName)) {
			if (blockName != NOTHING) {
				sb.append("}");
			}
			sb.append(key);
			key.setLength(0);

			sb.append(sb.length() == 0 ? "" : " +");
			if (refModelName != NOTHING && !refModelName.isEmpty()) {
				sb.append(" ");
				sb.append(refModelName);
				refNameOnly = refModelName;
			}
			if (arrow != null) {
				sb.append(" ");
				sb.append(arrow);
			}
			if (block) {
				sb.append(" {\n");
				refNameOnly = null;
				blockName = refModelName;
			} else {
				blockName = NOTHING;
			}
		} else {
			if (lf) {
				sb.append("\n");
			}
		}
	}

	private void appendDescription(String desc) {
		if (StringUtil.nonEmpty(desc)) {
			sb.append(indent);
			sb.append(DataModelUtil.encodeDescription(desc));
			sb.append("\n");
		}
	}

	public String getText() {
		if (sb.length() == 0) {
			sb.append(" {}");
		} else if (blockName != NOTHING && refNameOnly == null) {
			sb.append("}");
			blockName = NOTHING;
		}
		sb.append(key);
		key.setLength(0);

		StringBuilder all = new StringBuilder(256 + sb.length());
		if (StringUtil.nonEmpty(modelDescription)) {
			all.append(DataModelUtil.encodeDescription(modelDescription));
			all.append("\n");
		}
		if (StringUtil.nonEmpty(modelType)) {
			all.append(modelType);
			all.append(" ");
		}
		all.append(modelName);
		all.append(" =");
		all.append(sb);
		all.append(";\n");

		DMDLContentFormatter formatter = new DMDLContentFormatter();
		String text = formatter.format(all.toString());
		return text;
	}
}
