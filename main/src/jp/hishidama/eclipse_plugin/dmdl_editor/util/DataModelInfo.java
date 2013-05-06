package jp.hishidama.eclipse_plugin.dmdl_editor.util;

import org.eclipse.core.resources.IFile;

public class DataModelInfo {
	private String name;
	private String description;
	private String modelType;
	private IFile file;

	public DataModelInfo(String name, String description, String modelType, IFile file) {
		this.name = name;
		this.description = description;
		this.modelType = modelType;
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getModelType() {
		return modelType;
	}

	public IFile getFile() {
		return file;
	}
}