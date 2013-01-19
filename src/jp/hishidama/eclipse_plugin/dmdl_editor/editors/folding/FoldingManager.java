package jp.hishidama.eclipse_plugin.dmdl_editor.editors.folding;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.partition.DMDLPartitionScanner;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.partition.DMDLPartitionRule;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
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
	 * @param styles
	 * @return ProjectionViewer
	 */
	public ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, IOverviewRuler overviewRuler, int styles) {
		viewer = new ProjectionViewer(parent, ruler, overviewRuler, true,
				styles);
		return viewer;
	}

	public void install(IAnnotationAccess annotationAccess,
			ISharedTextColors sharedColors) {
		projectionSupport = new ProjectionSupport(viewer, annotationAccess,
				sharedColors);
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
	public void updateFolding(IDocument document) {
		try {
			if (viewer == null) {
				return;
			}
			ProjectionAnnotationModel model = viewer
					.getProjectionAnnotationModel();
			if (model == null) {
				return;
			}

			// 全てのフォールディング範囲をクリア
			model.removeAllAnnotations();

			// ドキュメントのパーティションを走査してフォールディング範囲を決定
			for (int offset = 0; offset < document.getLength();) {
				ITypedRegion part = document.getPartition(offset);
				String type = part.getType();
				if (DMDLPartitionScanner.DMDL_BLOCK.equals(type)) {
					applyFolding(document, part, model);
				}
				offset += part.getLength();
			}
		} catch (Exception e) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
					"updateFolding error.", e));
		}
	}

	private ApplyRule rule = new ApplyRule();

	protected void applyFolding(IDocument document, ITypedRegion part,
			ProjectionAnnotationModel model) throws BadLocationException {
		int offset = part.getOffset();
		int length = part.getLength();
		Scanner scanner = new Scanner(document, offset, length, model);
		for (;;) {
			IToken t = rule.evaluate(scanner);
			if (t == Token.EOF) {
				break;
			}
			if (t == Token.UNDEFINED) {
				scanner.read();
			}
		}
	}

	class Scanner implements ICharacterScanner {
		private IDocument document;
		private int offset;
		private int length;
		private int pos;
		private ProjectionAnnotationModel model;

		public Scanner(IDocument document, int offset, int length,
				ProjectionAnnotationModel model) {
			this.document = document;
			this.offset = offset;
			this.length = length;
			pos = 0;
			this.model = model;
		}

		@Override
		public char[][] getLegalLineDelimiters() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getColumn() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int read() {
			if (pos >= length) {
				return EOF;
			}

			int n = offset + pos;
			pos++;
			try {
				return document.getChar(n);
			} catch (BadLocationException e) {
				pos--;
				return EOF;
			}
		}

		@Override
		public void unread() {
			pos--;
		}

		public int getOffset() {
			return offset + pos - 1;
		}
	}

	class ApplyRule extends DMDLPartitionRule {

		public ApplyRule() {
			super(null);
		}

		@Override
		protected void readBlock(ICharacterScanner scanner) {
			Scanner s = (Scanner) scanner;
			int start = s.getOffset();
			super.readBlock(scanner);
			int end = s.getOffset();
			apply(s.document, start, end, s.model);
		}

		@Override
		protected void readToCommentEnd(ICharacterScanner scanner, boolean top) {
			if (top) {
				Scanner s = (Scanner) scanner;
				int start = s.getOffset();
				super.readToCommentEnd(scanner, top);
				int end = s.getOffset();
				apply(s.document, start, end, s.model);
			}
		}

		void apply(IDocument document, int start, int end,
				ProjectionAnnotationModel model) {
			try {
				int startLine = document.getLineOfOffset(start);
				int endLine = document.getLineOfOffset(end);
				if (startLine != endLine) {
					Position pos = new Position(start, end - start + 1);
					model.addAnnotation(new ProjectionAnnotation(), pos);
				}
			} catch (BadLocationException e) {
			}
		}

	}
}
