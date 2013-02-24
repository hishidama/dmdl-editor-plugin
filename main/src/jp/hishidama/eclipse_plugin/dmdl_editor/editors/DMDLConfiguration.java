package jp.hishidama.eclipse_plugin.dmdl_editor.editors;

import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.assist.DMDLContentAssistProcessor;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.format.DMDLContentFormatter;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker.DMDLTextHover;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.AttributeManager;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.ColorManager;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.DMScanner;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.NonRuleBasedDamagerRepairer;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.PartitionDamagerRepairer;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.partition.DMDLPartitionScanner;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

public class DMDLConfiguration extends TextSourceViewerConfiguration {
	private DMDLEditor editor;
	private AttributeManager attrManager;

	/**
	 * コンストラクター.
	 *
	 * @param editor
	 * @param colorManager
	 */
	public DMDLConfiguration(DMDLEditor editor, ColorManager colorManager) {
		super(Activator.getDefault().getPreferenceStore());
		this.editor = editor;
		attrManager = new AttributeManager(colorManager);
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE,
				DMDLPartitionScanner.DMDL_BLOCK, };
	}

	private DMScanner blockScanner;

	protected DMScanner getBlockScanner() {
		if (blockScanner == null) {
			blockScanner = new DMScanner(attrManager);
		}
		return blockScanner;
	}

	private PresentationReconciler reconciler;

	@Override
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		reconciler = new PresentationReconciler();

		{ // デフォルトの色の設定
			NonRuleBasedDamagerRepairer dr = new NonRuleBasedDamagerRepairer(
					attrManager.getDefaultAttribute());
			reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
			reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		}
		{ // データモデルブロック内の色の設定
			DMScanner scanner = getBlockScanner();
			PartitionDamagerRepairer dr = new PartitionDamagerRepairer(scanner);
			reconciler.setDamager(dr, DMDLPartitionScanner.DMDL_BLOCK);
			reconciler.setRepairer(dr, DMDLPartitionScanner.DMDL_BLOCK);
		}

		return reconciler;
	}

	/**
	 * Preference更新時に呼ばれる処理.
	 * <p>
	 * 色を設定し直す。
	 * </p>
	 */
	public void updatePreferences() {
		// データモデルブロック内の色の設定
		getBlockScanner().initialize();
	}

	public static final String HYPERLINK_TARGET_ID = "dmdl-editor-plugin.hyperlink.target";

	@Override
	protected Map<String, IAdaptable> getHyperlinkDetectorTargets(
			ISourceViewer sourceViewer) {
		@SuppressWarnings("unchecked")
		Map<String, IAdaptable> targets = super
				.getHyperlinkDetectorTargets(sourceViewer);
		targets.put(HYPERLINK_TARGET_ID, editor);
		return targets;
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = new ContentAssistant();

		DMDLContentAssistProcessor processor = new DMDLContentAssistProcessor();
		assistant.setContentAssistProcessor(processor,
				DMDLPartitionScanner.DMDL_BLOCK);
		assistant.setContentAssistProcessor(processor,
				IDocument.DEFAULT_CONTENT_TYPE);
		assistant.install(sourceViewer);

		return assistant;
	}

	@Override
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		return new DMDLContentFormatter();
	}

	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new DefaultAnnotationHover();
	}

	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer,
			String contentType) {
		return new DMDLTextHover(editor, sourceViewer);
	}
}
