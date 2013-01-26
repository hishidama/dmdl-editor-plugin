package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken.WordType;

public class ModelToken extends DMDLBodyToken {
	private WordToken nameToken;

	public ModelToken(int start, int end, List<DMDLToken> bodyList) {
		super(start, end, bodyList);
		parse();
	}

	protected void parse() {
		WordToken prevWord = null;
		WordType typeOfNext = null;
		WordToken refModel = null;
		for (DMDLToken token : bodyList) {
			if (token instanceof WordToken) {
				WordToken word = (WordToken) token;
				switch (word.getWordType()) {
				case COMMA:
					continue;
				case DEF:
					if (prevWord != null && nameToken == null) {
						setNameToken(prevWord);
					}
					typeOfNext = WordType.REF_MODEL_NAME;
					prevWord = null;
					refModel = null;
					continue;
				case ALLOW:
					if (prevWord != null) {
						prevWord.setWordType(WordType.REF_MODEL_NAME);
						refModel = word;
					}
					prevWord = null;
					continue;
				case PERCENT:
					if (prevWord != null) {
						prevWord.setWordType(WordType.REF_MODEL_NAME);
						refModel = word;
					}
					prevWord = null;
					typeOfNext = WordType.REF_PROPERTY_NAME;
					continue;
				case PLUS:
					typeOfNext = WordType.REF_MODEL_NAME;
					prevWord = null;
					refModel = null;
					continue;
				}
				if (typeOfNext != null) {
					word.setWordType(typeOfNext);
					switch (typeOfNext) {
					case REF_MODEL_NAME:
						refModel = word;
						typeOfNext = null;
						break;
					case REF_PROPERTY_NAME:
						word.setRefModelName(refModel);
						break;
					}
					prevWord = null;
					continue;
				}
				if (refModel != null) {

				}
				prevWord = word;
				continue;
			}
			if (token instanceof CommentToken
					|| token instanceof DescriptionToken) {
				continue;
			}
			if (token instanceof BlockToken) {
				token.setRefModelName(refModel);
			}
			prevWord = null;
			typeOfNext = null;
		}
	}

	public void setNameToken(WordToken token) {
		nameToken = token;
		if (token != null) {
			token.setWordType(WordType.MODEL_NAME);
		}
	}

	@Override
	public String toString() {
		return toString("ModelToken", "  ");
	}

	public WordToken getModelNameToken() {
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

	public PropertyToken findProperty(String name) {
		for (PropertyToken prop : getPropertyList()) {
			if (name.equals(prop.getName())) {
				return prop;
			}
		}
		return null;
	}
}
