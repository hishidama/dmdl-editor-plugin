package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.DMScanner.AttrType;

public class WordToken extends DMDLTextToken {
	public static enum WordType {
		UNKNOWN, DEF, COMMA, ALLOW, PERCENT,
		// モデル名
		MODEL_NAME, REF_MODEL_NAME, MODEL_TYPE,
		// プロパティー
		PROPERTY_NAME, REF_PROPERTY_NAME, TYPE_SEPARATOR, DATA_TYPE,
		// 集計モデル
		SUMMARIZED_MODEL, SUMMARIZED_DEF, SUMMARIZED_TYPE,
		// 結合モデル
		JOIN_MODEL, PLUS,
		// 射影モデル
		PROJECTIVE_MODEL,
	}

	static final Set<String> MODEL_TYPE = new HashSet<String>();
	static {
		String[] ss = { "joined", "summarized", "projective" };
		for (String s : ss) {
			MODEL_TYPE.add(s);
		}
	}
	static final Set<String> DMDL_PROPERTY_TYPE = new HashSet<String>();
	static {
		String[] ss = { "INT", "LONG", "FLOAT", "DOUBLE", "TEXT", "DECIMAL",
				"DATE", "DATETIME", "BOOLEAN", "BYTE", "SHORT" };
		for (String s : ss) {
			DMDL_PROPERTY_TYPE.add(s);
		}
	}
	static final Set<String> SUMMARIZED_TYPE = new HashSet<String>();
	static {
		String[] ss = { "any", "sum", "max", "min", "count" };
		for (String s : ss) {
			MODEL_TYPE.add(s);
		}
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
	public AttrType getStyleAttribute() {
		String s = getBody();
		switch (getWordType()) {
		case MODEL_TYPE:
			if (MODEL_TYPE.contains(s)) {
				return AttrType.MODEL_TYPE;
			}
			break;
		case DATA_TYPE:
			if (DMDL_PROPERTY_TYPE.contains(s)) {
				return AttrType.DATA_TYPE;
			}
			break;
		}
		return AttrType.DEFAULT;
	}

	@Override
	public WordToken getReferenceWord() {
		switch (getWordType()) {
		case REF_MODEL_NAME: {
			ModelToken refModel = findModel(getBody());
			if (refModel != null) {
				WordToken refName = refModel.getModelNameToken();
				if (refName != null) {
					return refName;
				}
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
					}
				}
			}
			return null;
		}
		default:
			return null;
		}
	}
}