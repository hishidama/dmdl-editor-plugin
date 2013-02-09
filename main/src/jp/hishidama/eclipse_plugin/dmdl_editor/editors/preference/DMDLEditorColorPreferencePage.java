package jp.hishidama.eclipse_plugin.dmdl_editor.editors.preference;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import static jp.hishidama.eclipse_plugin.dmdl_editor.editors.preference.PreferenceConst.*;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
		super("DMDLEditorColorPreferencePage");
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		setTitle("dmdl-editor color preference");
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
	public void performApply() {
		for (Field f : list) {
			f.performApply();
		}
	}

	@Override
	public boolean performCancel() {
		for (Field f : list) {
			f.performCancel();
		}
		return true;
	}

	protected static class Field {
		protected IPreferenceStore store;
		protected String colorKey;
		protected String styleKey;
		protected ColorSelector color;
		protected Button check;
		protected RGB backupColor;
		protected int backupStyle;

		public Field(Composite composite, String text, IPreferenceStore store,
				String colorKey, String styleKey) {
			this.store = store;
			this.colorKey = colorKey;
			this.styleKey = styleKey;

			Label label = new Label(composite, SWT.NONE);
			label.setText(text);

			color = new ColorSelector(composite);
			color.addListener(new ColorListener());
			backupColor = PreferenceConverter.getColor(store, colorKey);
			setInputColor(backupColor);

			check = new Button(composite, SWT.CHECK | SWT.CENTER);
			check.addSelectionListener(new CheckListener());
			backupStyle = store.getInt(styleKey);
			setInputStyle(backupStyle);
		}

		protected void setInputColor(RGB rgb) {
			color.setColorValue(rgb);
		}

		protected RGB getInputColor() {
			return color.getColorValue();
		}

		protected void setInputStyle(int style) {
			check.setSelection((style & SWT.BOLD) != 0);
		}

		protected int getInputStyle() {
			int style = SWT.NORMAL;
			if (check.getSelection()) {
				style |= SWT.BOLD;
			}
			return style;
		}

		protected class ColorListener implements IPropertyChangeListener {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				RGB rgb = (RGB) event.getNewValue();
				PreferenceConverter.setValue(store, colorKey, rgb);
			}
		}

		protected class CheckListener implements SelectionListener {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int style = getInputStyle();
				store.setValue(styleKey, style);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				store.setValue(styleKey, SWT.NORMAL);
			}
		}

		public void performDefault() {
			RGB rgb = PreferenceConverter.getDefaultColor(store, colorKey);
			if (!rgb.equals(getInputColor())) {
				PreferenceConverter.setValue(store, colorKey, rgb);
				setInputColor(rgb);
			}

			int style = store.getDefaultInt(styleKey);
			if (style != getInputStyle()) {
				store.setValue(styleKey, style);
				setInputStyle(style);
			}
		}

		public void performApply() {
			backupColor = getInputColor();
			backupStyle = getInputStyle();
		}

		public void performCancel() {
			if (!backupColor.equals(getInputColor())) {
				PreferenceConverter.setValue(store, colorKey, backupColor);
			}

			if (backupStyle != getInputStyle()) {
				store.setValue(styleKey, backupStyle);
			}
		}
	}
}
