package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension;

import java.util.Properties;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DmdlCompilerProperties;

public class BuildProperties extends DmdlCompilerProperties {

	private Properties properties;

	public BuildProperties(Properties properties) {
		this.properties = properties;
	}

	@Override
	public String getPackageDefault() {
		return getProperty("asakusa.package.default");
	}

	@Override
	public String getDmdlDir() {
		return getProperty("asakusa.dmdl.dir");
	}

	@Override
	public String getModelgenOutput() {
		return getProperty("asakusa.modelgen.output");
	}

	@Override
	public String getModelgenPackage() {
		return getProperty("asakusa.modelgen.package");
	}

	@Override
	public String getDmdlEncoding() {
		return getProperty("asakusa.dmdl.encoding");
	}

	private String getProperty(String key) {
		if (properties == null) {
			return null;
		}
		return properties.getProperty(key);
	}
}
