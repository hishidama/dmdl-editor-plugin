package jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;

import org.eclipse.core.resources.IFile;

public class PropertyIndex implements Index {

	private ModelIndex model;
	private PropertyToken prop;

	public PropertyIndex(ModelIndex model, PropertyToken prop) {
		this.model = model;
		this.prop = prop;
	}

	public ModelIndex getModel() {
		return model;
	}

	public String getName() {
		return prop.getName();
	}

	@Override
	public IFile getFile() {
		return model.getFile();
	}

	public PropertyToken getToken() {
		return prop;
	}

	@Override
	public int getOffset() {
		return prop.getNameToken().getStart();
	}

	@Override
	public int getEnd() {
		return prop.getNameToken().getEnd();
	}
}
