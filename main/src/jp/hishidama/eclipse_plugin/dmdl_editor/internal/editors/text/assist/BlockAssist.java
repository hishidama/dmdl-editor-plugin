package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.assist;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.BlockToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.CommentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLBodyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DescriptionToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class BlockAssist extends Assist {

	// ここに来るのは、ブロック内でプロパティー外の場合のみ。
	// 1文字でも入れられているとPropertyTokenになるので
	// ここには来ない。

	public List<ICompletionProposal> getBlockAssist(DMDLDocument document, int offset, BlockToken token) {
		WordToken refModelName = (WordToken) token.getRefModelToken();
		if (refModelName != null) {
			return getRefAssist(document, offset, token, refModelName);
		}
		return null;
	}

	protected List<ICompletionProposal> getRefAssist(IDocument document, int offset, BlockToken token,
			WordToken refModelName) {
		if ("summarized".equals(token.getModelType())) {
			List<DMDLToken> list = getList(token, offset);
			AssistMatcher matcher = new AssistMatcher(list, offset);
			switch (matcher.matchFirst("{")) {
			case 0:
				return null;
			case 1:
				return matcher.createAssist(document, SUM_ASSIST);
			default:
				break;
			}
		}

		ModelToken refModel = findModel(token, refModelName.getText());
		if (refModel != null) {
			List<DMDLToken> list = getList(token, offset);
			AssistMatcher matcher = new AssistMatcher(list, offset);
			switch (matcher.matchFirst("{")) {
			case 0:
				return null;
			case 1:
				String[] propNames = getRefProperties(refModelName);
				return matcher.createAssist(document, propNames);
			default:
				break;
			}
		}

		return null;
	}

	@Override
	protected List<DMDLToken> getList(DMDLBodyToken token, int offset) {
		List<DMDLToken> list = new ArrayList<DMDLToken>();
		for (DMDLToken t : token.getBody()) {
			if (offset < t.getStart()) {
				break;
			}
			if (t instanceof PropertyToken || t instanceof CommentToken || t instanceof DescriptionToken) {
				continue;
			}
			list.add(t);
		}
		return list;
	}
}
