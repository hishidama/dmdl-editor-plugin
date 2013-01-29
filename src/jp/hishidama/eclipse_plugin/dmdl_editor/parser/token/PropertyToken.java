package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken.WordType;

public class PropertyToken extends DMDLBodyToken {
	private WordToken nameToken;
	private WordToken typeToken;
	private WordToken refNameToken;
	private WordToken sumTypeToken;

	public PropertyToken(int start, int end, List<DMDLToken> bodyList) {
		super(start, end, bodyList);
		parse();
	}

	protected void parse() {
		int n = indexOf(WordType.TYPE_SEPARATOR);
		if (n >= 0) {
			setNameToken(findWord(n - 1, -1));
			setTypeToken(findWord(n + 1, +1));
		} else {
			n = indexOf(WordType.ALLOW);
			if (n >= 0) {
				setRefNameToken(findWord(n - 1, -1));
				setNameToken(findWord(n + 1, +1));
			}
		}
	}

	public void setNameToken(WordToken token) {
		nameToken = token;
		if (token != null) {
			token.setWordType(WordType.PROPERTY_NAME);
		}
	}

	public WordToken getNameToken() {
		return nameToken;
	}

	public String getName() {
		if (nameToken != null) {
			return nameToken.getBody();
		}
		return null;
	}

	public void setTypeToken(WordToken token) {
		typeToken = token;
		if (token != null) {
			token.setWordType(WordType.DATA_TYPE);
		}
	}

	public void setRefNameToken(WordToken token) {
		refNameToken = token;
		if (token != null) {
			token.setWordType(WordType.REF_PROPERTY_NAME);
		}
	}

	public void setSumTypeToken(WordToken token) {
		sumTypeToken = token;
		if (token != null) {
			token.setWordType(WordType.SUMMARIZED_TYPE);
		}
	}

	@Override
	public String toString() {
		return toString("PropertyToken", "      ");
	}

	public WordToken getPropertyNameToken() {
		return nameToken;
	}

	public String getPropertyName() {
		WordToken token = getPropertyNameToken();
		if (token != null) {
			return token.getBody();
		}
		return null;
	}

	public WordToken getDataTypeToken() {
		return typeToken;
	}

	public String getDataType() {
		String dataType = getDataType(new HashSet<PropertyToken>());
		if (sumTypeToken != null) {
			String sumType = sumTypeToken.getBody();
			if ("sum".equals(sumType)) {
				if ("INT".equals(dataType)) {
					return "LONG";
				}
			} else if ("count".equals(sumType)) {
				return "INT";
			}
		}
		return dataType;
	}

	private String getDataType(Set<PropertyToken> set) {
		if (set.contains(this)) {
			return null;
		}
		WordToken token = getDataTypeToken();
		if (token != null) {
			return token.getBody();
		}
		if (refNameToken != null) {
			WordToken target = refNameToken.getReferenceWord();
			if (target != null) {
				DMDLToken parent = target.getParent();
				if (parent instanceof PropertyToken) {
					PropertyToken prop = (PropertyToken) parent;
					set.add(this);
					return prop.getDataType(set);
				}
			}
		}
		return null;
	}
}
