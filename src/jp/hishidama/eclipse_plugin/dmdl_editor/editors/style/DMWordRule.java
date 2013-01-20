package jp.hishidama.eclipse_plugin.dmdl_editor.editors.style;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.WordRule;

public class DMWordRule extends WordRule {
	/**
	 * コンストラクター.
	 *
	 * @param defaultToken
	 */
	public DMWordRule(IToken defaultToken) {
		super(new DMWordDetector(), defaultToken);
	}

	public void addWords(String[] words, IToken token) {
		for (String word : words) {
			addWord(word, token);
		}
	}

	static class DMWordDetector implements IWordDetector {
		protected boolean accept(char c) {
			switch (c) {
			case ' ':
			case '\t':
			case '\r':
			case '\n':
			case '@':
			case '=':
			case ',':
			case '.':
			case '+':
			case '&':
			case '%':
			case ':':
			case ';':
			case '-':
			case '>':
			case '{':
			case '}':
			case '(':
			case ')':
			case '/':
			case '*':
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