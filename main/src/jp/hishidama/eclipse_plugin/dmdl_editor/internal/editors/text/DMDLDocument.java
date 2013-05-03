package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelList;

import org.eclipse.jface.text.Document;

public class DMDLDocument extends Document {

	protected ModelList models;

	public void setModelList(ModelList models) {
		this.models = models;
	}

	public ModelList getModelList() {
		return models;
	}
}
