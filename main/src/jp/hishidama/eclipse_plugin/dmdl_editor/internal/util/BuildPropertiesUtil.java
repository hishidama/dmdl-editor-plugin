package jp.hishidama.eclipse_plugin.dmdl_editor.internal.util;

import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.PARSER_BUILD_PROPERTIES;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.Properties;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.marker.ParserClassUtil;
import jp.hishidama.eclipse_plugin.util.FileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;

public class BuildPropertiesUtil {

	public static String getBuildPropertiesFileName(IProject project) {
		return ParserClassUtil.getValue(project, PARSER_BUILD_PROPERTIES);
	}

	public static Properties getBuildProperties(IProject project) {
		String s = getBuildPropertiesFileName(project);
		if (s == null) {
			return null;
		}

		IFile file = FileUtil.getFile(project, s);
		if (file == null) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID, MessageFormat.format(
					"not found Asakusa Framework build properties. file={0}", s)));
			return null;
		}

		InputStream is = null;
		Reader reader = null;
		try {
			is = file.getContents();
			String cs;
			try {
				cs = file.getCharset();
			} catch (Exception e) {
				cs = "UTF-8";
			}
			reader = new InputStreamReader(is, cs);
			Properties p = new Properties();
			p.load(reader);
			return p;
		} catch (CoreException e) {
			ILog log = Activator.getDefault().getLog();
			log.log(e.getStatus());
			return null;
		} catch (IOException e) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID, MessageFormat.format(
					"build.properties read error. file={0}", s), e));
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getPackageDefault(Properties properties) {
		return getProperty(properties, "asakusa.package.default");
	}

	public static String getDmdlDir(Properties properties) {
		return getProperty(properties, "asakusa.dmdl.dir");
	}

	public static String getModelgenPackage(Properties properties) {
		return getProperty(properties, "asakusa.modelgen.package");
	}

	private static String getProperty(Properties properties, String key) {
		if (properties == null) {
			return null;
		}
		return properties.getProperty(key);
	}
}
