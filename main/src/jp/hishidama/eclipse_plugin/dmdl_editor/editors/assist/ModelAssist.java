package jp.hishidama.eclipse_plugin.dmdl_editor.editors.assist;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.CommentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLBodyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DescriptionToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class ModelAssist extends Assist {
	protected static final String[] MODEL_ASSIST = { "summarized ", "joined ",
			"projective " };

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
		String w0 = getWordString(list.get(0));
		if ("summarized".equals(w0) || "joined".equals(w0)
				|| "projective".equals(w0)) {
			list.remove(0);
		}

		if (list.size() <= 1) {
			return createAssist(offset, "=");
		}

		String last = getWordString(list.get(list.size() - 1));
		if ("=".equals(last)) {
			return createAssist(offset, BLOCK_ASSIST);
		}
		if ("->".equals(last) || "=>".equals(last)) {
			return createAssist(offset, BLOCK_ASSIST);
		}

		return null;
	}

	protected List<DMDLToken> getList(DMDLBodyToken token, int offset) {
		List<DMDLToken> list = new ArrayList<DMDLToken>();
		for (DMDLToken t : token.getBody()) {
			if (offset < t.getStart()) {
				break;
			}
			if (t instanceof CommentToken || t instanceof DescriptionToken) {
				continue;
			}
			list.add(t);
		}
		return list;
	}

	protected String getWordString(DMDLToken token) {
		if (token instanceof WordToken) {
			return ((WordToken) token).getBody();
		}
		return null;
	}
}
