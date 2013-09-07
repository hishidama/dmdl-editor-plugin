package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.assist;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLDocument;
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
		List<Word> list = getList(token, offset);
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
			return matcher.createAssist(BLOCK_ASSIST);
		default:
			break;
		}
		switch (matcher.matchLast(ANY, "%", ANY)) {
		case 2: {
			Word t = matcher.getToken(-2); // 「%」の前
			return matcher.createAssist(getProperties(t));
		}
		case 3:
			Word t = matcher.getToken(-3); // 「%」の前
			List<ICompletionProposal> r = matcher.createAssist(document, getProperties(t));
			String first = matcher.getWord(0);
			if ("summarized".equals(first)) {
				return distinctAssist(r, matcher, ",", ";");
			} else if ("joined".equals(first)) {
				int n = matcher.lastIndexOf("+");
				if (n < 0) {
					return distinctAssist(r, matcher, ",", ";", "+");
				} else {
					return distinctAssist(r, matcher, ",", ";");
				}
			}
			return distinctAssist(r, matcher, ",", "+", ";");
		default:
			break;
		}
		switch (matcher.matchLast("+", ANY, "%", ANY)) {
		case 1:
			String[] modelNames = getModelNames(token, matcher.getWord(1));
			return matcher.createAssist(modelNames);
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
		switch (matcher.matchLast("+", ANY, "->", ANY, ANY)) {
		case 4:
			return matcher.createAssist("%", ";");
		}
		switch (matcher.matchLast(ANY, ",", ANY)) {
		case 2: {
			int n = matcher.lastIndexOf("%");
			if (n >= 1) {
				Word t = matcher.getToken(n - 1);
				return matcher.createAssist(getProperties(t));
			}
			break;
		}
		case 3: {
			List<ICompletionProposal> r = null;
			{
				int n = matcher.lastIndexOf("%");
				if (n >= 1) {
					Word t = matcher.getToken(n - 1);
					r = matcher.createAssist(document, getProperties(t));
				}
			}
			String first = matcher.getWord(0);
			if ("summarized".equals(first)) {
				return distinctAssist(r, matcher, ",", ";");
			} else if ("joined".equals(first)) {
				int p = matcher.lastIndexOf("+");
				if (p < 0) {
					return distinctAssist(r, matcher, ",", ";", "+");
				} else {
					return distinctAssist(r, matcher, ",", ";");
				}
			} else {
				return distinctAssist(r, matcher, ",", ";", "+");
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
			return matcher.createAssist(modelNames);
		case 4:
			String[] names = getModelNames(token, matcher.getWord(1));
			List<ICompletionProposal> r = matcher.createAssist(document, names);
			matcher.addAssist(r, "=> " + BLOCK);
			return r;
		case 5:
			return matcher.createAssist(BLOCK_ASSIST);
		case 6:
			return matcher.createAssist("%", ";");
		case 7: {
			Word t = matcher.getToken(5);
			return matcher.createAssist(getProperties(t));
		}
		case 8:
			return matcher.createAssist(",", ";");
		default:
			break;
		}

		switch (matcher.matchFirst("joined", ANY, "=", ANY, "->", ANY, "%", ANY)) {
		case 1:
			return null;
		case 2:
			return matcher.createAssist("=");
		case 3:
			String[] modelNames = getModelNames(token, matcher.getWord(1));
			return matcher.createAssist(modelNames);
		case 4:
			String[] names = getModelNames(token, matcher.getWord(1));
			List<ICompletionProposal> r = matcher.createAssist(document, names);
			matcher.addAssist(r, "-> " + BLOCK, "%");
			return r;
		case 5:
			return matcher.createAssist(BLOCK_ASSIST);
		case 6:
			return matcher.createAssist("%");
		case 7: {
			Word t = matcher.getToken(5);
			return matcher.createAssist(getProperties(t));
		}
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
			return matcher.createAssist(modelNames);
		case 4:
			String[] names = getModelNames(token, matcher.getWord(1));
			List<ICompletionProposal> r = matcher.createAssist(document, names);
			matcher.addAssist(r, "-> " + BLOCK, "+");
			return r;
		default:
			break;
		}

		switch (matcher.matchFirst(ANY, "=", ANY)) {
		case 0:
			return matcher.createAssist(document, MODEL_ASSIST);
		case 1:
			List<ICompletionProposal> r = matcher.createAssist(document, MODEL_ASSIST);
			matcher.addAssist(r, "=");
			return r;
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
