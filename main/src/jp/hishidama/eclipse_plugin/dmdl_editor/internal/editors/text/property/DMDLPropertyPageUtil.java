package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.property;

import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.CONFIGURATION_NAME;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.PARSER_BUILD_PROPERTIES;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.PARSER_JAR_FILE;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.PARSER_JAR_FILE_COUNT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLEditorConfiguration;
import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLEditorConfiguration.Library;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.IPreferenceStore;

public class DMDLPropertyPageUtil {

	public static void setConfigurationName(IProject project, String cname) {
		setValue(project, CONFIGURATION_NAME, cname);
	}

	public static String getConfigurationName(IProject project) {
		initializeProjectParser(project);

		String cname = getValue(project, CONFIGURATION_NAME);
		return cname;
	}

	public static void setLibraries(IProject project, List<Library> list) {
		setIntValue(project, PARSER_JAR_FILE_COUNT, list.size());

		int i = 0;
		for (Library lib : list) {
			setValue(project, PARSER_JAR_FILE + i, lib.toString());
			i++;
		}
	}

	public static List<Library> getLibraries(IProject project) {
		initializeProjectParser(project);

		List<Library> libs = new ArrayList<Library>();

		Integer sizeObject = getIntValue(project, PARSER_JAR_FILE_COUNT);
		int size = (sizeObject != null) ? sizeObject : 0;
		for (int i = 0; i < size; i++) {
			String lib = getValue(project, PARSER_JAR_FILE + i);
			if (lib != null) {
				libs.add(Library.valudOf(lib));
			}
		}
		return libs;
	}

	public static void initializeProjectParser(IProject project) {
		final String key = "ParserClassPath.initialize";
		{
			String s = getValue(project, key);
			if (StringUtil.nonEmpty(s)) {
				return;
			}
		}

		DMDLEditorConfiguration c = null;
		{
			String cname = getValue(project, CONFIGURATION_NAME);
			if (StringUtil.isEmpty(cname)) {
				c = getDefaultConfiguration(project);
				if (c != null) {
					try {
						cname = c.getConfigurationName();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (cname == null) {
					cname = "";
				}
				setConfigurationName(project, cname);
			} else {
				c = getConfiguration(cname);
			}
		}

		String bpath = null;
		List<Library> libs = null;
		if (c != null) {
			try {
				bpath = c.getDefaultBuildPropertiesPath();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				libs = c.getDefaultLibraries(project);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (bpath == null) {
			bpath = "";
		}
		setValue(project, PARSER_BUILD_PROPERTIES, bpath);

		if (libs == null) {
			libs = Collections.emptyList();
		}
		setLibraries(project, libs);

		setValue(project, key, "initialized");
	}

	public static String getValue(IProject project, String key) {
		try {
			String value = project.getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, key));
			if (value != null) {
				return value;
			}
			return getDefaultValue(project, key);
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(e.getStatus());
			return null;
		}
	}

	private static Integer getIntValue(IProject project, String key) {
		try {
			String value = project.getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, key));
			if (value != null) {
				return Integer.valueOf(value);
			}
			return getDefaultIntValue(project, key);
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(e.getStatus());
			return null;
		}
	}

	private static String getDefaultValue(IProject project, String key) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getString(key);
	}

	private static Integer getDefaultIntValue(IProject project, String key) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getInt(key);
	}

	public static void setValue(IProject project, String key, String value) {
		try {
			project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, key), value);
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(e.getStatus());
		}
	}

	private static void setIntValue(IProject project, String key, int value) {
		try {
			project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, key), Integer.toString(value));
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(e.getStatus());
		}
	}

	public static DMDLEditorConfiguration getConfiguration(IProject project) {
		String cname = getConfigurationName(project);
		return getConfiguration(cname);
	}

	private static DMDLEditorConfiguration getConfiguration(String cname) {
		if (cname == null) {
			return null;
		}
		List<DMDLEditorConfiguration> list = Activator.getExtensionLoader().getConfigurations();
		for (DMDLEditorConfiguration c : list) {
			if (cname.equals(c.getConfigurationName())) {
				return c;
			}
		}
		return null;
	}

	public static DMDLEditorConfiguration getDefaultConfiguration(IProject project) {
		for (DMDLEditorConfiguration c : Activator.getExtensionLoader().getConfigurations()) {
			if (c.acceptable(project)) {
				return c;
			}
		}
		return null;
	}

	public static List<Library> getDefaultLibraries(IProject project) {
		DMDLEditorConfiguration c = getConfiguration(project);
		if (c == null) {
			return Collections.emptyList();
		}
		try {
			List<Library> list = c.getDefaultLibraries(project);
			if (list == null) {
				return Collections.emptyList();
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
}
