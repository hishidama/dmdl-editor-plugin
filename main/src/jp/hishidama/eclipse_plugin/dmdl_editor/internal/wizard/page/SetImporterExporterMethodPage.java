package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen.ImporterExporterGenerator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen.ImporterExporterGenerator.FieldData;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class SetImporterExporterMethodPage extends WizardPage {

	private final ImporterExporterGenerator generator;
	private final Map<String, Field> fieldMap = new LinkedHashMap<String, Field>();

	private ModifyListener listener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			validate(true);
		}
	};

	public SetImporterExporterMethodPage(ImporterExporterGenerator gen) {
		super("SetImporterExporterMethodPage." + gen.getDisplayName());
		setTitle("メソッドの内容の指定");
		setDescription(MessageFormat.format("{0}クラスの各メソッドの内容を入力して下さい。", gen.getDisplayName()));
		this.generator = gen;
		gen.initializeFields();
	}

	public final ImporterExporterGenerator getGenerator() {
		return generator;
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridData compositeGrid = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(compositeGrid);
		composite.setLayout(new GridLayout(1, false));

		rebuild(composite);
		validate(false);

		setControl(composite);
	}

	private void rebuild(Composite composite) {
		Map<String, List<FieldData>> map = generator.getFields();
		for (Entry<String, List<FieldData>> entry : map.entrySet()) {
			String groupName = entry.getKey();
			Group group = createGroup(composite, groupName);
			for (FieldData data : entry.getValue()) {
				if (data.combo == null) {
					createTextField(group, data.keyName, data.required, data.displayName, data.description,
							data.toolTip);
				} else {
					createComboField(group, data.keyName, data.required, data.displayName, data.description,
							data.toolTip, data.combo);
				}
			}
		}
	}

	private Group createGroup(Composite composite, String text) {
		Group group = new Group(composite, SWT.SHADOW_IN);
		group.setText(text);
		GridData groupGrid = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(groupGrid);

		group.setLayout(new GridLayout(3, false));

		return group;
	}

	private void createTextField(Group group, String key, boolean required, String label, String desc, String tip) {
		createLabel(group, required, label, desc, tip);

		Text text = new Text(group, SWT.BORDER);
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(grid);
		String value = getSetting(key);
		text.setText(nonNull(value));
		text.addModifyListener(listener);

		fieldMap.put(key, new Field(required, label, text));
	}

	private void createComboField(Group group, String key, boolean required, String label, String desc, String tip,
			List<String> values) {
		createLabel(group, required, label, desc, tip);

		Combo combo = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		combo.setLayoutData(grid);

		String value = getSetting(key);
		if (value == null) {
			value = values.get(0);
		}
		boolean found = false;
		for (String s : values) {
			combo.add(s);
			if (s.equals(value)) {
				found = true;
			}
		}
		if (!found) {
			value = values.get(0);
		}
		combo.setText(nonNull(value));
		combo.addModifyListener(listener);

		fieldMap.put(key, new Field(required, label, combo));
	}

	private void createLabel(Group group, boolean required, String label, String desc, String tip) {
		Label label1 = new Label(group, SWT.NONE);
		label1.setText(label);

		Label label2 = new Label(group, SWT.NONE);
		label2.setText(desc + (required ? "（必須）" : "（任意）"));
		label2.setToolTipText(tip);
	}

	private static class Field {
		public boolean required;
		public String label;
		public Widget widget;

		public Field(boolean required, String label, Widget widget) {
			this.required = required;
			this.label = label;
			this.widget = widget;
		}

		public String getText() {
			if (widget instanceof Text) {
				return ((Text) widget).getText().trim();
			} else if (widget instanceof Combo) {
				return ((Combo) widget).getText().trim();
			}
			throw new UnsupportedOperationException("widgetClass=" + widget.getClass());
		}
	}

	private void validate(boolean setError) {
		setPageComplete(false);
		for (Field f : fieldMap.values()) {
			if (f.required) {
				String s = f.getText();
				if (s.isEmpty()) {
					if (setError) {
						setErrorMessage(MessageFormat.format("{0}の値を入力して下さい。", f.label));
					}
					return;
				}
			}
		}

		setErrorMessage(null);
		setPageComplete(true);
	}

	public Map<String, String> getValues() {
		Map<String, String> map = new HashMap<String, String>(fieldMap.size());
		for (Entry<String, Field> entry : fieldMap.entrySet()) {
			String key = entry.getKey();
			Field f = entry.getValue();
			String value = f.getText();
			map.put(key, value);
			setSetting(key, value);
		}
		return map;
	}

	// DialogSettings
	private String getSetting(String key) {
		IDialogSettings settings = getDialogSettings();
		String value = settings.get(getKey1(key));
		if (value == null) {
			value = settings.get(getKey2(key));
		}
		return value;
	}

	private void setSetting(String key, String value) {
		IDialogSettings settings = getDialogSettings();
		settings.put(getKey1(key), value);
		settings.put(getKey2(key), value);
	}

	private String getKey1(String key) {
		return String.format("SetImporterExporterMethodPage.%s.%s", generator.getFullName(), key);
	}

	private String getKey2(String key) {
		return String.format("SetImporterExporterMethodPage.%s", key);
	}

	private static String nonNull(String s) {
		return (s != null) ? s : "";
	}
}
