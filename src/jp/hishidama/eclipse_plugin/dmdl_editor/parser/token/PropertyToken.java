package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

import java.util.List;

public class PropertyToken extends DMDLBodyToken {

	public PropertyToken(int start, int end, List<DMDLToken> bodyList) {
		super(start, end, bodyList);
	}

	@Override
	public String toString() {
		return toString("PropertyToken", "      ");
	}

	private boolean searchName = false;
	private WordToken nameToken;

	public WordToken getPropertyNameToken() {
		if (!searchName) {
			int n = indexOfWord(":");
			if (n >= 0) {
				nameToken = findWord(n - 1, -1);
			} else {
				n = indexOfWord("->");
				if (n >= 0) {
					nameToken = findWord(n + 1, +1);
				}
			}
			searchName = true;
		}
		return nameToken;
	}

	public String getPropertyName() {
		WordToken token = getPropertyNameToken();
		if (token != null) {
			return token.getBody();
		}
		return null;
	}

	private boolean searchType = false;
	private WordToken typeToken;

	public WordToken getDataTypeToken() {
		if (!searchType) {
			int n = indexOfWord(":");
			if (n >= 0) {
				typeToken = findWord(n + 1, +1);
			}
			searchType = true;
		}
		return typeToken;
	}

	public String getDataType() {
		WordToken token = getDataTypeToken();
		if (token != null) {
			return token.getBody();
		}
		return null;
	}
}
