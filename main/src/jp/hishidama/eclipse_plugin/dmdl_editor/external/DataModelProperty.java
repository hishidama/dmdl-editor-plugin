package jp.hishidama.eclipse_plugin.dmdl_editor.external;

public class DataModelProperty {
	private String name;
	private String description;
	private String type;

	public DataModelProperty(String name, String description, String type) {
		this.name = name;
		this.description = description;
		this.type = type;
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
}