package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.marker;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLTextEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index.PositionUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelList;

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
		return PositionUtil.getQualifiedName(editor.getProject(), token);
	}
}
