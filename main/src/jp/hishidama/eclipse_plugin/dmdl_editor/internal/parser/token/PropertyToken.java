package jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index.IndexContainer;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index.PropertyIndex;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken.WordType;

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

	public WordToken getRefNameToken() {
		return refNameToken;
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

	@Override
	public String getDataType(IndexContainer ic) {
		return getDataType(ic, new HashSet<PropertyToken>());
	}

	private String getDataType(IndexContainer ic, Set<PropertyToken> set) {
		if (set.contains(this)) {
			return null;
		}
		set.add(this);

		String dataType = getDataTypeFirst(ic, set);
		if (!"summarized".equals(getModelType())) {
			return dataType;
		}

		String sumType = (sumTypeToken != null) ? sumTypeToken.getBody() : null;
		if ("count".equals(sumType)) {
			return "LONG";
		}
		if ("sum".equals(sumType)) {
			if ("BYTE".equals(dataType) || "SHORT".equals(dataType)
					|| "INT".equals(dataType) || "LONG".equals(dataType)) {
				return "LONG";
			} else if ("FLOAT".equals(dataType) || "DOUBLE".equals(dataType)) {
				return "DOUBLE";
			} else if ("DECIMAL".equals(dataType)) {
				return "DECIMAL";
			}
			return null;
		}
		if ("any".equals(sumType) || "min".equals(sumType)
				|| "max".equals(sumType)) {
			return dataType;
		}
		return null;
	}

	private String getDataTypeFirst(IndexContainer ic, Set<PropertyToken> set) {
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
					return prop.getDataType(ic, set);
				}
			} else {
				return getDataTypeFrom(ic, refNameToken.getBody());
			}
		}
		return null;
	}

	private String getDataTypeFrom(IndexContainer ic, String name) {
		if (ic == null) {
			return null;
		}
		DMDLToken ref = findRefModelToken();
		if (ref instanceof WordToken) {
			String modelName = ((WordToken) ref).getBody();
			PropertyIndex index = ic.findProperty(modelName, name);
			if (index != null) {
				PropertyToken token = index.getToken();
				return token.getDataType(ic);
			}
		}
		return null;
	}

	@Override
	public String getPropertyDescription() {
		DescriptionToken desc = getPropertyDescriptionToken();
		if (desc != null) {
			return desc.getBody();
		}
		return null;
	}

	public DescriptionToken getPropertyDescriptionToken() {
		for (DMDLToken token : bodyList) {
			if (token instanceof DescriptionToken) {
				return (DescriptionToken) token;
			}
		}
		return null;
	}

	public String getQualifiedName(IndexContainer ic) {
		StringBuilder sb = new StringBuilder(64);

		String desc = getPropertyDescription();
		if (desc != null) {
			sb.append(desc);
			sb.append(" ");
		}

		String name = getName();
		sb.append(name);

		String type = getDataType(ic);
		if (type != null) {
			sb.append(" : ");
			sb.append(type);
		}

		return sb.toString();
	}
}
