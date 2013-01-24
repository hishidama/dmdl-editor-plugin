package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

public class WordToken extends DMDLTextToken {

	public WordToken(int start, int end, String text) {
		super(start, end, text);
	}

	@Override
	public String toString() {
		return toString("WordToken");
	}
}
