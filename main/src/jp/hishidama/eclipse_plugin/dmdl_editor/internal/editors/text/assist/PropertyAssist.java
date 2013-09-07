package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.assist;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.BlockToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class PropertyAssist extends Assist {
	protected static final String[] TYPE_ASSIST = WordToken.PROPERTY_TYPE;

	public List<ICompletionProposal> getPropertyAssist(IDocument document, int offset, PropertyToken token) {
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

	protected List<ICompletionProposal> getDefaultAssist(IDocument document, int offset, PropertyToken token) {
		List<Word> list = getList(token, offset);
		AssistMatcher matcher = new AssistMatcher(list, offset);
		switch (matcher.matchFirst(ANY, ":", ANY)) {
		case 0:
			return null;
		case 1:
			return matcher.createAssist(":");
		case 2:
			return matcher.createAssist(TYPE_ASSIST);
		case 3:
			List<ICompletionProposal> r = matcher.createAssist(document, TYPE_ASSIST);
			return distinctAssist(r, matcher, ";");
		default:
			return null;
		}
	}

	protected List<ICompletionProposal> getSummarizedAssist(IDocument document, int offset, PropertyToken token) {
		List<Word> list = getList(token, offset);
		AssistMatcher matcher = new AssistMatcher(list, offset);
		switch (matcher.matchFirst(ANY, ANY, "->", ANY)) {
		case 0:
			return matcher.createAssist(document, SUM_ASSIST);
		case 1: {
			List<ICompletionProposal> r = matcher.createAssist(document, SUM_ASSIST);
			if (r == null || r.isEmpty()) {
				BlockToken block = (BlockToken) token.getParent();
				WordToken refModelName = (WordToken) block.getRefModelToken();
				return matcher.createAssist(document, getRefProperties(refModelName));
			}
			return r;
		}
		case 2:
			return matcher.createAssist("->");
		case 3: {
			String refPropertyName = matcher.getWord(1);
			return matcher.createAssist(refPropertyName);
		}
		case 4:
			BlockToken block = (BlockToken) token.getParent();
			WordToken refModelName = (WordToken) block.getRefModelToken();
			List<ICompletionProposal> r = matcher.createAssist(document, getRefProperties(refModelName));
			return distinctAssist(r, matcher, ";");
		default:
			return null;
		}
	}

	protected List<ICompletionProposal> getRefAssist(IDocument document, int offset, PropertyToken token,
			WordToken refModelName) {
		List<Word> list = getList(token, offset);
		AssistMatcher matcher = new AssistMatcher(list, offset);
		switch (matcher.matchFirst(ANY, "->", ANY)) {
		case 0:
			return matcher.createAssist(document, getRefProperties(refModelName));
		case 1:
			List<ICompletionProposal> r = matcher.createAssist(document, getRefProperties(refModelName));
			return distinctAssist(r, matcher, "->");
		case 2: {
			String refPropertyName = matcher.getWord(0);
			return matcher.createAssist(refPropertyName);
		}
		case 3:
			return matcher.createAssist(";");
		default:
			return null;
		}
	}
}
