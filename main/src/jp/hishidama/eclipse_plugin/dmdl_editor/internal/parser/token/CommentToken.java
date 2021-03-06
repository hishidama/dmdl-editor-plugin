package jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.style.DMScanner.AttrType;

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

	@Override
	public AttrType getStyleAttribute() {
		return AttrType.COMMENT;
	}
}
