package jp.hishidama.eclipse_plugin.dmdl_editor.parser;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

public class DocumentScanner {
	public static final char EOF = (char) -1;

	protected IDocument document;
	protected int offset = 0;
	protected int eof = 0;

	public DocumentScanner(IDocument document) {
		this.document = document;
	}

	public char read() {
		if (offset < document.getLength()) {
			char c;
			try {
				c = document.getChar(offset);
			} catch (BadLocationException e) {
				throw new IllegalStateException(e);
			}
			offset++;
			return c;
		}

		eof++;
		return EOF;
	}

	public void unread() {
		if (eof > 0) {
			eof--;
		} else {
			offset--;
		}
	}

	public int getOffset() {
		return offset;
	}

	public String getString(int start, int end) {
		int length = end - start;
		try {
			return document.get(start, length);
		} catch (BadLocationException e) {
			StringBuilder sb = new StringBuilder(length);
			for (int i = 0; i < length; i++) {
				try {
					char c = document.getChar(start + i);
					sb.append(c);
				} catch (BadLocationException e1) {
					break;
				}
			}
			return sb.toString();
		}
	}

	public int getLine(int offset) {
		try {
			return document.getLineOfOffset(offset);
		} catch (BadLocationException e) {
			return 0;
		}
	}

	public int getColumn(int offset) {
		int line = getLine(offset);
		try {
			return offset - document.getLineOffset(line);
		} catch (BadLocationException e) {
			return 0;
		}
	}
}