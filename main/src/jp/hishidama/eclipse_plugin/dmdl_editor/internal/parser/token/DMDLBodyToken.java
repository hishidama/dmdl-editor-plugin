package jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken.WordType;

public class DMDLBodyToken extends DMDLToken {

	protected List<DMDLToken> bodyList;

	public DMDLBodyToken(int start, int end, List<DMDLToken> bodyList) {
		super(start, end);
		this.bodyList = bodyList;
		for (DMDLToken token : bodyList) {
			token.setParent(this);
		}
	}

	public List<DMDLToken> getBody() {
		return bodyList;
	}

	public String toString(String name, String tab) {
		if (bodyList.isEmpty()) {
			return name + "()";
		} else if (bodyList.size() == 1) {
			return name + "(" + bodyList.get(0) + ")";
		}

		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append("(\n");
		for (DMDLToken t : bodyList) {
			sb.append(tab);
			sb.append(t);
			sb.append('\n');
		}
		sb.setLength(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}

	public int indexOf(WordType type) {
		for (int i = 0; i < bodyList.size(); i++) {
			DMDLToken token = bodyList.get(i);
			if (token instanceof WordToken) {
				WordToken t = (WordToken) token;
				if (t.getWordType() == type) {
					return i;
				}
			}
		}
		return -1;
	}

	public WordToken findWord(int n, int step) {
		while (0 <= n && n < bodyList.size()) {
			DMDLToken token = bodyList.get(n);
			if (token instanceof WordToken) {
				return (WordToken) token;
			}
			n += step;
		}
		return null;
	}

	public WordToken findWord(String text) {
		for (DMDLToken token : bodyList) {
			if (token instanceof WordToken) {
				WordToken word = (WordToken) token;
				if (text.equals(word.getText())) {
					return word;
				}
			}
		}
		return null;
	}

	@Override
	public DMDLToken getTokenByOffset(int offset) {
		if (start <= offset && offset < end) {
			for (DMDLToken token : bodyList) {
				DMDLToken found = token.getTokenByOffset(offset);
				if (found != null) {
					return found;
				}
			}
			return this;
		}
		return null;
	}

	private List<PropertyToken> propList;

	public List<PropertyToken> getPropertyList() {
		if (propList == null) {
			propList = new ArrayList<PropertyToken>();
			addProperty(propList, this, new HashSet<DMDLToken>());

			String modelType = getModelType();
			if ("joined".equals(modelType)) {
				List<PropertyToken> list = new ArrayList<PropertyToken>(
						propList.size());
				Set<String> set = new HashSet<String>();
				for (PropertyToken prop : propList) {
					String name = prop.getName();
					if (!set.contains(name)) {
						set.add(name);
						list.add(prop);
					}
				}
				propList = list;
			}
		}
		return propList;
	}

	private void addProperty(List<PropertyToken> list, DMDLToken token,
			Set<DMDLToken> set) {
		if (token == null) {
			return;
		}
		if (set.contains(token)) {
			return;
		}
		set.add(token);

		if (token instanceof PropertyToken) {
			PropertyToken prop = (PropertyToken) token;
			if (prop.getPropertyNameToken() != null) {
				list.add(prop);
			}
		} else if (token instanceof ModelToken) {
			List<DMDLToken> l = ((ModelToken) token).getBody();
			for (int i = 0; i < l.size(); i++) {
				DMDLToken t = l.get(i);
				if (t instanceof WordToken) {
					WordToken word = (WordToken) t;
					if (word.getWordType() == WordType.REF_MODEL_NAME) {
						if (!nextIs(l, i + 1, "=>", "->")) {
							ModelToken refModel = findModel(word.getText());
							addProperty(list, refModel, set);
						}
					}
				} else {
					addProperty(list, t, set);
				}
			}
		} else if (token instanceof DMDLBodyToken) {
			for (DMDLToken t : ((DMDLBodyToken) token).getBody()) {
				addProperty(list, t, set);
			}
		}
	}

	private boolean nextIs(List<DMDLToken> list, int n, String text1,
			String text2) {
		DMDLToken token = getNextToken(list, n);
		if (token == null) {
			return false;
		}
		if (token instanceof WordToken) {
			String word = ((WordToken) token).getText();
			return word.equals(text1) || word.equals(text2);
		}
		return false;
	}

	private DMDLToken getNextToken(List<DMDLToken> list, int n) {
		for (; n < list.size(); n++) {
			DMDLToken token = list.get(n);
			if (token instanceof CommentToken
					|| token instanceof DescriptionToken) {
				continue;
			}
			return token;
		}
		return null;
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
