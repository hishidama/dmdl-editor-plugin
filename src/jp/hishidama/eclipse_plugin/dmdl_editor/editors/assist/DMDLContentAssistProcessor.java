package jp.hishidama.eclipse_plugin.dmdl_editor.editors.assist;

import java.util.ArrayList;
import java.util.List;

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

		IDocument document = viewer.getDocument();
		List<ICompletionProposal> list = computeDataType(document, offset);
		if (list != null && !list.isEmpty()) {
			return list.toArray(new ICompletionProposal[list.size()]);
		}
		return null;
	}

	protected static final String[] TYPE_ASSIST = { "INT", "LONG", "FLOAT",
			"DOUBLE", "TEXT", "DECIMAL", "DATE", "DATETIME", "BOOLEAN", "BYTE",
			"SHORT" };

	protected List<ICompletionProposal> computeDataType(IDocument document,
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
			if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
				continue;
			} else if (c == ':') {
				int start = n + 1;
				int len = offset - start;
				String text;
				try {
					text = document.get(start, len).toUpperCase();
				} catch (BadLocationException e) {
					return null;
				}
				List<ICompletionProposal> list = new ArrayList<ICompletionProposal>();
				for (String s : TYPE_ASSIST) {
					if (s.startsWith(text)) {
						list.add(new CompletionProposal(s, start, len, s
								.length()));
					}
				}
				return list;
			} else {
				break;
			}
		}
		return null;
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
