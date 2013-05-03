package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.assist;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ArgumentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.BlockToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLBodyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
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

	protected List<ICompletionProposal> computeDataType(DMDLDocument document,
			int offset) {
		ModelList models = document.getModelList();
		ModelToken model = models.getModelByOffset(offset);
		if (model == null) {
			List<DMDLToken> list = models.getBody();
			if (!list.isEmpty()) {
				model = (ModelToken) list.get(list.size() - 1);
			} else {
				return getModelAssist(offset);
			}
		}

		DMDLToken token = model.getTokenByOffset(offset);
		for (;;) {
			if (token == null) {
				return getModelAssist(document, offset, model);
			}
			if (token instanceof DMDLBodyToken) {
				break;
			}
			token = token.getParent();
		}

		if (token instanceof ArgumentToken) {
			return getArgumentAssist(document, offset, (ArgumentToken) token);
		}
		if (token instanceof PropertyToken) {
			return getPropertyAssist(document, offset, (PropertyToken) token);
		}
		if (token instanceof BlockToken) {
			return getBlockAssist(document, offset, (BlockToken) token);
		}
		if (token instanceof ModelToken) {
			return getModelAssist(document, offset, (ModelToken) token);
		}

		return null;
	}

	protected ModelAssist model;

	protected List<ICompletionProposal> getModelAssist(int offset) {
		if (model == null) {
			model = new ModelAssist();
		}
		return model.getModelAssist(offset);
	}

	protected List<ICompletionProposal> getModelAssist(DMDLDocument document,
			int offset, ModelToken token) {
		if (model == null) {
			model = new ModelAssist();
		}
		return model.getModelAssist(document, offset, token);
	}

	protected BlockAssist block;

	protected List<ICompletionProposal> getBlockAssist(DMDLDocument document,
			int offset, BlockToken token) {
		if (block == null) {
			block = new BlockAssist();
		}
		return block.getBlockAssist(document, offset, token);
	}

	protected ArgumentAssist argument;

	protected List<ICompletionProposal> getArgumentAssist(IDocument document,
			int offset, ArgumentToken token) {
		if (argument == null) {
			argument = new ArgumentAssist();
		}
		return argument.getArgumentAssist(document, offset, token);
	}

	protected PropertyAssist property;

	protected List<ICompletionProposal> getPropertyAssist(IDocument document,
			int offset, PropertyToken token) {
		if (property == null) {
			property = new PropertyAssist();
		}
		return property.getPropertyAssist(document, offset, token);
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
