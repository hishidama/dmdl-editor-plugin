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

	/**
	 * コンストラクター.
	 *
	 * @param colorManager
	 */
	public DMScanner(ColorManager colorManager) {
		RGB c = new RGB(192, 0, 0);
		int style = SWT.BOLD;
		IToken typeToken = new Token(new TextAttribute(
				colorManager.getColor(c), null, style));

		IRule[] rules = { new DMWordRule(DMDL_PROPERTY_TYPE, typeToken), };
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
		@Override
		public boolean isWordStart(char c) {
			return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
		}

		@Override
		public boolean isWordPart(char c) {
			return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
		}
	}
}
