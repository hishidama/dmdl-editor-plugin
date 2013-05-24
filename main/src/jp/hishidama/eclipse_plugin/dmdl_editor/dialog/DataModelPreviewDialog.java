package jp.hishidama.eclipse_plugin.dmdl_editor.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DataModelPreviewDialog extends Dialog {
	private String windowTitle;
	private String text;

	private Text textArea;

	public DataModelPreviewDialog(Shell parentShell, String windowTitle) {
		super(parentShell);
		this.windowTitle = windowTitle;
	}

	@Override
	public void create() {
		super.create();

		getShell().setText(windowTitle);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		textArea = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
		// textArea.setFont(composite.getFont());
		GridData grid = new GridData(GridData.FILL_BOTH);
		grid.widthHint = 256;
		grid.heightHint = 256;
		textArea.setLayoutData(grid);
		if (text != null) {
			textArea.setText(text);
		}

		return composite;
	}

	public void setText(String text) {
		this.text = text;
		if (textArea != null) {
			textArea.setText(text);
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}
}
