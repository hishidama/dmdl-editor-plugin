package jp.hishidama.eclipse_plugin.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

public abstract class EditDialog extends Dialog {

	private String windowTitle;

	public EditDialog(Shell parentShell, String windowTitle) {
		super(parentShell);
		this.windowTitle = windowTitle;
	}

	@Override
	public void create() {
		super.create();

		getShell().setText(windowTitle);

		refresh();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = createDialogAreaComposite(parent);

		createFields(composite);

		return composite;
	}

	private Composite createDialogAreaComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		applyDialogFont(composite);

		return composite;
	}

	protected abstract void createFields(Composite composite);

	protected abstract void refresh();

	protected Button createRadioField(Composite composite, String label, String label1, String label2) {
		createLabel(composite, label);

		Composite field = new Composite(composite, SWT.NONE);
		field.setLayout(new RowLayout(SWT.HORIZONTAL));
		final Button button1 = new Button(field, SWT.RADIO);
		button1.setText(label1);
		final Button button2 = new Button(field, SWT.RADIO);
		button2.setText(label2);

		button1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshOkButton();
			}
		});

		return button1;
	}

	protected Text createTextField(Composite composite, String label) {
		createLabel(composite, label);
		Text text = crateText(composite);
		return text;
	}

	protected Tree createTreeField(Composite composite, String label) {
		createLabel(composite, label);

		Tree tree = createTree(composite);
		return tree;
	}

	protected Table createCheckedTable(Composite composite, String label) {
		createLabel(composite, label);

		Table table = createCheckedTable(composite);
		return table;
	}

	protected void createLabel(Composite composite, String text) {
		Label label = new Label(composite, SWT.LEFT);
		label.setText(text);
	}

	protected Text crateText(Composite composite) {
		final Text text = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 128 * 3;
		text.setLayoutData(data);
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				refreshOkButton();
			}
		});
		return text;
	}

	protected Tree createTree(Composite composite) {
		Tree tree = new Tree(composite, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 128 * 3;
		data.heightHint = 128 * 2;
		tree.setLayoutData(data);

		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshOkButton();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (refreshOkButton()) {
					okPressed();
				}
			}
		});

		return tree;
	}

	protected Table createCheckedTable(Composite composite) {
		Table table = new Table(composite, SWT.SINGLE | SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 128 * 3;
		data.heightHint = 128 * 2;
		table.setLayoutData(data);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshOkButton();
			}
		});

		return table;
	}

	private boolean refreshOkButton() {
		Button ok = getButton(IDialogConstants.OK_ID);
		if (ok == null) {
			return false;
		}
		if (validate()) {
			ok.setEnabled(true);
			return true;
		} else {
			ok.setEnabled(false);
			return false;
		}
	}

	protected abstract boolean validate();

	protected static String nonNull(String s) {
		return (s != null) ? s : "";
	}
}
