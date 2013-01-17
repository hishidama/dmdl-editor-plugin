package jp.hishidama.eclipse_plugin.dmdl_editor.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.RGB;

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

		RGB defaultColor = new RGB(0, 0, 0);
		{ // デフォルトの色の設定
			DMDefaultScanner scanner = new DMDefaultScanner(colorManager);
			scanner.setDefaultReturnToken(new Token(new TextAttribute(
					colorManager.getColor(defaultColor))));
			DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
			reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
			reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		}
		{ // データモデルブロック内の色の設定
			DMBlockScanner scanner = new DMBlockScanner(colorManager);
			scanner.setDefaultReturnToken(new Token(new TextAttribute(
					colorManager.getColor(defaultColor))));
			DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
			reconciler.setDamager(dr, DMDLPartitionScanner.DMDL_BLOCK);
			reconciler.setRepairer(dr, DMDLPartitionScanner.DMDL_BLOCK);
		}
		{ // コメントの色の設定
			RGB c = new RGB(0, 192, 0);
			NonRuleBasedDamagerRepairer dr = new NonRuleBasedDamagerRepairer(
					new TextAttribute(colorManager.getColor(c)));
			reconciler.setDamager(dr, DMDLPartitionScanner.DMDL_COMMENT);
			reconciler.setRepairer(dr, DMDLPartitionScanner.DMDL_COMMENT);
		}

		return reconciler;
	}
}
