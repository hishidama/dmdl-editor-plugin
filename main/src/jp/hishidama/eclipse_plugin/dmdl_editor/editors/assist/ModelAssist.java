package jp.hishidama.eclipse_plugin.dmdl_editor.editors.assist;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class ModelAssist extends Assist {
	protected static final String[] MODEL_ASSIST = { "summarized", "joined",
			"projective" };

	protected static final String[] BLOCK_ASSIST = { String.format("{%n};") };

	public List<ICompletionProposal> getModelAssist(int offset) {
		return createAssist(offset, MODEL_ASSIST);
	}

	public List<ICompletionProposal> getModelAssist(DMDLDocument document,
			int offset, ModelToken token) {
		List<DMDLToken> list = getList(token, offset);
		if (list.isEmpty()) {
			return createAssist(offset, MODEL_ASSIST);
		}
		AssistMatcher matcher = new AssistMatcher(list, offset);
		switch (matcher.matchLast("->")) {
		case 1:
			return matcher.createAssist(document, BLOCK_ASSIST);
		default:
			break;
		}

		switch (matcher.matchFirst("summarized", ANY, "=", ANY, "=>")) {
		case 1:
			return null;
		case 2:
			return matcher.createAssist(document, "=");
		case 3:
			String[] modelNames = getModelNames(token, matcher.getWord(1));
			return matcher.createAssist(document, modelNames);
		case 4:
			return matcher.createAssist(document, "=>");
		case 5:
			return matcher.createAssist(document, BLOCK_ASSIST);
		default:
			break;
		}

		switch (matcher.matchFirst("joined", ANY, "=", ANY)) {
		case 1:
			return null;
		case 2:
			return matcher.createAssist(document, "=");
		case 3:
			String[] modelNames = getModelNames(token, matcher.getWord(1));
			return matcher.createAssist(document, modelNames);
		case 4:
			return matcher.createAssist(document, "->", "%");
		default:
			break;
		}

		switch (matcher.matchFirst("projective", ANY, "=", ANY)) {
		case 1:
			return null;
		case 2:
			return matcher.createAssist(document, "=");
		case 3:
			String[] modelNames = getModelNames(token, matcher.getWord(1));
			return matcher.createAssist(document, modelNames);
		case 4:
			return matcher.createAssist(document, "->");
		default:
			break;
		}

		switch (matcher.matchFirst(ANY, "=")) {
		case 1:
			return matcher.createAssist(document, "=");
		case 2:
			return matcher.createAssist(document, BLOCK_ASSIST);
		default:
			break;
		}

		return null;
	}
}
