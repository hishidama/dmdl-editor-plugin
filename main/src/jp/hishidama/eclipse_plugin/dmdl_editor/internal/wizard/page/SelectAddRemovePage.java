package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class SelectAddRemovePage extends WizardPage {
	private static final String SETTINGS_REMOVE = "AttributeWizard.add/remove";

	private Button addButton;

	public SelectAddRemovePage() {
		super("SelectAddRemovePage");
		setTitle("属性の更新内容の選択");
		setDescription("属性を追加するのか削除するのかを選択して下さい。");
	}

	@Override
	public void createControl(Composite parent) {
		IDialogSettings settings = getDialogSettings();

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		GridData compositeGrid = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(compositeGrid);
		{
			addButton = new Button(composite, SWT.RADIO);
			addButton.setText("追加");
			Button delButton = new Button(composite, SWT.RADIO);
			delButton.setText("削除");
			boolean add = !settings.getBoolean(SETTINGS_REMOVE);
			if (add) {
				addButton.setSelection(true);
			} else {
				delButton.setSelection(true);
			}
		}

		setControl(composite);
	}

	public boolean isAdd() {
		boolean add = addButton.getSelection();
		IDialogSettings settings = getDialogSettings();
		settings.put(SETTINGS_REMOVE, !add);
		return add;
	}
}
