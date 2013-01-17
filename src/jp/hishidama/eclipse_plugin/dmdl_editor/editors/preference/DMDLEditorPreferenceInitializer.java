package jp.hishidama.eclipse_plugin.dmdl_editor.editors.preference;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import static jp.hishidama.eclipse_plugin.dmdl_editor.editors.preference.PreferenceConst.*;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public class DMDLEditorPreferenceInitializer extends
		AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		String red = StringConverter.asString(new RGB(192, 0, 0));
		String green = StringConverter.asString(new RGB(0, 192, 0));
		String blue = StringConverter.asString(new RGB(0, 0, 192));

		store.setDefault(COLOR_COMMENT, green);
		store.setDefault(STYLE_COMMENT, SWT.NORMAL);

		store.setDefault(COLOR_ANNOTATION, red);
		store.setDefault(STYLE_ANNOTATION, SWT.BOLD);

		store.setDefault(COLOR_DESCRIPTION, blue);
		store.setDefault(STYLE_DESCRIPTION, SWT.NORMAL);

		store.setDefault(COLOR_MODEL_TYPE, red);
		store.setDefault(STYLE_MODEL_TYPE, SWT.BOLD);

		store.setDefault(COLOR_DATA_TYPE, red);
		store.setDefault(STYLE_DATA_TYPE, SWT.NORMAL);

		store.setDefault(COLOR_SUM_TYPE, red);
		store.setDefault(STYLE_SUM_TYPE, SWT.NORMAL);
	}
}
