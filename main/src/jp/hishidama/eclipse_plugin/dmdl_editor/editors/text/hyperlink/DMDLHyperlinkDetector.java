package jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.hyperlink;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLTextEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class DMDLHyperlinkDetector extends AbstractHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		DMDLDocument document = (DMDLDocument) textViewer.getDocument();
		ModelList models = document.getModelList();
		// if (models == null) {
		// models = new DMDLSimpleParser().parse(document);
		// }

		DMDLToken token = models.getTokenByOffset(region.getOffset());
		if (token != null && token instanceof WordToken) {
			WordToken word = (WordToken) token;
			switch (word.getWordType()) {
			case REF_MODEL_NAME:
			case REF_PROPERTY_NAME:
				DMDLTextEditor editor = (DMDLTextEditor) getAdapter(DMDLTextEditor.class);
				return new IHyperlink[] { new DMDLHyperlink(editor, token) };
			default:
				break;
			}
		}

		return null;
	}
}
