package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

public class AnnotationToken extends DMDLTextToken {

	public AnnotationToken(int start, int end, String text) {
		super(start, end, text);
	}

	@Override
	public String toString() {
		return toString("AnnotationToken");
	}
}
