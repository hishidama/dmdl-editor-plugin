package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken.WordType;

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
}
