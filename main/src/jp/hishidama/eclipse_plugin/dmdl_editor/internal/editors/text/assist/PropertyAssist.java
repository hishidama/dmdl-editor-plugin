package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.assist;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.BlockToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLTextToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
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
		List<DMDLToken> list = getList(token, offset);
		AssistMatcher matcher = new AssistMatcher(list, offset);
		switch (matcher.matchFirst(ANY, ":", ANY)) {
		case 0:
			return null;
		case 1:
			return matcher.createAssist(":");
		case 2:
			return matcher.createAssist(document, TYPE_ASSIST);
		case 3:
			if (matcher.existsCursorToken()) {
				List<ICompletionProposal> r = matcher.createAssist(document, TYPE_ASSIST);
				if (r == null || r.isEmpty()) {
					return matcher.createAssist(";");
				}
				if (r.size() == 1) {
					DMDLToken t = matcher.getToken(2);
					if (t instanceof DMDLTextToken) {
						String word = ((DMDLTextToken) t).getText(t.getStart(), offset);
						if (r.get(0).getDisplayString().equals(word)) {
							return matcher.createAssist(";");
						}
					}
				}
				return r;
			}
			return matcher.createAssist(";");
		default:
			return null;
		}
	}

	protected List<ICompletionProposal> getSummarizedAssist(IDocument document, int offset, PropertyToken token) {
		List<DMDLToken> list = getList(token, offset);
		AssistMatcher matcher = new AssistMatcher(list, offset);
		switch (matcher.matchFirst(ANY, ANY, "->", ANY)) {
		case 0:
			return matcher.createAssist(document, SUM_ASSIST);
		case 1:
			if (matcher.existsCursorToken()) {
				return matcher.createAssist(document, SUM_ASSIST);
			} else {
				BlockToken block = (BlockToken) token.getParent();
				WordToken refModelName = (WordToken) block.getRefModelToken();
				return matcher.createAssist(document, getRefProperties(refModelName));
			}
		case 2:
			return matcher.createAssist("->");
		case 3: {
			String refPropertyName = matcher.getWord(1);
			List<ICompletionProposal> r = matcher.createAssist(document, refPropertyName);
			if (r == null || r.isEmpty()) {
				return matcher.createAssist(";");
			}
			return r;
		}
		case 4:
			return matcher.createAssist(";");
		default:
			return null;
		}
	}

	protected List<ICompletionProposal> getRefAssist(IDocument document, int offset, PropertyToken token,
			WordToken refModelName) {
		List<DMDLToken> list = getList(token, offset);
		AssistMatcher matcher = new AssistMatcher(list, offset);
		switch (matcher.matchFirst(ANY, "->", ANY)) {
		case 0:
			return matcher.createAssist(document, getRefProperties(refModelName));
		case 1:
			if (matcher.existsCursorToken()) {
				List<ICompletionProposal> r = matcher.createAssist(document, getRefProperties(refModelName));
				if (r == null || r.isEmpty()) {
					return matcher.createAssist("->");
				}
				if (r.size() == 1) {
					DMDLToken t = matcher.getToken(0);
					if (t instanceof DMDLTextToken) {
						String word = ((DMDLTextToken) t).getText(t.getStart(), offset);
						if (r.get(0).getDisplayString().equals(word)) {
							return matcher.createAssist("->");
						}
					}
				}
				return r;
			}
			return matcher.createAssist("->");
		case 2: {
			String refPropertyName = matcher.getWord(0);
			List<ICompletionProposal> r = matcher.createAssist(document, refPropertyName);
			if (r == null || r.isEmpty()) {
				return matcher.createAssist(";");
			}
			return r;
		}
		case 3:
			return matcher.createAssist(";");
		default:
			return null;
		}
	}
}
