package jp.hishidama.eclipse_plugin.dmdl_editor.editors.preference;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import static jp.hishidama.eclipse_plugin.dmdl_editor.editors.preference.PreferenceConst.*;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DMDLEditorColorPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	protected List<Field> list = new ArrayList<Field>();

	public DMDLEditorColorPreferencePage() {
		super("color preference");
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		IPreferenceStore store = getPreferenceStore();

		Composite composite = new Composite(parent, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 3; // 列数
			composite.setLayout(layout);
		}
		{
			Label label1 = new Label(composite, SWT.CENTER);
			label1.setText("column");
			Label label2 = new Label(composite, SWT.CENTER);
			label2.setText("color");
			Label label3 = new Label(composite, SWT.LEFT);
			label3.setText("bold");
		}

		list.clear();
		list.add(new Field(composite, "comment", store, COLOR_COMMENT,
				STYLE_COMMENT));
		list.add(new Field(composite, "attribute", store, COLOR_ANNOTATION,
				STYLE_ANNOTATION));
		list.add(new Field(composite, "description", store, COLOR_DESCRIPTION,
				STYLE_DESCRIPTION));
		list.add(new Field(composite, "model type", store, COLOR_MODEL_TYPE,
				STYLE_MODEL_TYPE));
		list.add(new Field(composite, "data type", store, COLOR_DATA_TYPE,
				STYLE_DATA_TYPE));
		list.add(new Field(composite, "summarized type", store, COLOR_SUM_TYPE,
				STYLE_SUM_TYPE));

		return composite;
	}

	@Override
	protected void performDefaults() {
		for (Field f : list) {
			f.performDefault();
		}
	}

	@Override
	public boolean performOk() {
		for (Field f : list) {
			f.performOk();
		}
		return true;
	}

	protected static class Field {
		protected IPreferenceStore store;
		protected String colorKey;
		protected String styleKey;
		protected ColorSelector color;
		protected Button check;

		public Field(Composite composite, String text, IPreferenceStore store,
				String colorKey, String styleKey) {
			this.store = store;
			this.colorKey = colorKey;
			this.styleKey = styleKey;

			Label label = new Label(composite, SWT.NONE);
			label.setText(text);

			color = new ColorSelector(composite);
			check = new Button(composite, SWT.CHECK | SWT.CENTER);

			RGB rgb = PreferenceConverter.getColor(store, colorKey);
			int style = store.getInt(styleKey);
			set(rgb, style);
		}

		public void performDefault() {
			RGB rgb = PreferenceConverter.getDefaultColor(store, colorKey);
			int style = store.getDefaultInt(styleKey);
			set(rgb, style);
		}

		protected void set(RGB rgb, int style) {
			color.setColorValue(rgb);
			check.setSelection((style & SWT.BOLD) != 0);
		}

		public void performOk() {
			RGB rgb = color.getColorValue();
			PreferenceConverter.setValue(store, colorKey, rgb);

			int style = SWT.NORMAL;
			if (check.getSelection()) {
				style |= SWT.BOLD;
			}
			store.setValue(styleKey, style);
		}
	}
}
