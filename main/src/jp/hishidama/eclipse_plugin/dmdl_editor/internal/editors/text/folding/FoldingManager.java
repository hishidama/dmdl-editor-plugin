package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.folding;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.BlockToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.CommentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;

public class FoldingManager {

	protected ProjectionViewer viewer;
	protected ProjectionSupport projectionSupport;

	/**
	 * フォールディングの為のViewerを作成.
	 *
	 * @param parent
	 * @param ruler
	 * @param overviewRuler
	 * @param showsAnnotationOverview
	 * @param styles
	 * @return ProjectionViewer
	 */
	public ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, IOverviewRuler overviewRuler,
			boolean showsAnnotationOverview, int styles) {
		viewer = new ProjectionViewer(parent, ruler, overviewRuler, showsAnnotationOverview, styles);
		return viewer;
	}

	public void install(IAnnotationAccess annotationAccess, ISharedTextColors sharedColors) {
		projectionSupport = new ProjectionSupport(viewer, annotationAccess, sharedColors);
		projectionSupport.install();
		viewer.doOperation(ProjectionViewer.TOGGLE);
	}

	public Object getAdapter(Class<?> adapter) {
		if (projectionSupport != null) {
			return projectionSupport.getAdapter(viewer, adapter);
		}
		return null;
	}

	/**
	 * フォールディング範囲を最新状態に更新する.
	 * <p>
	 * 『Eclipse3.4 プラグイン開発 徹底攻略』p.184
	 * </p>
	 */
	public void updateFolding(IDocument document, ModelList models) {
		try {
			if (viewer == null) {
				return;
			}
			ProjectionAnnotationModel model = viewer.getProjectionAnnotationModel();
			if (model == null) {
				return;
			}

			// 全てのフォールディング範囲をクリア
			model.removeAllAnnotations();

			// ドキュメントを走査してフォールディング範囲を決定
			applyFolding(document, models, model);
		} catch (Exception e) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID, "updateFolding error.", e));
		}
	}

	protected void applyFolding(IDocument document, ModelList models, ProjectionAnnotationModel model)
			throws BadLocationException {
		if (models == null) {
			return;
		}
		for (DMDLToken token : models.getBody()) {
			if (token instanceof ModelToken) {
				applyFolding(document, (ModelToken) token, model);
			}
		}
	}

	protected void applyFolding(IDocument document, ModelToken modelToken, ProjectionAnnotationModel model) {
		for (DMDLToken token : modelToken.getBody()) {
			if (token instanceof BlockToken) {
				applyFolding(document, (BlockToken) token, model);
			} else if (token instanceof CommentToken) {
				applyFolding(document, (CommentToken) token, model);
			}
		}
	}

	protected void applyFolding(IDocument document, BlockToken token, ProjectionAnnotationModel model) {
		int start = token.getStart();
		int end = token.getEnd();
		loop: while (end < document.getLength()) {
			try {
				char c = document.getChar(end);
				switch (c) {
				case ';':
				case ' ':
				case '\t':
					end++;
					break;
				case '\r': {
					end++;
					char d = document.getChar(end);
					if (d == '\n') {
						end++;
					}
					break loop;
				}
				case '\n':
					end++;
					break loop;
				default:
					break loop;
				}
			} catch (BadLocationException e) {
				break;
			}
		}
		applyFolding(document, start, end, model);
	}

	protected void applyFolding(IDocument document, CommentToken token, ProjectionAnnotationModel model) {
		if (!token.isBlock()) {
			return;
		}
		int start = token.getStart();
		int end = token.getEnd();
		loop: while (end < document.getLength()) {
			try {
				char c = document.getChar(end);
				switch (c) {
				case ' ':
				case '\t':
					end++;
					break;
				case '\r': {
					end++;
					char d = document.getChar(end);
					if (d == '\n') {
						end++;
					}
					break loop;
				}
				case '\n':
					end++;
					break loop;
				default:
					break loop;
				}
			} catch (BadLocationException e) {
				break;
			}
		}
		applyFolding(document, start, end, model);
	}

	protected void applyFolding(IDocument document, int start, int end, ProjectionAnnotationModel model) {
		try {
			int line0 = document.getLineOfOffset(start);
			int line1 = document.getLineOfOffset(end - 1);
			if (line0 == line1) {
				return;
			}
		} catch (BadLocationException e) {
		}
		Position pos = new Position(start, end - start);
		model.addAnnotation(new ProjectionAnnotation(), pos);
	}
}
