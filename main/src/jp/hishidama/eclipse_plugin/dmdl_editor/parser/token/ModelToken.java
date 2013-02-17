package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken.WordType;

public class ModelToken extends DMDLBodyToken {
	private WordToken nameToken;
	private WordToken modelTypeToken;

	public ModelToken(int start, int end, List<DMDLToken> bodyList) {
		super(start, end, bodyList);
		parse();
	}

	protected void parse() {
		WordToken prevWord = null;
		WordType typeOfNext = null;
		DMDLToken refModel = null;
		for (int i = 0; i < bodyList.size(); i++) {
			DMDLToken token = bodyList.get(i);
			if (token instanceof WordToken) {
				WordToken word = (WordToken) token;
				switch (word.getWordType()) {
				case COMMA:
					continue;
				case DEF:
					if (prevWord != null && nameToken == null) {
						setNameToken(prevWord);
						parseModelType(prevWord);
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
					if (i > 0) {
						for (int j = i - 1; j >= 0; j--) {
							DMDLToken t = bodyList.get(j);
							if (t instanceof WordToken) {
								WordToken w = (WordToken) t;
								w.setWordType(WordType.REF_MODEL_NAME);
								refModel = word;
								break;
							} else if (t instanceof BlockToken) {
								refModel = t;
								break;
							}
						}
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
						word.setRefModelToken(refModel);
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
				token.setRefModelToken(refModel);
			}
			prevWord = null;
			typeOfNext = null;
		}
	}

	protected void parseModelType(WordToken nameToken) {
		for (DMDLToken token : bodyList) {
			if (token == nameToken) {
				break;
			}
			if (token instanceof WordToken) {
				setModelTypeToken((WordToken) token);
				return;
			}
		}
		setModelTypeToken(null);
	}

	public void setNameToken(WordToken token) {
		if (nameToken != null) {
			nameToken.setWordType(WordType.UNKNOWN);
		}
		nameToken = token;
		if (token != null) {
			token.setWordType(WordType.MODEL_NAME);
		}
	}

	public void setModelTypeToken(WordToken token) {
		if (modelTypeToken != null) {
			modelTypeToken.setWordType(WordType.UNKNOWN);
		}
		modelTypeToken = token;
		if (token != null) {
			String text = token.getBody();
			if (text.equals("summarized")) {
				token.setWordType(WordType.SUMMARIZED_MODEL);
				parseSummarized();
			} else if (text.equals("joined")) {
				token.setWordType(WordType.JOIN_MODEL);
			} else if (text.equals("projective")) {
				token.setWordType(WordType.PROJECTIVE_MODEL);
			}
		}
	}

	protected void parseSummarized() {
		for (DMDLToken token : bodyList) {
			if (token instanceof BlockToken) {
				((BlockToken) token).parseSummarized();
			}
		}
	}

	@Override
	public String toString() {
		return toString("ModelToken", "  ");
	}

	@Override
	public ModelToken getModelToken() {
		return this;
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

	public WordToken getModelTypeToken() {
		return modelTypeToken;
	}

	@Override
	public String getModelType() {
		WordToken token = getModelTypeToken();
		if (token != null) {
			return token.getBody();
		}
		return null;
	}

	public String getDescription() {
		for (DMDLToken token : bodyList) {
			if (token instanceof DescriptionToken) {
				DescriptionToken desc = (DescriptionToken) token;
				return desc.getBody();
			}
		}
		return null;
	}

	public String getQualifiedName() {
		StringBuilder sb = new StringBuilder(64);

		String desc = getDescription();
		if (desc != null) {
			sb.append(desc);
			sb.append(" ");
		}

		String type = getModelType();
		if (type != null) {
			sb.append(type);
			sb.append(" ");
		}

		String name = getModelName();
		sb.append(name);

		return sb.toString();
	}
}
