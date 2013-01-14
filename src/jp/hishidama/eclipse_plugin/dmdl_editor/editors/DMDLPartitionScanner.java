package jp.hishidama.eclipse_plugin.dmdl_editor.editors;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

public class DMDLPartitionScanner extends RuleBasedPartitionScanner {
	public final static String DMDL_COMMENT = "__dmdl_comment";

	/**
	 * コンストラクター.
	 */
	public DMDLPartitionScanner() {
		IToken commentToken = new Token(DMDL_COMMENT);

		IPredicateRule[] rules = { new EndOfLineRule("--", commentToken),
				new EndOfLineRule("//", commentToken),
				new MultiLineRule("/*", "*/", commentToken), };
		setPredicateRules(rules);

		/*
		 * RGB c = new RGB(128, 0, 0); ColorManager colorManager = new
		 * ColorManager(); setDefaultReturnToken(new Token(new TextAttribute(
		 * colorManager.getColor(c))));
		 */
	}
}
