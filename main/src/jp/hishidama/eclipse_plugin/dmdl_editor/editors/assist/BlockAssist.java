package jp.hishidama.eclipse_plugin.dmdl_editor.editors.assist;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.BlockToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class BlockAssist extends Assist {

	// ここに来るのは、ブロック内でプロパティー外の場合のみ。
	// 1文字でも入れられているとPropertyTokenになるので
	// ここには来ない。

	public List<ICompletionProposal> getBlockAssist(DMDLDocument document,
			int offset, BlockToken token) {
		WordToken refModelName = (WordToken) token.getRefModelToken();
		if (refModelName != null) {
			return getRefAssist(document, offset, token, refModelName);
		}
		return null;
	}

	protected List<ICompletionProposal> getRefAssist(IDocument document,
			int offset, BlockToken token, WordToken refModelName) {
		List<WordToken> list = getWordList(token, offset);
		if (list.isEmpty()) {
			return null;
		}

		ModelToken refModel = token.findModel(refModelName.getBody());
		if (refModel != null) {
			List<String> alist = new ArrayList<String>();
			for (PropertyToken p : refModel.getPropertyList()) {
				if (p.getName() != null) {
					alist.add(p.getName());
				}
			}
			return createAssist(offset, alist.toArray(new String[alist.size()]));
		}
		return null;
	}
}
