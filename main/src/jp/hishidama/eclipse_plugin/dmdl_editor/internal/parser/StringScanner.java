package jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.DMDLSimpleScanner;

public class StringScanner implements DMDLSimpleScanner {
	public static final char EOF = (char) -1;

	protected String document;
	protected int offset = 0;
	protected int eof = 0;

	public StringScanner(String document) {
		this.document = document;
	}

	@Override
	public int getLength() {
		return document.length();
	}

	@Override
	public char read() {
		if (offset < document.length()) {
			char c = document.charAt(offset);
			offset++;
			return c;
		}

		eof++;
		return EOF;
	}

	@Override
	public void unread() {
		if (eof > 0) {
			eof--;
		} else {
			offset--;
		}
	}

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public String getString(int start, int end) {
		return document.substring(start, end);
	}
}
