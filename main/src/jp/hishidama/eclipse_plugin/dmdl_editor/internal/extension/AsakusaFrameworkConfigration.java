package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension;

import java.io.IOException;
import java.util.Properties;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLEditorConfiguration;
import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DmdlCompilerProperties;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.PomXmlUtil;
import jp.hishidama.eclipse_plugin.util.FileUtil;

import org.eclipse.core.resources.IProject;

public abstract class AsakusaFrameworkConfigration extends DMDLEditorConfiguration {

	@Override
	public boolean acceptable(IProject project) {
		String pom = PomXmlUtil.getValue(project);
		String version = PomXmlUtil.getAsakusaFrameworkVersion(pom);
		if (version == null) {
			return false;
		}
		return version.startsWith(getVersionPrefix());
	}

	protected abstract String getVersionPrefix();

	@Override
	public String getDefaultBuildPropertiesPath() {
		return "build.properties";
	}

	@Override
	public DmdlCompilerProperties getCompilerProperties(IProject project, String propertyFilePath) throws IOException {
		Properties p = FileUtil.loadProperties(project, propertyFilePath);
		return new BuildProperties(p);
	}
}
