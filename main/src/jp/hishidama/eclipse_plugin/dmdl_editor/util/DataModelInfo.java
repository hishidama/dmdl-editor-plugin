package jp.hishidama.eclipse_plugin.dmdl_editor.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.util.StringUtil;

public class DataModelInfo extends DataModelPosition {
	private static final long serialVersionUID = -2060031678717073341L;

	private String name;
	private String description;
	private String type;

	private List<DataModelProperty> properties;
	private Map<String, List<String>> keyMap;

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

	public boolean isPropertiesNull() {
		return properties == null;
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

	public void setPropertyEmpty() {
		properties = Collections.emptyList();
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

	public void addGroupKey(String refModelName, String key) {
		if (keyMap == null) {
			keyMap = new LinkedHashMap<String, List<String>>();
		}
		List<String> list = keyMap.get(refModelName);
		if (list == null) {
			list = new ArrayList<String>();
			keyMap.put(refModelName, list);
		}
		list.add(key);
	}

	public Map<String, List<String>> getGroupKey() {
		if (keyMap == null) {
			return Collections.emptyMap();
		}
		return keyMap;
	}

	public List<String> getGroupKey(String refModelName) {
		Map<String, List<String>> map = getGroupKey();
		List<String> list = map.get(refModelName);
		if (list == null) {
			return Collections.emptyList();
		}
		return list;
	}
}
