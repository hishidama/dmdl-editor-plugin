package jp.hishidama.eclipse_plugin.dmdl_editor.editors.assist;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ArgumentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.CommentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLBodyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class ArgumentAssist extends Assist {
	protected static final String[] VALUE_ASSIST = { "TRUE", "FALSE", "\"\"",
			"\"yyyy-MM-dd HH:mm:ss\"" };

	protected List<DMDLToken> getList(DMDLBodyToken token, int offset) {
		List<DMDLToken> list = new ArrayList<DMDLToken>();
		for (DMDLToken t : token.getBody()) {
			if (offset < t.getStart()) {
				break;
			}
			if (t instanceof CommentToken) {
				continue;
			}
			list.add(t);
		}
		return list;
	}

	public List<ICompletionProposal> getArgumentAssist(IDocument document,
			int offset, ArgumentToken token) {
		List<DMDLToken> list = getList(token, offset);
		AssistMatcher matcher = new AssistMatcher(list, offset);
		switch (matcher.matchFirst(ANY, "=", ANY)) {
		case 0:
			return null;
		case 1:
			return matcher.createAssist(document, "=");
		case 2:
			return matcher.createAssist(document, VALUE_ASSIST);
		case 3:
			return matcher.createAssist(document, ",", ")");
		default:
			return null;
		}
	}
}
