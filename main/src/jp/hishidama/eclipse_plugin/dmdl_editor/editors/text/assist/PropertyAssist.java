package jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.assist;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.BlockToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class PropertyAssist extends Assist {
	protected static final String[] TYPE_ASSIST = { "INT", "LONG", "FLOAT",
			"DOUBLE", "TEXT", "DECIMAL", "DATE", "DATETIME", "BOOLEAN", "BYTE",
			"SHORT" };

	public List<ICompletionProposal> getPropertyAssist(IDocument document,
			int offset, PropertyToken token) {
		String modelType = token.getModelType();
		if ("summarized".equals(modelType)) {
			return getSummarizedAssist(document, offset, token);
		}
		BlockToken block = (BlockToken) token.getParent();
		WordToken refModelName = (WordToken) block.getRefModelToken();
		if (refModelName != null) {
			return getRefAssist(document, offset, token, refModelName);
		}

		return getDefaultAssist(document, offset, token);
	}

	protected List<ICompletionProposal> getDefaultAssist(IDocument document,
			int offset, PropertyToken token) {
		List<DMDLToken> list = getList(token, offset);
		AssistMatcher matcher = new AssistMatcher(list, offset);
		switch (matcher.matchFirst(ANY, ":", ANY)) {
		case 0:
			return null;
		case 1:
			return matcher.createAssist(document, ":");
		case 2:
			return matcher.createAssist(document, TYPE_ASSIST);
		case 3:
			return matcher.createAssist(document, ";");
		default:
			return null;
		}
	}

	protected List<ICompletionProposal> getSummarizedAssist(IDocument document,
			int offset, PropertyToken token) {
		List<DMDLToken> list = getList(token, offset);
		AssistMatcher matcher = new AssistMatcher(list, offset);
		switch (matcher.matchFirst(ANY, ANY, "->", ANY)) {
		case 0:
			return matcher.createAssist(document, SUM_ASSIST);
		case 1:
			BlockToken block = (BlockToken) token.getParent();
			WordToken refModelName = (WordToken) block.getRefModelToken();
			return matcher.createAssist(document,
					getRefProperties(refModelName));
		case 2:
			return matcher.createAssist(document, "->");
		case 3:
			String refPropertyName = matcher.getWord(1);
			return matcher.createAssist(document, refPropertyName);
		case 4:
			return matcher.createAssist(document, ";");
		default:
			return null;
		}
	}

	protected List<ICompletionProposal> getRefAssist(IDocument document,
			int offset, PropertyToken token, WordToken refModelName) {
		List<DMDLToken> list = getList(token, offset);
		AssistMatcher matcher = new AssistMatcher(list, offset);
		switch (matcher.matchFirst(ANY, "->", ANY)) {
		case 0:
			return matcher.createAssist(document,
					getRefProperties(refModelName));
		case 1:
			return matcher.createAssist(document, "->");
		case 2:
			String refPropertyName = matcher.getWord(0);
			return matcher.createAssist(document, refPropertyName);
		case 3:
			return matcher.createAssist(document, ";");
		default:
			return null;
		}
	}
}
