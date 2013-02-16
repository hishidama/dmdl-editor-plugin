package jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.CommentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken;

import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.MarkerAnnotation;

public class DMDLTextHover extends DefaultTextHover implements
		ITextHoverExtension2 {

	public DMDLTextHover(ISourceViewer sourceViewer) {
		super(sourceViewer);
	}

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		return (String) getHoverInfo2(textViewer, hoverRegion);
	}

	@Override
	protected boolean isIncluded(Annotation annotation) {
		if (annotation instanceof MarkerAnnotation) {
			return true;
		}
		return false;
	}

	@Override
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		@SuppressWarnings("deprecation")
		String message = super.getHoverInfo(textViewer, hoverRegion);
		if (message != null) {
			return message;
		}

		DMDLDocument document = (DMDLDocument) textViewer.getDocument();
		ModelList models = document.getModelList();
		DMDLToken token = models.getTokenByOffset(hoverRegion.getOffset());
		return getTokenMessage(token);
	}

	private String getTokenMessage(DMDLToken token) {
		while (token != null) {
			if (token instanceof WordToken) {
				WordToken word = (WordToken) token;
				switch (word.getWordType()) {
				case REF_MODEL_NAME: {
					String name = word.getBody();
					String type = null;
					WordToken ref = word.getReferenceWord();
					if (ref != null) {
						type = ref.getModelType();
					}
					if (type != null) {
						return name + " (" + type + ")";
					} else {
						return name;
					}
				}
				case REF_PROPERTY_NAME: {
					String name = word.getBody();
					String type = null;
					WordToken ref = word.getReferenceWord();
					if (ref != null) {
						type = ref.getDataType();
					}
					if (type != null) {
						return name + " : " + type;
					} else {
						return name;
					}
				}
				}
			} else if (token instanceof PropertyToken) {
				PropertyToken prop = (PropertyToken) token;
				String name = prop.getName();
				String type = prop.getDataType();
				if (type != null) {
					return name + " : " + type;
				} else {
					return name;
				}
			} else if (token instanceof ModelToken) {
				ModelToken model = (ModelToken) token;
				String name = model.getModelName();
				String type = model.getModelType();
				if (type != null) {
					return name + " (" + type + ")";
				} else {
					return name;
				}
			} else if (token instanceof CommentToken) {
				return null;
			}
			token = token.getParent();
		}
		return null;
	}
}
