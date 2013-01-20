package jp.hishidama.eclipse_plugin.dmdl_editor.editors;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.AttributeManager;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.ColorManager;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.DMScanner;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.NonRuleBasedDamagerRepairer;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.PartitionDamagerRepairer;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.partition.DMDLPartitionScanner;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class DMDLConfiguration extends SourceViewerConfiguration {
	private AttributeManager attrManager;

	/**
	 * コンストラクター.
	 *
	 * @param colorManager
	 */
	public DMDLConfiguration(ColorManager colorManager) {
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
			blockScanner.setDefaultReturnToken(new Token(attrManager
					.getDefaultAttribute()));
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
}
