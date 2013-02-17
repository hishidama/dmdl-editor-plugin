package jp.hishidama.eclipse_plugin.dmdl_editor.parser.index;

import java.util.HashMap;
import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken;

import org.eclipse.core.resources.IFile;

public class ModelIndex implements Index {

	private IFile file;
	private ModelToken model;
	private Map<String, PropertyIndex> map = new HashMap<String, PropertyIndex>();

	public ModelIndex(IFile file, ModelToken model) {
		this.file = file;
		this.model = model;
	}

	public String getName() {
		return model.getModelName();
	}

	@Override
	public IFile getFile() {
		return file;
	}

	@Override
	public int getOffset() {
		WordToken name = model.getModelNameToken();
		if (name != null) {
			return name.getStart();
		}
		return model.getStart();
	}

	@Override
	public int getEnd() {
		WordToken name = model.getModelNameToken();
		if (name != null) {
			return name.getEnd();
		}
		return model.getEnd();
	}

	public ModelToken getToken() {
		return model;
	}

	public void clearProperty() {
		map.clear();
	}

	public void addProperty(String name, PropertyToken prop) {
		PropertyIndex index = new PropertyIndex(this, prop);
		map.put(name, index);
	}

	public PropertyIndex getProperty(String name) {
		return map.get(name);
	}

}
