package jp.hishidama.eclipse_plugin.dmdl_editor.editors.style;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

public class DMDLPartitionScanner extends RuleBasedPartitionScanner {
	public final static String DMDL_COMMENT = "__dmdl_comment";
	public final static String DMDL_BLOCK = "__dmdl_block";

	/**
	 * コンストラクター.
	 */
	public DMDLPartitionScanner() {
		IToken commentToken = new Token(DMDL_COMMENT);
		IToken blockToken = new Token(DMDL_BLOCK);

		IPredicateRule[] rules = { new EndOfLineRule("--", commentToken),
				new EndOfLineRule("//", commentToken),
				new MultiLineRule("/*", "*/", commentToken),
				new MultiLineRule("{", "}", blockToken), };
		setPredicateRules(rules);
	}
}
