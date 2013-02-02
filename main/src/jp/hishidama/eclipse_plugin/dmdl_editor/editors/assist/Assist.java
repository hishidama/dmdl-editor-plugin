package jp.hishidama.eclipse_plugin.dmdl_editor.editors.assist;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLBodyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class Assist {
	protected static final String[] SUM_ASSIST = { "any", "sum", "count",
			"min", "max" };

	protected String getWordString(DMDLToken token) {
		if (token instanceof WordToken) {
			return ((WordToken) token).getBody();
		}
		return null;
	}

	protected List<WordToken> getWordList(DMDLBodyToken token, int offset) {
		List<WordToken> list = new ArrayList<WordToken>();
		for (DMDLToken t : token.getBody()) {
			if (offset < t.getStart()) {
				break;
			}
			if (t instanceof WordToken) {
				list.add((WordToken) t);
			}
		}
		return list;
	}

	protected List<ICompletionProposal> createAssist(IDocument document,
			int offset, int start, String... candidate) {
		offset++;
		int len = offset - start;
		String text;
		try {
			text = document.get(start, len);
		} catch (BadLocationException e) {
			return null;
		}
		List<ICompletionProposal> list = new ArrayList<ICompletionProposal>();
		for (String s : candidate) {
			if (s.length() >= text.length()
					&& s.substring(0, text.length()).equalsIgnoreCase(text)) {
				list.add(new CompletionProposal(s, start, len, s.length()));
			}
		}
		return list;
	}

	protected List<ICompletionProposal> createAssist(int offset,
			String... candidate) {
		offset++;
		List<ICompletionProposal> list = new ArrayList<ICompletionProposal>();
		for (String s : candidate) {
			list.add(new CompletionProposal(s, offset, 0, s.length()));
		}
		return list;
	}
}
