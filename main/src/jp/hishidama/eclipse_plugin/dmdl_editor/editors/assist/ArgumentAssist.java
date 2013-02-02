package jp.hishidama.eclipse_plugin.dmdl_editor.editors.assist;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ArgumentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLTextToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class ArgumentAssist extends Assist {
	protected static final String[] VALUE_ASSIST = { "TRUE", "FALSE", "\"\"",
			"\"yyyy-MM-dd HH:mm:ss\"" };

	public List<ICompletionProposal> getArgumentAssist(IDocument document,
			int offset, ArgumentToken token) {
		List<DMDLTextToken> list = new ArrayList<DMDLTextToken>();
		for (DMDLToken t : token.getBody()) {
			if (offset < t.getStart()) {
				break;
			}
			if (t instanceof DMDLTextToken) {
				list.add((DMDLTextToken) t);
			}
		}
		if (list.size() >= 1) {
			DMDLTextToken last = list.get(list.size() - 1);
			if ("=".equals(last.getBody())) {
				return createAssist(offset, VALUE_ASSIST);
			}
			if (list.size() >= 2) {
				DMDLTextToken prev = list.get(list.size() - 2);
				if ("=".equals(prev.getBody())) {
					return createAssist(document, offset, last.getStart(),
							VALUE_ASSIST);
				}
			}
		}
		return null;
	}
}
