package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.assist;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.AnnotationToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ArgumentsToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.BlockToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.CommentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLBodyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLTextToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DescriptionToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class Assist {
	protected static final String ANY = AssistMatcher.ANY;

	protected static final String[] SUM_ASSIST = { "any", "count", "sum", "min", "max" };

	protected static class Word {

		private DMDLToken token;
		private int offset;

		Word(DMDLToken token, int offset) {
			this.token = token;
			this.offset = offset;
		}

		public String getText() {
			if (token instanceof DMDLTextToken) {
				DMDLTextToken w = (DMDLTextToken) token;
				return w.getText(w.getStart(), offset);
			}
			return null;
		}

		public int getStart() {
			return token.getStart();
		}

		public int getEnd() {
			return token.getEnd();
		}

		public DMDLToken getToken() {
			return token;
		}

		@Override
		public String toString() {
			if (token instanceof DMDLTextToken) {
				return getText();
			}
			return token.toString();
		}
	}

	protected List<Word> getList(DMDLBodyToken token, int offset) {
		List<Word> list = new ArrayList<Word>();
		for (DMDLToken t : token.getBody()) {
			if (offset <= t.getStart()) {
				break;
			}
			if (t instanceof CommentToken || t instanceof DescriptionToken || t instanceof AnnotationToken
					|| t instanceof ArgumentsToken) {
				continue;
			}
			list.add(new Word(t, offset));
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

	protected String[] getProperties(Word word) {
		if (word == null) {
			return new String[] {};
		}
		DMDLToken token = word.getToken();
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
		ModelToken refModel = findModel(refModelName, refModelName.getText());
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

	protected ModelToken findModel(DMDLToken token, String modelName) {
		return token.findModel(modelName);
	}

	protected List<ICompletionProposal> createAssist(int offset, String... candidate) {
		List<ICompletionProposal> list = new ArrayList<ICompletionProposal>();
		for (String s : candidate) {
			list.add(new CompletionProposal(s, offset, 0, s.length()));
		}
		return list;
	}

	protected List<ICompletionProposal> distinctAssist(List<ICompletionProposal> list, AssistMatcher matcher,
			String... candidate) {
		if (list == null || list.isEmpty()) {
			return matcher.createAssist(candidate);
		}
		if (list.size() == 1) {
			String word = list.get(0).getDisplayString();
			String last = matcher.getLastWord();
			if (word.equals(last)) {
				return matcher.createAssist(candidate);
			}
		}
		return list;
	}

}
