package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

public class DescriptionToken extends DMDLTextToken {

	public DescriptionToken(int start, int end, String text) {
		super(start, end, text);
	}

	@Override
	public String toString() {
		return toString("DescriptionToken");
	}
}
