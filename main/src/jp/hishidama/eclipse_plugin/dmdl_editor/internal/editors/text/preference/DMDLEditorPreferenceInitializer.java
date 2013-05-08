package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference;

import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.COLOR_ANNOTATION;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.COLOR_COMMENT;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.COLOR_DATA_TYPE;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.COLOR_DESCRIPTION;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.COLOR_MODEL_TYPE;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.COLOR_SUM_TYPE;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.FORMAT_INDENT_ARGUMENT;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.FORMAT_INDENT_PROPERTY;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.PARSER_BUILD_PROPERTIES;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.PARSER_JAR_CHECKED;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.PARSER_JAR_FILES;
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

		// Parser
		store.setDefault(PARSER_BUILD_PROPERTIES, "build.properties");
		String[] jars = getJars("0.4.0");
		store.setDefault(PARSER_JAR_FILES, jars[0]);
		store.setDefault(PARSER_JAR_CHECKED, jars[1]);

		// Hyperlink
		store.setDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED, true);
	}

	public static String[] getJars(String version) {
		StringBuilder jars = new StringBuilder(96 * 7);
		StringBuilder checked = new StringBuilder(8 * 7);
		addJar(jars, checked, "M2_REPO/com/asakusafw/asakusa-dmdl-core/${version}/asakusa-dmdl-core-${version}.jar",
				true, version);
		addJar(jars, checked, "M2_REPO/com/asakusafw/collections/${version}/collections-${version}.jar", true, version);
		addJar(jars, checked, "M2_REPO/com/asakusafw/simple-graph/${version}/simple-graph-${version}.jar", true,
				version);
		addJar(jars, checked, "M2_REPO/org/slf4j/slf4j-api/1.6.6/slf4j-api-1.6.6.jar", true, version);
		addJar(jars, checked,
				"M2_REPO/com/asakusafw/asakusa-directio-dmdl/${version}/asakusa-directio-dmdl-${version}.jar", true,
				version);
		addJar(jars, checked,
				"M2_REPO/com/asakusafw/asakusa-windgate-dmdl/${version}/asakusa-windgate-dmdl-${version}.jar", true,
				version);
		addJar(jars, checked,
				"M2_REPO/com/asakusafw/asakusa-thundergate-dmdl/${version}/asakusa-thundergate-dmdl-${version}.jar",
				false, version);
		return new String[] { jars.toString(), checked.toString() };
	}

	private static void addJar(StringBuilder jars, StringBuilder checked, String jar, boolean check, String version) {
		jars.append(jar.replaceAll("\\$\\{version\\}", version));
		jars.append(",");
		checked.append(check);
		checked.append(",");
	}
}
