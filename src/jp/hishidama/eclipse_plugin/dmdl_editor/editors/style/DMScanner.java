package jp.hishidama.eclipse_plugin.dmdl_editor.editors.style;

import java.util.HashSet;
import java.util.Set;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.AnnotationToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.CommentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DescriptionToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * データモデルScanner.
 */
public class DMScanner implements ITokenScanner {
	static final Set<String> MODEL_TYPE = new HashSet<String>();
	static {
		String[] ss = { "joined", "summarized", "projective" };
		for (String s : ss) {
			MODEL_TYPE.add(s);
		}
	}
	static final Set<String> DMDL_PROPERTY_TYPE = new HashSet<String>();
	static {
		String[] ss = { "INT", "LONG", "FLOAT", "DOUBLE", "TEXT", "DECIMAL",
				"DATE", "DATETIME", "BOOLEAN", "BYTE", "SHORT" };
		for (String s : ss) {
			DMDL_PROPERTY_TYPE.add(s);
		}
	}
	static final Set<String> SUMMARIZED_TYPE = new HashSet<String>();
	static {
		String[] ss = { "any", "sum", "max", "min", "count" };
		for (String s : ss) {
			MODEL_TYPE.add(s);
		}
	}

	private AttributeManager attrManager;

	/**
	 * コンストラクター.
	 *
	 * @param attrManager
	 */
	public DMScanner(AttributeManager attrManager) {
		this.attrManager = attrManager;
		initialize();
	}

	protected IToken commentToken;
	protected IToken modelToken;
	protected IToken typeToken;
	protected IToken sumToken;
	protected IToken annToken;
	protected IToken descToken;
	protected IToken defaultToken;

	public void initialize() {
		commentToken = new Token(attrManager.getCommentAttribute());
		modelToken = new Token(attrManager.getModelTypeAttribute());
		typeToken = new Token(attrManager.getDataTypeAttribute());
		sumToken = new Token(attrManager.getSumTypeAttribute());
		annToken = new Token(attrManager.getAnnotationAttribute());
		descToken = new Token(attrManager.getDescriptionAttribute());
		defaultToken = new Token(attrManager.getDefaultAttribute());
	}

	protected DMDLTextTokenIterator tokenIterator;
	protected int firstOffset;
	protected int charOffset;

	@Override
	public void setRange(IDocument document, int offset, int length) {
		DMDLDocument doc = (DMDLDocument) document;
		ModelList models = doc.getModelList();
		for (DMDLToken model : models.getBody()) {
			if (model.getStart() <= offset && offset < model.getEnd()) {
				tokenIterator = new DMDLTextTokenIterator(model, offset);
				break;
			}
		}
		firstOffset = charOffset = offset;
	}

	protected int tokenOffset;
	protected int tokenLength;
	protected DMDLToken backupToken;

	@Override
	public IToken nextToken() {
		DMDLToken token = getNextToken();
		if (token != null) {
			if (charOffset < token.getStart()) {
				tokenOffset = charOffset;
				charOffset = token.getStart();
				tokenLength = charOffset - tokenOffset;
				backupToken = token;
				return defaultToken;
			}

			tokenOffset = token.getStart();
			tokenLength = token.getLength();
			charOffset = token.getEnd();
			// TODO instanceofを使わないよう修正
			if (token instanceof CommentToken) {
				return commentToken;
			} else if (token instanceof DescriptionToken) {
				return descToken;
			} else if (token instanceof AnnotationToken) {
				return annToken;
			} else if (token instanceof WordToken) {
				WordToken word = (WordToken) token;
				switch (word.getWordType()) {
				case DATA_TYPE:
					if (DMDL_PROPERTY_TYPE.contains(word.getBody())) {
						return typeToken;
					}
					break;
				}
			}
			return defaultToken;
		}
		return Token.EOF;
	}

	protected DMDLToken getNextToken() {
		if (backupToken != null) {
			DMDLToken r = backupToken;
			backupToken = null;
			return r;
		}
		if (tokenIterator != null) {
			for (;;) {
				DMDLToken token = tokenIterator.next();
				if (token == null) {
					return null;
				}
				if (token.getEnd() < firstOffset) {
					continue;
				}
				return token;
			}
		}
		return null;
	}

	@Override
	public int getTokenOffset() {
		return tokenOffset;
	}

	@Override
	public int getTokenLength() {
		return tokenLength;
	}
}
