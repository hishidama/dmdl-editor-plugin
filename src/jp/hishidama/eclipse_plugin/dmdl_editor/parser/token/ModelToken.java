package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

import java.util.ArrayList;
import java.util.List;

public class ModelToken extends DMDLBodyToken {

	public ModelToken(int start, int end, List<DMDLToken> bodyList) {
		super(start, end, bodyList);
	}

	@Override
	public String toString() {
		return toString("ModelToken", "  ");
	}

	private boolean searchName = false;
	private WordToken nameToken;

	public WordToken getModelNameToken() {
		if (!searchName) {
			int n = indexOfWord("=");
			if (n >= 0) {
				nameToken = findWord(n - 1, -1);
			}
			searchName = true;
		}
		return nameToken;
	}

	public String getModelName() {
		WordToken token = getModelNameToken();
		if (token != null) {
			return token.getBody();
		}
		return null;
	}

	private List<PropertyToken> propList;

	public List<PropertyToken> getPropertyList() {
		if (propList == null) {
			propList = new ArrayList<PropertyToken>();
			addProperty(propList, this);
		}
		return propList;
	}

	private void addProperty(List<PropertyToken> list, DMDLToken token) {
		if (token instanceof PropertyToken) {
			PropertyToken prop = (PropertyToken) token;
			if (prop.getPropertyNameToken() != null) {
				list.add(prop);
			}
		} else if (token instanceof DMDLBodyToken) {
			for (DMDLToken t : ((DMDLBodyToken) token).getBody()) {
				addProperty(list, t);
			}
		}
	}
}
