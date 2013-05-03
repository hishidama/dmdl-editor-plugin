package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.style.partition;

import java.util.Iterator;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.DMDLSimpleParser;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.DMDLSimpleScanner;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.DocumentScanner;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelList;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class DMDLPartitionScanner implements IPartitionTokenScanner {
	public final static String DMDL_BLOCK = "__dmdl_block";
	protected final IToken blockToken = new Token(DMDL_BLOCK);
	protected final IToken defaultToken = new Token(null);

	protected DMDLSimpleParser parser = new DMDLSimpleParser();

	/**
	 * コンストラクター.
	 */
	public DMDLPartitionScanner() {
	}

	protected int firstOffset;
	protected Iterator<DMDLToken> modelIterator;

	@Override
	public void setRange(IDocument document, int offset, int length) {
		setPartialRange(document, offset, length, null, -1);
	}

	@Override
	public void setPartialRange(IDocument document, int offset, int length,
			String contentType, int partitionOffset) {
		DMDLDocument doc = (DMDLDocument) document;
		DMDLSimpleScanner scanner = new DocumentScanner(doc);
		ModelList models = parser.parse(scanner);
		// TODO 毎回計算しなおすのはやめたい
		if (partitionOffset >= 0) {
			// models = parser.parse(document, models, partitionOffset);
		} else {
			// models = parser.parse(document, models, offset);
		}
		doc.setModelList(models);
		firstOffset = charOffset = offset;
		modelIterator = models.getBody().iterator();
	}

	protected int tokenOffset;
	protected int tokenLength;
	protected int charOffset;
	protected DMDLToken backupToken;

	@Override
	public IToken nextToken() {

		for (;;) {
			DMDLToken t = getNextToken();
			if (t == null) {
				return Token.EOF;
			}
			if (t.getEnd() <= firstOffset) {
				continue;
			}
			if (charOffset < t.getStart()) {
				tokenOffset = charOffset;
				charOffset = t.getStart();
				tokenLength = charOffset - tokenOffset;
				backupToken = t;
				return defaultToken;
			}
			tokenOffset = t.getStart();
			tokenLength = t.getLength();
			charOffset = t.getEnd();
			return blockToken;
		}
	}

	protected DMDLToken getNextToken() {
		if (backupToken != null) {
			DMDLToken r = backupToken;
			backupToken = null;
			return r;
		}
		if (modelIterator.hasNext()) {
			return modelIterator.next();
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
