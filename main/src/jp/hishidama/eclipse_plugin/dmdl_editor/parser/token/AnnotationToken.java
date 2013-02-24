package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.style.DMScanner.AttrType;

public class AnnotationToken extends DMDLTextToken {

	public AnnotationToken(int start, int end, String text) {
		super(start, end, text);
	}

	@Override
	public String toString() {
		return toString("AnnotationToken");
	}

	@Override
	public AttrType getStyleAttribute() {
		return AttrType.ANNOTATION;
	}
}
