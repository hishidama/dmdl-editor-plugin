package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

public class DMDLTextToken extends DMDLToken {

	protected String text;

	public DMDLTextToken(int start, int end, String text) {
		super(start, end);
		this.text = text;
	}

	public String getBody() {
		return text;
	}

	public String toString(String name) {
		return name + "(" + text + ")";
	}

	@Override
	public DMDLToken getTokenByOffset(int offset) {
		if (start <= offset && offset < end) {
			return this;
		}
		return null;
	}
}
