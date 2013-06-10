package jp.hishidama.eclipse_plugin.dmdl_editor.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.util.StringUtil;

public class DataModelInfo extends DataModelPosition {
	private static final long serialVersionUID = -2060031678717073341L;

	private String name;
	private String description;
	private String type;

	private List<DataModelProperty> properties;

	private transient Map<String, DataModelProperty> snakeMap;
	private transient Map<String, DataModelProperty> camelMap;

	public DataModelInfo(String name, String description, String modelType) {
		this.name = name;
		this.description = description;
		this.type = modelType;
	}

	@Override
	public DataModelFile getParent() {
		return (DataModelFile) super.getParent();
	}

	public String getModelName() {
		return name;
	}

	public String getModelDescription() {
		return description;
	}

	public String getModelType() {
		return type;
	}

	public List<DataModelProperty> getProperties() {
		if (properties == null) {
			properties = DataModelUtil.getModelProperties(getProject(), getModelName());
			for (DataModelProperty prop : properties) {
				prop.setParent(this);
			}
		}
		return properties;
	}

	public void addProperty(DataModelProperty prop) {
		prop.setParent(this);

		if (properties == null) {
			properties = new ArrayList<DataModelProperty>();
		}
		properties.add(prop);
	}

	public DataModelProperty getProperty(String name) {
		if (snakeMap == null) {
			snakeMap = new HashMap<String, DataModelProperty>();
			for (DataModelProperty p : getProperties()) {
				snakeMap.put(p.getName(), p);
			}
		}
		DataModelProperty prop = snakeMap.get(name);
		if (prop != null) {
			return prop;
		}

		if (camelMap == null) {
			camelMap = new HashMap<String, DataModelProperty>();
			for (DataModelProperty p : getProperties()) {
				String cname = StringUtil.toLowerCamelCase(p.getName());
				camelMap.put(cname, p);
			}
		}
		return camelMap.get(name);
	}
}
