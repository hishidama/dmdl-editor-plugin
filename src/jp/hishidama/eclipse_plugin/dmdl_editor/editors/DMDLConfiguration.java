package jp.hishidama.eclipse_plugin.dmdl_editor.editors;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.AttributeManager;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.DMBlockScanner;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.DMDLPartitionScanner;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.DMDefaultScanner;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.NonRuleBasedDamagerRepairer;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class DMDLConfiguration extends SourceViewerConfiguration {
	private ColorManager colorManager;

	/**
	 * コンストラクター.
	 *
	 * @param colorManager
	 */
	public DMDLConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE,
				DMDLPartitionScanner.DMDL_COMMENT,
				DMDLPartitionScanner.DMDL_BLOCK, };
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		AttributeManager attrManager = new AttributeManager(colorManager);
		{ // デフォルトの色の設定
			DMDefaultScanner scanner = new DMDefaultScanner(attrManager);
			scanner.setDefaultReturnToken(new Token(attrManager
					.getDefaultAttribute()));
			DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
			reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
			reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		}
		{ // データモデルブロック内の色の設定
			DMBlockScanner scanner = new DMBlockScanner(attrManager);
			scanner.setDefaultReturnToken(new Token(attrManager
					.getDefaultAttribute()));
			DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
			reconciler.setDamager(dr, DMDLPartitionScanner.DMDL_BLOCK);
			reconciler.setRepairer(dr, DMDLPartitionScanner.DMDL_BLOCK);
		}
		{ // コメントの色の設定
			NonRuleBasedDamagerRepairer dr = new NonRuleBasedDamagerRepairer(
					attrManager.getCommentAttribute());
			reconciler.setDamager(dr, DMDLPartitionScanner.DMDL_COMMENT);
			reconciler.setRepairer(dr, DMDLPartitionScanner.DMDL_COMMENT);
		}

		return reconciler;
	}
}
