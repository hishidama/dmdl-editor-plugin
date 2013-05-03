package jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token;

import java.util.List;

public class ArrayToken extends DMDLBodyToken {

	public ArrayToken(int start, int end, List<DMDLToken> bodyList) {
		super(start, end, bodyList);
	}

	@Override
	public String toString() {
		return toString("ArrayToken", "    ");
	}
}
