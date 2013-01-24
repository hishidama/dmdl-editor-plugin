package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

public class CommentToken extends DMDLTextToken {

	protected boolean block;

	public CommentToken(int start, int end, String text, boolean block) {
		super(start, end, text);
		this.block = block;
	}

	public boolean isBlock() {
		return block;
	}

	@Override
	public String toString() {
		return toString("CommentToken");
	}
}
