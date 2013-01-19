package jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.partition;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

public class DMDLPartitionScanner extends RuleBasedPartitionScanner {
	public final static String DMDL_BLOCK = "__dmdl_block";

	/**
	 * コンストラクター.
	 */
	public DMDLPartitionScanner() {
		IToken blockToken = new Token(DMDL_BLOCK);

		IPredicateRule[] rules = { new DMDLPartitionRule(blockToken) };
		setPredicateRules(rules);
	}
}
