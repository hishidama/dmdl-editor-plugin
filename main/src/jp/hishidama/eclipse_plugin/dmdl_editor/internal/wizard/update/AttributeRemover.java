package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.update;

import java.util.HashSet;
import java.util.Set;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.AnnotationToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ArgumentsToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.CommentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

public class AttributeRemover extends AttributeUpdater {

	private Set<String> modelAttr;
	private Set<String> propAttr;

	@Override
	public void setAttribute(String modelAttr, String propAttr) {
		this.modelAttr = toSet(modelAttr);
		this.propAttr = toSet(propAttr);
	}

	private static Set<String> toSet(String attr) {
		Set<String> set = new HashSet<String>();
		String[] ss = attr.split("[ \t\r\n]+");
		for (String s : ss) {
			set.add(s);
		}
		return set;
	}

	private int deleteStart;
	private int deleteEnd;

	@Override
	protected void execute(IFile file, IDocument doc, ModelToken model) {

		boolean delete = false;
		deleteStart = -1;
		deleteEnd = -1;
		for (DMDLToken token : model.getBody()) {
			delete = removeToken(file, doc, modelAttr, token, delete);
			if (!delete) {
				addRemoveRegion(file, doc);
			}
		}
		addRemoveRegion(file, doc);

		for (PropertyToken prop : model.getOwnPropertyList()) {
			delete = false;
			for (DMDLToken token : prop.getBody()) {
				delete = removeToken(file, doc, propAttr, token, delete);
				if (!delete) {
					addRemoveRegion(file, doc);
				}
			}
			addRemoveRegion(file, doc);
		}
	}

	private boolean removeToken(IFile file, IDocument doc, Set<String> attrName, DMDLToken token, boolean delete) {
		if (token instanceof CommentToken) {
			return delete;
		}
		if (token instanceof AnnotationToken) {
			AnnotationToken atoken = (AnnotationToken) token;
			if (attrName.contains(atoken.getText())) {
				deleteStart = token.getStart();
				deleteEnd = token.getEnd();
				return true;
			}
		} else if ((token instanceof ArgumentsToken) && delete) {
			deleteEnd = token.getEnd();
		}
		return false;
	}

	private static int getLineEnd(IDocument doc, int start, int end) {
		boolean top = false;
		if (start <= 0) {
			top = true;
		} else {
			try {
				if (doc.getChar(start - 1) == '\n') {
					top = true;
				}
			} catch (BadLocationException e) {
				// do nothing
			}
		}

		try {
			int dlen = doc.getLength();
			for (int i = end; i < dlen; i++) {
				char c = doc.getChar(i);
				switch (c) {
				case ' ':
				case '\t':
					continue;
				case '\r':
					if (i + 1 < dlen) {
						char d = doc.getChar(i + 1);
						if (d == '\n') {
							if (top) {
								return i + 2;
							} else {
								return i;
							}
						}
					}
					continue;
				case '\n':
					if (top) {
						return i + 1;
					} else {
						return i;
					}
				default:
					return i;
				}
			}
			return dlen;
		} catch (BadLocationException e) {
			return end;
		}
	}

	private void addRemoveRegion(IFile file, IDocument doc) {
		if (deleteStart < 0 || deleteEnd < 0) {
			return;
		}

		int start = getLineTop(doc, deleteStart);
		int end = getLineEnd(doc, start, deleteEnd);

		addRemoveRegion(file, start, end);

		deleteStart = -1;
		deleteEnd = -1;
	}
}
