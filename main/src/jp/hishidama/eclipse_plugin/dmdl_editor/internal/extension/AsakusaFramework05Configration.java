package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;

public class AsakusaFramework05Configration extends AsakusaFrameworkConfigration {

	@Override
	public String getConfigurationName() {
		return "Asakusa Framework 0.5";
	}

	@Override
	protected String getVersionPrefix() {
		return "0.5.";
	}

	@Override
	public List<Library> getDefaultLibraries(IProject project) {
		return Collections.emptyList();
	}
}
