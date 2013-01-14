package jp.hishidama.eclipse_plugin.dmdl_editor.editors;

import org.eclipse.jface.text.rules.*;

public class WhitespaceScanner extends RuleBasedScanner {

	public WhitespaceScanner(ColorManager manager) {

		IRule[] rules = { new WhitespaceRule(new WhitespaceDetector()) };
		setRules(rules);
	}

	static class WhitespaceDetector implements IWhitespaceDetector {

		public boolean isWhitespace(char c) {
			return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
		}
	}
}
