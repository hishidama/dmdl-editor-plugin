package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.property;

import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.PARSER_JAR_CHECKED;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.PARSER_JAR_FILES;

import java.util.HashMap;
import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.DMDLEditorPreferenceInitializer;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.PomXmlUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.IPreferenceStore;

public class DMDLPropertyUtil {

	public static String getValue(IProject project, String key) {
		try {
			String value = project.getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, key));
			if (value != null) {
				return value;
			}
			return getDefaultValue(project, key);
		} catch (CoreException e) {
			ILog log = Activator.getDefault().getLog();
			log.log(e.getStatus());
			return null;
		}
	}

	private static Map<String, String> defaultBuf = new HashMap<String, String>();

	public static String getDefaultValue(IProject project, String key) {
		if (PARSER_JAR_FILES.equals(key) || PARSER_JAR_CHECKED.equals(key)) {
			String bufKey = defaultBufKey(project, key);
			String value = defaultBuf.get(bufKey);
			if (value != null) {
				return value;
			}

			String pom = PomXmlUtil.getValue(project);
			String version = PomXmlUtil.getValue(pom, "asakusafw.version");
			if (version != null) {
				boolean d = PomXmlUtil.exists(pom, "artifactId", "asakusa-directio-dmdl", "asakusa-sdk-directio");
				boolean w = PomXmlUtil.exists(pom, "artifactId", "asakusa-windgate-dmdl", "asakusa-sdk-windgate");
				boolean t = PomXmlUtil.exists(pom, "artifactId", "asakusa-thundergate-dmdl", "asakusa-sdk-thundergate");
				String[] jars = DMDLEditorPreferenceInitializer.getJars(version, d, w, t);
				defaultBuf.put(defaultBufKey(project, PARSER_JAR_FILES), jars[0]);
				defaultBuf.put(defaultBufKey(project, PARSER_JAR_CHECKED), jars[1]);
				value = defaultBuf.get(bufKey);
				if (value != null) {
					return value;
				}
			}
		}

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getString(key);
	}

	private static String defaultBufKey(IProject project, String key) {
		return project.getName() + "#" + key;
	}

	public static void setValue(IProject project, String key, String value) {
		try {
			project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, key), value);
		} catch (CoreException e) {
			ILog log = Activator.getDefault().getLog();
			log.log(e.getStatus());
		}
	}
}
