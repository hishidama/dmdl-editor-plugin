package jp.hishidama.eclipse_plugin.dmdl_editor.editors;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.source.MatchingCharacterPainter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.editors.text.TextEditor;

public class DMDLEditor extends TextEditor implements IPropertyChangeListener {
	private ColorManager colorManager = new ColorManager();

	/**
	 * �R���X�g���N�^�[.
	 */
	public DMDLEditor() {
		setDocumentProvider(new DMDLDocumentProvider());
		setSourceViewerConfiguration(new DMDLConfiguration(colorManager));

		// Preference�̍X�V���X�i�[��o�^
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.addPropertyChangeListener(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		MatchingCharacterPainter painter = new MatchingCharacterPainter(
				getSourceViewer(), new DMBraceMatcher());
		painter.setColor(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));

		ITextViewerExtension2 extension = (ITextViewerExtension2) getSourceViewer();
		extension.addPainter(painter);
	}

	@Override
	public void dispose() {
		// Preference�̍X�V���X�i�[���폜
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.removePropertyChangeListener(this);

		colorManager.dispose();
		super.dispose();
	}

	// Preference�̍X�V�C�x���g����
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// �F��Preference����擾������
		DMDLConfiguration config = (DMDLConfiguration) getSourceViewerConfiguration();
		config.updatePreferences();

		// �G�f�B�^�[���ĕ`�悷��
		getSourceViewer().invalidateTextPresentation();
	}
}
