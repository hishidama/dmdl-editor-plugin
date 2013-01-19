package jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.partition;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * データモデル Partitionルール.
 *
 * @see org.eclipse.jface.text.rules.PatternRule
 */
public class DMDLPartitionRule implements IPredicateRule {
	/** The token to be returned on success */
	protected IToken fToken;

	/**
	 * コンストラクター.
	 *
	 * @param token
	 *            解釈成功時に返すToken
	 */
	public DMDLPartitionRule(IToken token) {
		fToken = token;
	}

	@Override
	public IToken getSuccessToken() {
		return fToken;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		if (resume) {
			int c = scanner.read();
			if (c == ICharacterScanner.EOF) {
				scanner.unread();
				return Token.EOF;
			}
			endSequenceDetected(scanner);
			return fToken;
		}

		int c = scanner.read();
		switch (c) {
		case ICharacterScanner.EOF:
			return Token.EOF;
		case ' ':
		case '\t':
		case '\r':
		case '\n':
			scanner.unread();
			return Token.UNDEFINED;
		}
		scanner.unread();
		endSequenceDetected(scanner);
		return fToken;
	}

	protected void endSequenceDetected(ICharacterScanner scanner) {
		for (;;) {
			int c = scanner.read();
			switch (c) {
			case ICharacterScanner.EOF:
				return;
			case ';':
				return;
			case '{':
				readBlock(scanner);
				break;
			case '-':
				switch (scanner.read()) {
				case '-':
					readToEOL(scanner);
					break;
				default:
					scanner.unread();
					break;
				}
				break;
			case '/':
				switch (scanner.read()) {
				case '/':
					readToEOL(scanner);
					break;
				case '*':
					readToCommentEnd(scanner, true);
					break;
				default:
					scanner.unread();
					break;
				}
				break;
			case '\"':
				readToDescriptionEnd(scanner);
				break;
			}
		}
	}

	protected void readBlock(ICharacterScanner scanner) {
		for (;;) {
			int c = scanner.read();
			switch (c) {
			case ICharacterScanner.EOF:
				return;
			case '}':
				return;
			case '-':
				switch (scanner.read()) {
				case '-':
					readToEOL(scanner);
					break;
				default:
					scanner.unread();
					break;
				}
				break;
			case '/':
				switch (scanner.read()) {
				case '/':
					readToEOL(scanner);
					break;
				case '*':
					readToCommentEnd(scanner, false);
					break;
				default:
					scanner.unread();
					break;
				}
				break;
			case '\"':
				readToDescriptionEnd(scanner);
				break;
			}
		}
	}

	protected void readToEOL(ICharacterScanner scanner) {
		for (;;) {
			int c = scanner.read();
			switch (c) {
			case ICharacterScanner.EOF:
			case '\r':
			case '\n':
				return;
			}
		}
	}

	protected void readToCommentEnd(ICharacterScanner scanner, boolean top) {
		for (;;) {
			int c = scanner.read();
			switch (c) {
			case ICharacterScanner.EOF:
				return;
			case '*':
				c = scanner.read();
				if (c == '/') {
					return;
				} else {
					scanner.unread();
				}
				break;
			}
		}
	}

	protected void readToDescriptionEnd(ICharacterScanner scanner) {
		for (;;) {
			int c = scanner.read();
			switch (c) {
			case ICharacterScanner.EOF:
			case '\r':
			case '\n':
				return;
			case '\"':
				return;
			case '\\':
				c = scanner.read();
				if (c == ICharacterScanner.EOF) {
					return;
				}
				break;
			}
		}
	}
}