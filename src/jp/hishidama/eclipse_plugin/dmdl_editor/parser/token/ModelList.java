package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

import java.util.ArrayList;
import java.util.List;

public class ModelList extends DMDLBodyToken {
	private List<ModelToken> list;

	public ModelList(int start, int end, List<DMDLToken> bodyList) {
		super(start, end, bodyList);
	}

	public List<ModelToken> getModelList() {
		if (list == null) {
			list = new ArrayList<ModelToken>();
			for (DMDLToken token : getBody()) {
				ModelToken model = (ModelToken) token;
				if (model.getModelNameToken() != null) {
					list.add(model);
				}
			}
		}
		return list;
	}
}
