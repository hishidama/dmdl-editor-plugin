package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.assist;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.AnnotationToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ArgumentsToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.BlockToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.CommentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLBodyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DescriptionToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class Assist {
	protected static final String ANY = AssistMatcher.ANY;

	protected static final String[] SUM_ASSIST = { "any", "count", "sum",
			"min", "max" };

	protected List<DMDLToken> getList(DMDLBodyToken token, int offset) {
		List<DMDLToken> list = new ArrayList<DMDLToken>();
		for (DMDLToken t : token.getBody()) {
			if (offset < t.getStart()) {
				break;
			}
			if (t instanceof CommentToken || t instanceof DescriptionToken
					|| t instanceof AnnotationToken
					|| t instanceof ArgumentsToken) {
				continue;
			}
			list.add(t);
		}
		return list;
	}

	protected String[] getModelNames(DMDLToken token, String modelName) {
		List<ModelToken> modelList = token.getTop().getNamedModelList();
		List<String> list = new ArrayList<String>(modelList.size());
		for (ModelToken model : modelList) {
			String name = model.getModelName();
			if (!name.equals(modelName)) {
				list.add(name);
			}
		}
		return list.toArray(new String[list.size()]);
	}

	protected String[] getProperties(DMDLToken token) {
		if (token == null) {
			return new String[] {};
		}
		if (token instanceof WordToken) {
			WordToken refModelName = (WordToken) token;
			return getRefProperties(refModelName);
		} else if (token instanceof BlockToken) {
			BlockToken block = (BlockToken) token;
			List<String> alist = new ArrayList<String>();
			for (PropertyToken p : block.getPropertyList()) {
				if (p.getName() != null) {
					alist.add(p.getName());
				}
			}
			return alist.toArray(new String[alist.size()]);
		}
		return new String[] {};
	}

	protected String[] getRefProperties(WordToken refModelName) {
		if (refModelName == null) {
			return new String[] {};
		}
		ModelToken refModel = refModelName.findModel(refModelName.getBody());
		if (refModel != null) {
			List<String> alist = new ArrayList<String>();
			for (PropertyToken p : refModel.getPropertyList()) {
				if (p.getName() != null) {
					alist.add(p.getName());
				}
			}
			return alist.toArray(new String[alist.size()]);
		}
		return new String[] {};
	}

	protected List<ICompletionProposal> createAssist(int offset,
			String... candidate) {
		List<ICompletionProposal> list = new ArrayList<ICompletionProposal>();
		for (String s : candidate) {
			list.add(new CompletionProposal(s, offset, 0, s.length()));
		}
		return list;
	}
}
