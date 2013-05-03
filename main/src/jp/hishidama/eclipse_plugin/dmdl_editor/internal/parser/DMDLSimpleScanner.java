package jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser;

public interface DMDLSimpleScanner {
	public static final char EOF = (char) -1;

	public int getLength();

	public char read();

	public void unread();

	public int getOffset();

	public String getString(int start, int end);
}
