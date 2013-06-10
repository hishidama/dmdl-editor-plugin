package jp.hishidama.eclipse_plugin.dmdl_editor.util;

public class DataModelProperty extends DataModelPosition {
	private static final long serialVersionUID = 2715522465833249435L;

	private String name;
	private String description;
	private String type;
	private String sumType;

	private String refModelName;
	private String refPropertyName;

	public DataModelProperty(String name, String description, String type) {
		this.name = name;
		this.description = description;
		this.type = type;
	}

	public DataModelProperty(String name, String description, String refModelName, String refPropertyName) {
		this.name = name;
		this.description = description;
		this.refModelName = refModelName;
		this.refPropertyName = refPropertyName;
	}

	@Override
	public DataModelInfo getParent() {
		return (DataModelInfo) super.getParent();
	}

	public String getModelName() {
		return getParent().getModelName();
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getDataType() {
		return type;
	}

	public String getResolvedDataType() {
		if (type == null) {
			type = DataModelUtil.getResolvedDataType(getProject(), refModelName, refPropertyName);
		}
		return type;
	}

	public void setSumType(String type) {
		this.sumType = type;
	}

	public String getSumType() {
		return sumType;
	}

	public String getRefModelName() {
		return refModelName;
	}

	public String getRefPropertyName() {
		return refPropertyName;
	}
}