package jp.hishidama.eclipse_plugin.dmdl_editor.editors.style;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

/**
 * データモデルScanner.
 */
public class DMScanner extends RuleBasedScanner {
	static final String[] MODEL_TYPE = { "joined", "summarized", "projective" };
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
	public DMScanner(AttributeManager attrManager) {
		this.attrManager = attrManager;
		initialize();
	}

	public void initialize() {
		IToken commentToken = new Token(attrManager.getCommentAttribute());
		IToken modelToken = new Token(attrManager.getModelTypeAttribute());
		IToken typeToken = new Token(attrManager.getDataTypeAttribute());
		IToken sumToken = new Token(attrManager.getSumTypeAttribute());
		IToken annToken = new Token(attrManager.getAnnotationAttribute());
		IToken descToken = new Token(attrManager.getDescriptionAttribute());
		IToken defaultToken = new Token(attrManager.getDefaultAttribute());

		DMWordRule wordRule = new DMWordRule(defaultToken);
		wordRule.addWords(MODEL_TYPE, modelToken);
		wordRule.addWords(DMDL_PROPERTY_TYPE, typeToken);
		wordRule.addWords(SUMMARIZED_TYPE, sumToken);

		IRule[] rules = { new EndOfLineRule("--", commentToken),
				new EndOfLineRule("//", commentToken),
				new MultiLineRule("/*", "*/", commentToken),
				new DMAnnotationRule(annToken),
				new SingleLineRule("\"", "\"", descToken), wordRule, };
		setRules(rules);
	}
}
