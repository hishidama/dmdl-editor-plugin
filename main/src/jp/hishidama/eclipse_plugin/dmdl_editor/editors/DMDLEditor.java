package jp.hishidama.eclipse_plugin.dmdl_editor.editors;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.folding.FoldingManager;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.hyperlink.DMDLHyperlinkDetector;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.outline.OutlinePage;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.ColorManager;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.MatchingCharacterPainter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class DMDLEditor extends TextEditor implements IPropertyChangeListener {
	private ColorManager colorManager = new ColorManager();

	protected FoldingManager foldingManager = new FoldingManager();
	protected OutlinePage outlinePage;

	/**
	 * コンストラクター.
	 */
	public DMDLEditor() {
		setDocumentProvider(new DMDLDocumentProvider());
		setSourceViewerConfiguration(new DMDLConfiguration(this, colorManager));

		// Preferenceの更新リスナーを登録
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.addPropertyChangeListener(this);
	}

	@Override
	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles) {
		// フォールディングの為のViewerを作成
		ISourceViewer viewer = foldingManager.createSourceViewer(parent, ruler,
				fOverviewRuler, styles);
		getSourceViewerDecorationSupport(viewer);
		return viewer;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		{ // フォールディングの設定
			foldingManager.install(getAnnotationAccess(), getSharedColors());
			update();
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
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);

		DMDLConfiguration configuration = (DMDLConfiguration) getSourceViewerConfiguration();
		DMDLHyperlinkDetector detector = configuration.getHyperlinkDetector();
		detector.init(this);
	}

	@Override
	protected void initializeKeyBindingScopes() {
		super.initializeKeyBindingScopes();
		setKeyBindingScopes(new String[] { "dmdl-editor-plugin.context" }); //$NON-NLS-1$
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
		if (IContentOutlinePage.class.equals(adapter)) {
			if (outlinePage == null) {
				outlinePage = new OutlinePage(this);
			}
			return outlinePage;
		}

		Object obj = foldingManager.getAdapter(adapter);
		if (obj != null) {
			return obj;
		}

		return super.getAdapter(adapter);
	}

	@Override
	protected void handleCursorPositionChanged() {
		super.handleCursorPositionChanged();

		jumpOutline();
	}

	private boolean inSelect = false;

	protected void jumpOutline() {
		if (outlinePage == null) {
			return;
		}
		DMDLDocument document = getDocument();
		if (document == null) {
			return;
		}
		ModelList models = document.getModelList();

		ISourceViewer sourceViewer = getSourceViewer();
		StyledText styledText = sourceViewer.getTextWidget();
		int caret = widgetOffset2ModelOffset(sourceViewer,
				styledText.getCaretOffset());
		DMDLToken token = models.getTokenByOffset(caret);
		if (token instanceof WordToken) {
			WordToken word = (WordToken) token;
			switch (word.getWordType()) {
			case MODEL_NAME:
				inSelect = true;
				try {
					DMDLToken model = token.getParent();
					outlinePage.selectToken(model);
				} finally {
					inSelect = false;
				}
				break;
			default:
				DMDLToken parent = token.getParent();
				if (parent instanceof PropertyToken) {
					inSelect = true;
					try {
						outlinePage.selectToken(parent);
					} finally {
						inSelect = false;
					}
				}
				break;
			}
		}
	}

	public boolean inSelect() {
		return inSelect;
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

		update();
	}

	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		super.doSave(progressMonitor);

		update();
	}

	private void update() {
		DMDLDocument document = getDocument();
		ModelList models = document.getModelList();
		// models = new DMDLSimpleParser().parse(document);

		// フォールディング範囲を最新状態に更新する
		foldingManager.updateFolding(document, models);

		// アウトラインを最新状態に更新する
		if (outlinePage != null) {
			outlinePage.refresh(models);
		}
	}

	public IProject getProject() {
		return getFile().getProject();
	}

	public IFile getFile() {
		return getEditorInput().getFile();
	}

	@Override
	public IFileEditorInput getEditorInput() {
		return (IFileEditorInput) super.getEditorInput();
	}

	public DMDLDocument getDocument() {
		IDocument document = getDocumentProvider()
				.getDocument(getEditorInput());
		return (DMDLDocument) document;
	}
}
