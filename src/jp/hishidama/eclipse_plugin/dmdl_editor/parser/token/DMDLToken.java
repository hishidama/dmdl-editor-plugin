package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

public class DMDLToken {

	protected int start;
	protected int end;

	public DMDLToken(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}
}
