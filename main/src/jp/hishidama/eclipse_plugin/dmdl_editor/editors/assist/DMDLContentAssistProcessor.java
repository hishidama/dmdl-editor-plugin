package jp.hishidama.eclipse_plugin.dmdl_editor.editors.assist;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ArgumentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLBodyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelList;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class DMDLContentAssistProcessor implements IContentAssistProcessor {

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {

		DMDLDocument document = (DMDLDocument) viewer.getDocument();
		List<ICompletionProposal> list = computeDataType(document, offset);
		if (list != null && !list.isEmpty()) {
			return list.toArray(new ICompletionProposal[list.size()]);
		}
		return null;
	}

	protected static final String[] TYPE_ASSIST = { "INT", "LONG", "FLOAT",
			"DOUBLE", "TEXT", "DECIMAL", "DATE", "DATETIME", "BOOLEAN", "BYTE",
			"SHORT" };
	protected static final String[] VALUE_ASSIST = { "TRUE", "FALSE", "\"\"",
			"\"yyyy-MM-dd HH:mm:ss\"" };
	protected static final String[] BLOCK_ASSIST = { String.format("{%n};") };

	protected List<ICompletionProposal> computeDataType(DMDLDocument document,
			int offset) {
		// 単語の先頭を探す
		int n = offset - 1;
		for (; n >= 0; n--) {
			char c;
			try {
				c = document.getChar(n);
			} catch (BadLocationException e) {
				break;
			}
			if ('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z') {
				continue;
			} else {
				break;
			}
		}

		// 「:」を探す
		for (int i = n; i >= 0; i--) {
			char c;
			try {
				c = document.getChar(i);
			} catch (BadLocationException e) {
				break;
			}
			switch (c) {
			case ' ':
			case '\t':
			case '\r':
			case '\n':
				continue;
			case ':':
				return getAssist(document, offset, n, TYPE_ASSIST);
			case '=':
				ModelList models = document.getModelList();
				DMDLToken token = models.getTokenByOffset(n);
				while (token != null && !(token instanceof DMDLBodyToken)) {
					token = token.getParent();
				}
				if (token instanceof ArgumentToken) {
					return getAssist(document, offset, n, VALUE_ASSIST);
				}
				return getAssist(document, offset, n, BLOCK_ASSIST);
			default:
				break;
			}
		}
		return null;
	}

	protected List<ICompletionProposal> getAssist(IDocument document,
			int offset, int n, String[] candidate) {
		int start = n + 1;
		int len = offset - start;
		String text;
		try {
			text = document.get(start, len).toUpperCase();
		} catch (BadLocationException e) {
			return null;
		}
		List<ICompletionProposal> list = new ArrayList<ICompletionProposal>();
		for (String s : candidate) {
			if (s.startsWith(text)) {
				list.add(new CompletionProposal(s, start, len, s.length()));
			}
		}
		return list;
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}
}
