package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.assist;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class ModelAssist extends Assist {
	protected static final String[] MODEL_ASSIST = { "summarized", "joined", "projective" };

	protected static final String BLOCK = "{\n}";
	protected static final String[] BLOCK_ASSIST = { BLOCK };
	protected static final String[] BLOCK_END_ASSIST = { BLOCK + ";" };

	public List<ICompletionProposal> getModelAssist(int offset) {
		return createAssist(offset, MODEL_ASSIST);
	}

	public List<ICompletionProposal> getModelAssist(DMDLDocument document, int offset, ModelToken token) {
		List<DMDLToken> list = getList(token, offset);
		if (list.isEmpty()) {
			return createAssist(offset, MODEL_ASSIST);
		}
		AssistMatcher matcher = new AssistMatcher(list, offset);

		// matchLast系
		switch (matcher.matchLast(";")) {
		case 1:
			return createAssist(offset, MODEL_ASSIST);
		default:
			break;
		}
		switch (matcher.matchLast("->")) {
		case 1:
			return matcher.createAssist(document, BLOCK_ASSIST);
		default:
			break;
		}
		switch (matcher.matchLast(ANY, "%", ANY)) {
		case 2: {
			DMDLToken t = matcher.getToken(-2); // 「%」が-1、「%」の前が-2
			List<ICompletionProposal> r = matcher.createAssist(document, getProperties(t));
			if (r != null && !r.isEmpty()) {
				if (r.size() == 1) {
					String word = matcher.getCursorText();
					if (r.get(0).getDisplayString().equals(word)) {
						// fall through
					} else {
						return r;
					}
				} else {
					return r;
				}
			}
			// fall through
		}
		case 3: {
			String first = matcher.getWord(0);
			if ("summarized".equals(first)) {
				return matcher.createAssist(",", ";");
			} else if ("joined".equals(first)) {
				int n = matcher.lastIndexOf("+");
				if (n < 0) {
					return matcher.createAssist(",", ";", "+");
				} else {
					return matcher.createAssist(",", ";");
				}
			} else {
				return matcher.createAssist(",", "+", ";");
			}
		}
		default:
			break;
		}
		switch (matcher.matchLast("+", ANY, "%", ANY)) {
		case 1:
			String[] modelNames = getModelNames(token, matcher.getWord(1));
			return matcher.createAssist(document, modelNames);
		case 2: {
			String first = matcher.getWord(0);
			if ("joined".equals(first)) {
				return matcher.createAssist("-> " + BLOCK, "%");
			}
			return matcher.createAssist("%");
		}
		default:
			break;
		}
		switch (matcher.matchLast(",", ANY)) {
		case 1: {
			int n = matcher.lastIndexOf("%");
			if (n >= 1) {
				DMDLToken t = matcher.getToken(n - 1);
				return matcher.createAssist(document, getProperties(t));
			}
			break;
		}
		case 2: {
			String first = matcher.getWord(0);
			if ("summarized".equals(first)) {
				return matcher.createAssist(",", ";");
			} else if ("joined".equals(first)) {
				int n = matcher.lastIndexOf("+");
				if (n < 0) {
					return matcher.createAssist(",", ";", "+");
				} else {
					return matcher.createAssist(",", ";");
				}
			} else {
				return matcher.createAssist(",", ";", "+");
			}
		}
		default:
			break;
		}

		// matchFirst系
		switch (matcher.matchFirst("summarized", ANY, "=", ANY, "=>", ANY, "%", ANY, ",")) {
		case 1:
			return null;
		case 2:
			return matcher.createAssist("=");
		case 3:
			String[] modelNames = getModelNames(token, matcher.getWord(1));
			return matcher.createAssist(document, modelNames);
		case 4:
			if (matcher.existsCursorToken()) {
				String[] names = getModelNames(token, matcher.getWord(1));
				List<ICompletionProposal> r = matcher.createAssist(document, names);
				matcher.addAssist(r, "=> " + BLOCK);
				return r;
			}
			return matcher.createAssist("=> " + BLOCK);
		case 5:
			return matcher.createAssist(BLOCK_ASSIST);
		case 6:
			return matcher.createAssist("%");
		case 7: {
			DMDLToken t = matcher.getToken(5);
			return matcher.createAssist(document, getProperties(t));
		}
		case 8:
			return matcher.createAssist(",", ";");
		default:
			break;
		}

		switch (matcher.matchFirst("joined", ANY, "=", ANY, "->", ANY)) {
		case 1:
			return null;
		case 2:
			return matcher.createAssist("=");
		case 3:
			String[] modelNames = getModelNames(token, matcher.getWord(1));
			return matcher.createAssist(document, modelNames);
		case 4:
			if (matcher.existsCursorToken()) {
				String[] names = getModelNames(token, matcher.getWord(1));
				List<ICompletionProposal> r = matcher.createAssist(document, names);
				matcher.addAssist(r, "-> " + BLOCK, "%");
				return r;
			}
			return matcher.createAssist("-> " + BLOCK, "%");
		case 5:
			return matcher.createAssist(BLOCK_ASSIST);
		case 6:
			return matcher.createAssist("%");
		default:
			break;
		}
		switch (matcher.matchFirst("joined", ANY, "=", ANY, "%", ANY)) {
		// 0～4は前で記述済み
		case 5:
			return matcher.createAssist(",", "+");
		default:
			break;
		}

		switch (matcher.matchFirst("projective", ANY, "=", ANY)) {
		case 1:
			return null;
		case 2:
			return matcher.createAssist("=");
		case 3:
			String[] modelNames = getModelNames(token, matcher.getWord(1));
			return matcher.createAssist(document, modelNames);
		case 4:
			if (matcher.existsCursorToken()) {
				String[] names = getModelNames(token, matcher.getWord(1));
				List<ICompletionProposal> r = matcher.createAssist(document, names);
				matcher.addAssist(r, "-> " + BLOCK, "+");
				return r;
			}
			return matcher.createAssist("-> " + BLOCK, "+");
		default:
			break;
		}

		switch (matcher.matchFirst(ANY, "=", ANY)) {
		case 0:
			if (matcher.existsCursorToken()) {
				return matcher.createAssist(document, MODEL_ASSIST);
			}
			break;
		case 1:
			if (!matcher.existsCursorToken()) {
				return matcher.createAssist("=");
			} else {
				List<ICompletionProposal> r = matcher.createAssist(document, MODEL_ASSIST);
				matcher.addAssist(r, "=");
				return r;
			}
		case 2:
			return matcher.createAssist(BLOCK_END_ASSIST);
		case 3:
			return matcher.createAssist(";");
		default:
			break;
		}

		return null;
	}
}
