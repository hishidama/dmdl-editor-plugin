package jp.hishidama.eclipse_plugin.dmdl_editor.editors;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.partition.DMDLPartitionScanner;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.MatchingCharacterPainter;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.editors.text.TextEditor;

public class DMDLEditor extends TextEditor implements IPropertyChangeListener {
	private ColorManager colorManager = new ColorManager();

	protected ProjectionSupport projectionSupport;

	/**
	 * コンストラクター.
	 */
	public DMDLEditor() {
		setDocumentProvider(new DMDLDocumentProvider());
		setSourceViewerConfiguration(new DMDLConfiguration(colorManager));

		// Preferenceの更新リスナーを登録
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.addPropertyChangeListener(this);
	}

	@Override
	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles) {
		// フォールディングの為のViewerを作成
		ISourceViewer viewer = new ProjectionViewer(parent, ruler,
				fOverviewRuler, true, styles);
		getSourceViewerDecorationSupport(viewer);
		return viewer;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		{ // フォールディングの設定
			ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
			projectionSupport = new ProjectionSupport(viewer,
					getAnnotationAccess(), getSharedColors());
			projectionSupport.install();
			viewer.doOperation(ProjectionViewer.TOGGLE);
			updateFolding();
		}
		{ // 対応する括弧の強調表示の設定
			MatchingCharacterPainter painter = new MatchingCharacterPainter(
					getSourceViewer(), new DMBraceMatcher());
			painter.setColor(Display.getDefault()
					.getSystemColor(SWT.COLOR_GRAY));

			ITextViewerExtension2 extension = (ITextViewerExtension2) getSourceViewer();
			extension.addPainter(painter);
		}
	}

	@Override
	public void dispose() {
		// Preferenceの更新リスナーを削除
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.removePropertyChangeListener(this);

		colorManager.dispose();
		super.dispose();
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (projectionSupport != null) {
			Object obj = projectionSupport.getAdapter(getSourceViewer(),
					adapter);
			if (obj != null) {
				return obj;
			}
		}

		return super.getAdapter(adapter);
	}

	/**
	 * Preferenceの更新イベント処理.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// 色をPreferenceから取得し直す
		DMDLConfiguration config = (DMDLConfiguration) getSourceViewerConfiguration();
		config.updatePreferences();

		// エディターを再描画する
		getSourceViewer().invalidateTextPresentation();
	}

	@Override
	public void doSaveAs() {
		super.doSaveAs();

		updateFolding();
	}

	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		super.doSave(progressMonitor);

		updateFolding();
	}

	/**
	 * フォールディング範囲を最新状態に更新する.
	 * <p>
	 * 『Eclipse3.4 プラグイン開発 徹底攻略』p.184
	 * </p>
	 */
	protected void updateFolding() {
		try {
			ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
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
			IDocument document = getDocumentProvider().getDocument(
					getEditorInput());
			for (int offset = 0; offset < document.getLength();) {
				ITypedRegion part = document.getPartition(offset);
				String type = part.getType();
				if (DMDLPartitionScanner.DMDL_BLOCK.equals(type)
						|| DMDLPartitionScanner.DMDL_COMMENT.equals(type)) {
					int startLine = document.getLineOfOffset(part.getOffset());
					int endLine = document.getLineOfOffset(part.getOffset()
							+ part.getLength() - 1);
					if (startLine != endLine) {
						Position pos = new Position(part.getOffset(),
								part.getLength() - 1);
						model.addAnnotation(new ProjectionAnnotation(), pos);
					}
				}
				offset += part.getLength();
			}
		} catch (Exception e) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
					"updateFolding error.", e));
		}
	}
}
