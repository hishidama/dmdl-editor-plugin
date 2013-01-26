package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

import java.util.HashMap;
import java.util.Map;

public class WordToken extends DMDLTextToken {
	public static enum WordType {
		UNKNOWN, DEF, COMMA, ALLOW, PERCENT,
		// モデル名
		MODEL_NAME, REF_MODEL_NAME,
		// プロパティー
		PROPERTY_NAME, REF_PROPERTY_NAME, TYPE_SEPARATOR, DATA_TYPE,
		// 集計モデル
		SUMMARIZED_MODEL, SUMMARIZED_DEF, SUMMARIZED_TYPE,
		// 結合モデル
		JOIN_MODEL, PLUS,
		// 射影モデル
		PROJECTIVE_MODEL,
	}

	protected WordType type = WordType.UNKNOWN;

	public WordToken(int start, int end, String text) {
		super(start, end, text);
		initializeType(text);
	}

	private static final Map<String, WordType> TYPE_MAP = new HashMap<String, WordToken.WordType>();
	static {
		TYPE_MAP.put("=", WordType.DEF);
		TYPE_MAP.put("=>", WordType.SUMMARIZED_DEF);
		TYPE_MAP.put("->", WordType.ALLOW);
		TYPE_MAP.put("+", WordType.PLUS);
		TYPE_MAP.put("%", WordType.PERCENT);
		TYPE_MAP.put(",", WordType.COMMA);
		TYPE_MAP.put(":", WordType.TYPE_SEPARATOR);
	}

	protected void initializeType(String text) {
		WordType t = TYPE_MAP.get(text);
		if (t != null) {
			setWordType(t);
		}
	}

	public void setWordType(WordType type) {
		this.type = type;
	}

	public WordType getWordType() {
		return type;
	}

	@Override
	public String toString() {
		return toString("WordToken");
	}

	@Override
	public DMDLToken getReference() {
		switch (getWordType()) {
		case REF_MODEL_NAME: {
			ModelToken refModel = findModel(getBody());
			if (refModel != null) {
				WordToken refName = refModel.getModelNameToken();
				if (refName != null) {
					return refName;
				}
				return refModel;
			}
			return null;
		}
		case REF_PROPERTY_NAME: {
			WordToken refModelName = findRefModelNameToken();
			if (refModelName != null) {
				ModelToken refModel = findModel(refModelName.getBody());
				if (refModel != null) {
					PropertyToken refProp = refModel.findProperty(getBody());
					if (refProp != null) {
						WordToken refName = refProp.getNameToken();
						if (refName != null) {
							return refName;
						}
						return refProp;
					}
					return refModel;
				}
			}
			return null;
		}
		default:
			return null;
		}
	}
}
