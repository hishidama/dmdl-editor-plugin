package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference;

import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.COLOR_ANNOTATION;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.COLOR_COMMENT;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.COLOR_DATA_TYPE;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.COLOR_DESCRIPTION;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.COLOR_MODEL_TYPE;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.COLOR_SUM_TYPE;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.FORMAT_INDENT_ARGUMENT;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.FORMAT_INDENT_PROPERTY;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.STYLE_ANNOTATION;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.STYLE_COMMENT;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.STYLE_DATA_TYPE;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.STYLE_DESCRIPTION;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.STYLE_MODEL_TYPE;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.STYLE_SUM_TYPE;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

public class DMDLEditorPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		// Color
		String red = StringConverter.asString(new RGB(192, 0, 0));
		String green = StringConverter.asString(new RGB(0, 192, 0));
		String blue = StringConverter.asString(new RGB(0, 0, 192));
		String purple = StringConverter.asString(new RGB(128, 0, 128));

		store.setDefault(COLOR_COMMENT, green);
		store.setDefault(STYLE_COMMENT, SWT.NORMAL);

		store.setDefault(COLOR_ANNOTATION, purple);
		store.setDefault(STYLE_ANNOTATION, SWT.BOLD);

		store.setDefault(COLOR_DESCRIPTION, blue);
		store.setDefault(STYLE_DESCRIPTION, SWT.NORMAL);

		store.setDefault(COLOR_MODEL_TYPE, red);
		store.setDefault(STYLE_MODEL_TYPE, SWT.BOLD);

		store.setDefault(COLOR_DATA_TYPE, red);
		store.setDefault(STYLE_DATA_TYPE, SWT.NORMAL);

		store.setDefault(COLOR_SUM_TYPE, red);
		store.setDefault(STYLE_SUM_TYPE, SWT.NORMAL);

		// Formatter
		store.setDefault(FORMAT_INDENT_ARGUMENT, 2);
		store.setDefault(FORMAT_INDENT_PROPERTY, 4);

		// Hyperlink
		store.setDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED, true);
	}
}
