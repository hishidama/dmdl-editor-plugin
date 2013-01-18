package jp.hishidama.eclipse_plugin.dmdl_editor.editors.style;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

/**
 * データモデルブロックScanner.
 */
public class DMBlockScanner extends RuleBasedScanner {
	static final String[] DMDL_PROPERTY_TYPE = { "INT", "LONG", "FLOAT",
			"DOUBLE", "TEXT", "DECIMAL", "DATE", "DATETIME", "BOOLEAN", "BYTE",
			"SHORT" };
	static final String[] SUMMARIZED_TYPE = { "any", "sum", "max", "min",
			"count" };

	private AttributeManager attrManager;

	/**
	 * コンストラクター.
	 *
	 * @param colorManager
	 */
	public DMBlockScanner(AttributeManager attrManager) {
		this.attrManager = attrManager;
		initialize();
	}

	public void initialize() {
		IToken commentToken = new Token(attrManager.getCommentAttribute());
		IToken typeToken = new Token(attrManager.getDataTypeAttribute());
		IToken sumToken = new Token(attrManager.getSumTypeAttribute());
		IToken annToken = new Token(attrManager.getAnnotationAttribute());
		IToken descToken = new Token(attrManager.getDescriptionAttribute());

		IRule[] rules = { new EndOfLineRule("--", commentToken),
				new EndOfLineRule("//", commentToken),
				new MultiLineRule("/*", "*/", commentToken),
				new DMWordRule(DMDL_PROPERTY_TYPE, typeToken),
				new DMWordRule(SUMMARIZED_TYPE, sumToken),
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
