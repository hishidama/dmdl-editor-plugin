package jp.hishidama.eclipse_plugin.dmdl_editor.editors.preference;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import static jp.hishidama.eclipse_plugin.dmdl_editor.editors.preference.PreferenceConst.*;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

public class DMDLEditorPreferenceInitializer extends
		AbstractPreferenceInitializer {

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

		// Parser
		store.setDefault(PARSER_BUILD_PROPERTIES, "build.properties");
		String jars = "M2_REPO/com/asakusafw/asakusa-dmdl-core/0.4.0/asakusa-dmdl-core-0.4.0.jar,"
				+ "M2_REPO/com/asakusafw/collections/0.4.0/collections-0.4.0.jar,"
				+ "M2_REPO/com/asakusafw/simple-graph/0.4.0/simple-graph-0.4.0.jar,"
				+ "M2_REPO/org/slf4j/slf4j-api/1.6.6/slf4j-api-1.6.6.jar,"
				+ "M2_REPO/com/asakusafw/asakusa-directio-dmdl/0.4.0/asakusa-directio-dmdl-0.4.0.jar,"
				+ "M2_REPO/com/asakusafw/asakusa-windgate-dmdl/0.4.0/asakusa-windgate-dmdl-0.4.0.jar,"
				+ "M2_REPO/com/asakusafw/asakusa-thundergate-dmdl/0.4.0/asakusa-thundergate-dmdl-0.4.0.jar,";
		store.setDefault(PARSER_JAR_FILES, jars);
		store.setDefault(PARSER_JAR_CHECKED,
				"true,true,true,true,true,true,false,");

		// Hyperlink
		store.setDefault(
				AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED,
				true);
	}
}
