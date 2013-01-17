package jp.hishidama.eclipse_plugin.dmdl_editor.editors;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

/**
 * データモデル外側Scanner.
 */
public class DMDefaultScanner extends RuleBasedScanner {
	static final String[] MODEL_TYPE = { "joined", "summarized", "projective" };

	/**
	 * コンストラクター.
	 *
	 * @param colorManager
	 */
	public DMDefaultScanner(ColorManager colorManager) {
		RGB c = new RGB(192, 0, 0);
		IToken modelToken = new Token(new TextAttribute(
				colorManager.getColor(c), null, SWT.BOLD));
		IToken annToken = new Token(new TextAttribute(colorManager.getColor(c),
				null, SWT.BOLD));
		IToken descToken = new Token(new TextAttribute(
				colorManager.getColor(new RGB(0, 0, 192)), null, SWT.NORMAL));

		IRule[] rules = { new DMWordRule(MODEL_TYPE, modelToken),
				new DMAnnotationRule(annToken),
				new SingleLineRule("\"", "\"", descToken), };
		setRules(rules);
	}

	static class DMWordRule extends WordRule {
		/** コンストラクター. */
		public DMWordRule(String[] words, IToken token) {
			super(new DMWordDetector());
			for (String word : words) {
				addWord(word, token);
			}
		}
	}

	static class DMWordDetector implements IWordDetector {
		protected boolean accept(char c) {
			switch (c) {
			case ' ':
			case '\t':
			case '\r':
			case '\n':
			case ':':
			case ';':
			case '%':
			case '=':
			case '-':
			case '+':
				return false;
			default:
				return true;
			}
		}

		@Override
		public boolean isWordStart(char c) {
			return accept(c);
		}

		@Override
		public boolean isWordPart(char c) {
			return accept(c);
		}
	}
}
