package jp.hishidama.eclipse_plugin.dmdl_editor.editors.style;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelList;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * データモデルScanner.
 */
public class DMScanner implements ITokenScanner {
	public static enum AttrType {
		DEFAULT, COMMENT, MODEL_TYPE, DATA_TYPE, SUM_TYPE, ANNOTATION, DESCRIPTION,
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

			switch (token.getStyleAttribute()) {
			case COMMENT:
				return commentToken;
			case MODEL_TYPE:
				return modelToken;
			case DATA_TYPE:
				return typeToken;
			case SUM_TYPE:
				return sumToken;
			case ANNOTATION:
				return annToken;
			case DESCRIPTION:
				return descToken;
			default:
				break;
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
