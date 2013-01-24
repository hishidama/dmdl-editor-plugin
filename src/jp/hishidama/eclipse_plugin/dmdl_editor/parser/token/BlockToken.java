package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

import java.util.List;

public class BlockToken extends DMDLBodyToken {

	public BlockToken(int start, int end, List<DMDLToken> bodyList) {
		super(start, end, bodyList);
	}

	@Override
	public String toString() {
		return toString("BlockToken", "    ");
	}
}
