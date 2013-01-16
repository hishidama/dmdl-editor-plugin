package jp.hishidama.eclipse_plugin.dmdl_editor.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class DMAnnotationRule implements IRule {
	private IToken token;

	/**
	 * コンストラクター.
	 *
	 * @param token
	 */
	public DMAnnotationRule(IToken token) {
		Assert.isNotNull(token);
		this.token = token;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		int c = scanner.read();
		if (c == '@') {
			do {
				c = scanner.read();
			} while (isWordPart((char) c));
			scanner.unread();

			return token;
		}

		scanner.unread();
		return Token.UNDEFINED;
	}

	protected boolean isWordPart(char c) {
		return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z')
				|| ('0' <= c && c <= '9') || c == '.' || c == '_';
	}
}
