package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

import java.util.List;

public class DMDLBodyToken extends DMDLToken {

	protected List<DMDLToken> bodyList;

	public DMDLBodyToken(int start, int end, List<DMDLToken> bodyList) {
		super(start, end);
		this.bodyList = bodyList;
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
}
