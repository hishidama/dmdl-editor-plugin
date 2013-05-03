package jp.hishidama.eclipse_plugin.dmdl_editor.util;

import org.eclipse.core.resources.IFile;

public class DataModelInfo {
	private String name;
	private String description;
	private IFile file;

	public DataModelInfo(String name, String description, IFile file) {
		this.name = name;
		this.description = description;
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public IFile getFile() {
		return file;
	}
}