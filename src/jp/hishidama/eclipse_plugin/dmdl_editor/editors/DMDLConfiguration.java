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
	private AttributeManager attrManager;

	/**
	 * �R���X�g���N�^�[.
	 *
	 * @param colorManager
	 */
	public DMDLConfiguration(ColorManager colorManager) {
		attrManager = new AttributeManager(colorManager);
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE,
				DMDLPartitionScanner.DMDL_COMMENT,
				DMDLPartitionScanner.DMDL_BLOCK, };
	}

	private DMDefaultScanner defaultScanner;

	protected DMDefaultScanner getDefaultScanner() {
		if (defaultScanner == null) {
			defaultScanner = new DMDefaultScanner(attrManager);
			defaultScanner.setDefaultReturnToken(new Token(attrManager
					.getDefaultAttribute()));
		}
		return defaultScanner;
	}

	private DMBlockScanner blockScanner;

	protected DMBlockScanner getBlockScanner() {
		if (blockScanner == null) {
			blockScanner = new DMBlockScanner(attrManager);
			blockScanner.setDefaultReturnToken(new Token(attrManager
					.getDefaultAttribute()));
		}
		return blockScanner;
	}

	private PresentationReconciler reconciler;
	private NonRuleBasedDamagerRepairer commentDamagerPepairer;

	@Override
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		reconciler = new PresentationReconciler();

		{ // �f�t�H���g�̐F�̐ݒ�
			DMDefaultScanner scanner = getDefaultScanner();
			DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
			reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
			reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		}
		{ // �f�[�^���f���u���b�N���̐F�̐ݒ�
			DMBlockScanner scanner = getBlockScanner();
			DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
			reconciler.setDamager(dr, DMDLPartitionScanner.DMDL_BLOCK);
			reconciler.setRepairer(dr, DMDLPartitionScanner.DMDL_BLOCK);
		}
		{ // �R�����g�̐F�̐ݒ�
			NonRuleBasedDamagerRepairer dr = new NonRuleBasedDamagerRepairer(
					attrManager.getCommentAttribute());
			commentDamagerPepairer = dr;
			reconciler.setDamager(dr, DMDLPartitionScanner.DMDL_COMMENT);
			reconciler.setRepairer(dr, DMDLPartitionScanner.DMDL_COMMENT);
		}

		return reconciler;
	}

	/**
	 * Preference�X�V���ɌĂ΂�鏈��.
	 * <p>
	 * �F��ݒ肵�����B
	 * </p>
	 */
	public void updatePreferences() {
		// �f�t�H���g�̐F�̐ݒ�
		getDefaultScanner().initialize();

		// �f�[�^���f���u���b�N���̐F�̐ݒ�
		getBlockScanner().initialize();

		// �R�����g�̐F�̐ݒ�
		commentDamagerPepairer.setDefaultTextAttribute(attrManager
				.getCommentAttribute());
	}
}
