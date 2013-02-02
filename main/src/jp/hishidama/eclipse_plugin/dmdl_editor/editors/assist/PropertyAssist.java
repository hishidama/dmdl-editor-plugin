package jp.hishidama.eclipse_plugin.dmdl_editor.editors.assist;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.BlockToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class PropertyAssist extends Assist {
	protected static final String[] TYPE_ASSIST = { "INT", "LONG", "FLOAT",
			"DOUBLE", "TEXT", "DECIMAL", "DATE", "DATETIME", "BOOLEAN", "BYTE",
			"SHORT" };

	public List<ICompletionProposal> getPropertyAssist(IDocument document,
			int offset, PropertyToken token) {
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

	protected List<ICompletionProposal> getDefaultAssist(IDocument document,
			int offset, PropertyToken token) {

		List<WordToken> list = getWordList(token, offset);
		if (list.size() >= 1) {
			WordToken last = list.get(list.size() - 1);
			if (":".equals(last.getBody())) {
				return createAssist(offset, TYPE_ASSIST);
			}
			if (list.size() >= 2) {
				WordToken prev = list.get(list.size() - 2);
				if (":".equals(prev.getBody())) {
					return createAssist(document, offset, last.getStart(),
							TYPE_ASSIST);
				}
			}
		}
		if (list.size() <= 1) {
			BlockToken block = (BlockToken) token.getParent();
			WordToken refModelName = (WordToken) block.getRefModelToken();
			if (refModelName != null) {
				ModelToken refModel = block.findModel(refModelName.getBody());
				if (refModel != null) {
					List<String> alist = new ArrayList<String>();
					for (PropertyToken p : refModel.getPropertyList()) {
						if (p.getName() != null) {
							alist.add(p.getName());
						}
					}
					if (list.size() == 1) {
						WordToken last = list.get(list.size() - 1);
						return createAssist(document, offset, last.getStart(),
								alist.toArray(new String[alist.size()]));
					} else {
						return createAssist(offset,
								alist.toArray(new String[alist.size()]));
					}
				}
			}
		}
		return null;
	}

	protected List<ICompletionProposal> getSummarizedAssist(IDocument document,
			int offset, PropertyToken token) {
		List<WordToken> list = getWordList(token, offset);
		if (list.isEmpty()) {
			return createAssist(offset, SUM_ASSIST);
		}
		if (list.size() == 1) {
			return createAssist(document, offset, list.get(0).getStart(),
					SUM_ASSIST);
		}

		WordToken lastToken = list.get(list.size() - 1);
		String last = getWordString(lastToken);
		if ("->".equals(last)) {
			return createAssist(offset, list.get(list.size() - 2).getBody());
		}

		if (list.size() >= 3) {
			String prev = getWordString(list.get(list.size() - 2));
			if ("->".equals(prev)) {
				return createAssist(document, offset, lastToken.getStart(),
						list.get(list.size() - 3).getBody());
			}
		}

		return null;
	}

	protected List<ICompletionProposal> getRefAssist(IDocument document,
			int offset, PropertyToken token, WordToken refModelName) {
		List<WordToken> list = getWordList(token, offset);
		if (list.size() >= 1) {
			WordToken last = list.get(list.size() - 1);
			if ("->".equals(last.getBody())) {
				return createAssist(offset, token.getName());
			}
			if (list.size() >= 2) {
				WordToken prev = list.get(list.size() - 2);
				if ("->".equals(prev.getBody())) {
					return createAssist(document, offset, last.getStart(),
							token.getName());
				}
			}
		}
		if (list.size() <= 1) {
			BlockToken block = (BlockToken) token.getParent();
			ModelToken refModel = block.findModel(refModelName.getBody());
			if (refModel != null) {
				List<String> alist = new ArrayList<String>();
				for (PropertyToken p : refModel.getPropertyList()) {
					if (p.getName() != null) {
						alist.add(p.getName());
					}
				}
				if (list.size() == 1) {
					WordToken last = list.get(list.size() - 1);
					return createAssist(document, offset, last.getStart(),
							alist.toArray(new String[alist.size()]));
				} else {
					return createAssist(offset,
							alist.toArray(new String[alist.size()]));
				}
			}
		}
		return null;
	}
}
