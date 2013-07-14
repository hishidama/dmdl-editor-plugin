package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class SelectAddRemovePage extends WizardPage {
	private static final String SETTINGS_REMOVE = "AttributeWizard.add/remove";
	private static final String SETTINGS_ADD_PATTERN = "AttributeWizard.add.pattern";

	public static enum AddPattern {
		NOTHING, REPLACE, ADD
	}

	private Button addButton;
	private List<Button> addPatternList = new ArrayList<Button>();

	public SelectAddRemovePage() {
		super("SelectAddRemovePage");
		setTitle("属性の更新内容の選択");
		setDescription("属性を追加するのか削除するのかを選択して下さい。");
	}

	@Override
	public void createControl(Composite parent) {
		IDialogSettings settings = getDialogSettings();

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));
		{
			addButton = new Button(composite, SWT.RADIO);
			addButton.setText("追加");
			{
				Group addGroup = new Group(composite, SWT.NONE);
				addGroup.setText("属性が既存の場合の動作");
				addGroup.setLayout(new GridLayout(1, true));
				AddPattern select;
				try {
					select = AddPattern.valueOf(settings.get(SETTINGS_ADD_PATTERN));
				} catch (Exception e) {
					select = AddPattern.NOTHING;
				}
				createAddRadio(addGroup, "追加しない（既存の属性を残す）", AddPattern.NOTHING, select);
				createAddRadio(addGroup, "新しい属性に置換する", AddPattern.REPLACE, select);
				createAddRadio(addGroup, "追加する（二重になる）", AddPattern.ADD, select);
			}
			addButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setAddRadioEnabled(true);
				}
			});

			Button delButton = new Button(composite, SWT.RADIO);
			delButton.setText("削除");
			delButton.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).create());
			delButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setAddRadioEnabled(false);
				}
			});

			boolean add = !settings.getBoolean(SETTINGS_REMOVE);
			if (add) {
				addButton.setSelection(true);
				setAddRadioEnabled(true);
			} else {
				delButton.setSelection(true);
				setAddRadioEnabled(false);
			}
		}

		setControl(composite);
	}

	private void createAddRadio(Group group, String text, AddPattern pattern, AddPattern select) {
		Button button = new Button(group, SWT.RADIO);
		button.setText(text);
		button.setData(pattern);
		addPatternList.add(button);

		button.setSelection(pattern == select);
	}

	private void setAddRadioEnabled(boolean enabled) {
		for (Button button : addPatternList) {
			button.setEnabled(enabled);
		}
	}

	public boolean isAdd() {
		boolean add = addButton.getSelection();
		IDialogSettings settings = getDialogSettings();
		settings.put(SETTINGS_REMOVE, !add);

		getAddPattern(); // settingsの保存

		return add;
	}

	public AddPattern getAddPattern() {
		for (Button button : addPatternList) {
			if (button.getSelection()) {
				AddPattern pattern = (AddPattern) button.getData();
				IDialogSettings settings = getDialogSettings();
				settings.put(SETTINGS_ADD_PATTERN, pattern.name());
				return pattern;
			}
		}
		return AddPattern.ADD;
	}
}
