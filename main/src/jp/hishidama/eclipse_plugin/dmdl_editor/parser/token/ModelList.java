package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

import java.util.ArrayList;
import java.util.List;

public class ModelList extends DMDLBodyToken {
	private List<ModelToken> list;

	public ModelList(int start, int end, List<DMDLToken> bodyList) {
		super(start, end, bodyList);
	}

	/**
	 * 名前の定義されているモデル一覧を返す.
	 *
	 * @return モデル一覧
	 */
	public List<ModelToken> getNamedModelList() {
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

	public ModelToken getModelByOffset(int offset) {
		for (DMDLToken model : bodyList) {
			if (model.getStart() <= offset && offset < model.getEnd()) {
				return (ModelToken) model;
			}
		}
		return null;
	}
}
