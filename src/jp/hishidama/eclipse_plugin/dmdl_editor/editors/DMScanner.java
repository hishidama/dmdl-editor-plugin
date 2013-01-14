package jp.hishidama.eclipse_plugin.dmdl_editor.editors;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

/**
 * データモデルScanner.
 */
public class DMScanner extends RuleBasedScanner {
	static final String[] DMDL_PROPERTY_TYPE = { "INT", "LONG", "FLOAT",
			"DOUBLE", "TEXT", "DECIMAL", "DATE", "DATETIME", "BOOLEAN", "BYTE",
			"SHORT" };
	static final String[] MODEL_TYPE = { "joined", "summarized", "projective" };
	static final String[] SUMMARIZED_TYPE = { "any", "sum", "max", "min",
			"count" };

	/**
	 * コンストラクター.
	 *
	 * @param colorManager
	 */
	public DMScanner(ColorManager colorManager) {
		RGB c = new RGB(192, 0, 0);
		IToken typeToken = new Token(new TextAttribute(
				colorManager.getColor(c), null, 0));
		IToken modelToken = new Token(new TextAttribute(
				colorManager.getColor(c), null, SWT.BOLD));
		IToken sumToken = new Token(new TextAttribute(colorManager.getColor(c),
				null, 0));

		IRule[] rules = { new DMWordRule(DMDL_PROPERTY_TYPE, typeToken),
				new DMWordRule(MODEL_TYPE, modelToken),
				new DMWordRule(SUMMARIZED_TYPE, sumToken), };
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
