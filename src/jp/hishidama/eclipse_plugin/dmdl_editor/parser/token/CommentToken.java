package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

public class CommentToken extends DMDLTextToken {

	protected boolean top;

	public CommentToken(int start, int end, String text, boolean top) {
		super(start, end, text);
		this.top = top;
	}

	public boolean isTop() {
		return top;
	}

	@Override
	public String toString() {
		return toString("CommentToken");
	}
}
