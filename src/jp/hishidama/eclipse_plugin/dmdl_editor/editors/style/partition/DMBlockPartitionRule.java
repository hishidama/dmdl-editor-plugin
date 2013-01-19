package jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.partition;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * データモデルブロック Partitionルール.
 * <p>
 * 波括弧で囲まれた部分「{～}」をPartitionにする為のルール。
 * </p>
 *
 * @see org.eclipse.jface.text.rules.PatternRule
 */
public class DMBlockPartitionRule implements IPredicateRule {
	/** The token to be returned on success */
	protected IToken fToken;

	public DMBlockPartitionRule(IToken token) {
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
			if (endSequenceDetected(scanner)) {
				return fToken;
			}
		} else {
			int c = scanner.read();
			switch (c) {
			case '{':
				if (endSequenceDetected(scanner)) {
					return fToken;
				}
				break;
			}
		}

		scanner.unread();
		return Token.UNDEFINED;
	}

	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		for (;;) {
			int c = scanner.read();
			switch (c) {
			case ICharacterScanner.EOF:
				return true;
			case '}':
				return true;
			case '{':
				scanner.unread();
				return true;
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
					readToCommentEnd(scanner);
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

	protected void readToCommentEnd(ICharacterScanner scanner) {
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