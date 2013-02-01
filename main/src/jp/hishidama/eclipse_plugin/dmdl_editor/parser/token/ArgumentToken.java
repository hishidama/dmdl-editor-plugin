package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

import java.util.List;

public class ArgumentToken extends DMDLBodyToken {

	public ArgumentToken(int start, int end, List<DMDLToken> bodyList) {
		super(start, end, bodyList);
	}

	@Override
	public String toString() {
		return toString("ArgumentToken", "      ");
	}
}
