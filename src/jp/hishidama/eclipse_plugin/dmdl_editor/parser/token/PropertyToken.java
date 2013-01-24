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
}
