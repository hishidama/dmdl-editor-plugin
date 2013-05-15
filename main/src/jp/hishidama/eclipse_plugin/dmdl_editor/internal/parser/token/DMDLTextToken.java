package jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token;

public class DMDLTextToken extends DMDLToken {

	protected String text;

	public DMDLTextToken(int start, int end, String text) {
		super(start, end);
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public String getText(int start, int end) {
		int offset = getStart();
		int s = Math.max(start, offset);
		int e = Math.min(end, getEnd());
		if (s <= e) {
			s -= offset;
			e -= offset;
			return text.substring(s, e);
		} else {
			return "";
		}
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
