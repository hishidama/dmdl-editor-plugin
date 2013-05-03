package jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.style.DMScanner.AttrType;

public class DescriptionToken extends DMDLTextToken {

	public DescriptionToken(int start, int end, String text) {
		super(start, end, text);
	}

	@Override
	public String toString() {
		return toString("DescriptionToken");
	}

	@Override
	public AttrType getStyleAttribute() {
		return AttrType.DESCRIPTION;
	}
}
