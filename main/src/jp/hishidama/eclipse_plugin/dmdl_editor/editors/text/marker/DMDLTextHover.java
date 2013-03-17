package jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.marker;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLTextEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.index.IndexContainer;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.index.ModelIndex;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.index.PropertyIndex;
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

	private DMDLTextEditor editor;

	public DMDLTextHover(DMDLTextEditor editor, ISourceViewer sourceViewer) {
		super(sourceViewer);
		this.editor = editor;
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
					WordToken ref = word.getReferenceWord();
					if (ref != null) {
						ModelToken model = ref.getModelToken();
						if (model != null) {
							return model.getQualifiedName();
						}
						return name;
					} else {
						IndexContainer ic = IndexContainer.getContainer(editor
								.getProject());
						if (ic != null) {
							ModelIndex index = ic.findModel(name);
							if (index != null) {
								ModelToken model = index.getToken();
								String file = index.getFile().getFullPath()
										.lastSegment();
								return model.getQualifiedName() + " (" + file
										+ ")";
							}
						}
						return null;
					}
				}
				case REF_PROPERTY_NAME: {
					WordToken ref = word.getReferenceWord();
					if (ref != null) {
						IndexContainer ic = IndexContainer.getContainer(editor
								.getProject());
						PropertyToken prop = (PropertyToken) ref.getParent();
						return prop.getQualifiedName(ic);
					} else {
						DMDLToken model = word.findRefModelToken();
						if (model instanceof WordToken) {
							IndexContainer ic = IndexContainer
									.getContainer(editor.getProject());
							if (ic != null) {
								String modelName = ((WordToken) model)
										.getBody();
								PropertyIndex index = ic.findProperty(
										modelName, word.getBody());
								if (index != null) {
									PropertyToken p = index.getToken();
									String file = index.getFile().getFullPath()
											.lastSegment();
									return p.getQualifiedName(ic) + " (" + file
											+ ")";
								}
							}
						}
					}
				}
					return null;
				}
			} else if (token instanceof PropertyToken) {
				IndexContainer ic = IndexContainer.getContainer(editor
						.getProject());
				PropertyToken prop = (PropertyToken) token;
				return prop.getQualifiedName(ic);
			} else if (token instanceof ModelToken) {
				ModelToken model = (ModelToken) token;
				return model.getQualifiedName();
			} else if (token instanceof CommentToken) {
				return null;
			}
			token = token.getParent();
		}
		return null;
	}
}
