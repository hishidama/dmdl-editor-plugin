package jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.hyperlink;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLTextEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class DMDLHyperlinkHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DMDLTextEditor editor = (DMDLTextEditor) HandlerUtil.getActiveEditor(event);
		DMDLDocument document = editor.getDocument();
		ModelList models = document.getModelList();

		ITextSelection selection = (ITextSelection) editor
				.getSelectionProvider().getSelection();
		int offset = selection.getOffset();
		DMDLToken token = models.getTokenByOffset(offset);
		if (token != null && token instanceof WordToken) {
			WordToken word = (WordToken) token;
			switch (word.getWordType()) {
			case REF_MODEL_NAME:
			case REF_PROPERTY_NAME:
				DMDLHyperlink link = new DMDLHyperlink(editor, token);
				link.open();
				break;
			default:
				break;
			}
		}

		return null;
	}
}
