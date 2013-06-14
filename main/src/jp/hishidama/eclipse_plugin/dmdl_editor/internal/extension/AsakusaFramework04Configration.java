package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLEditorConfiguration;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.PomXmlUtil;

import org.eclipse.core.resources.IProject;

public class AsakusaFramework04Configration extends AsakusaFrameworkConfigration {

	@Override
	public String getConfigurationName() {
		return "Asakusa Framework 0.4";
	}

	@Override
	protected String getVersionPrefix() {
		return "0.4.";
	}

	@Override
	public List<Library> getDefaultLibraries(IProject project) {
		String pom = PomXmlUtil.getValue(project);
		String version = PomXmlUtil.getAsakusaFrameworkVersion(pom);
		if (version == null) {
			version = "0.4.0";
		}
		boolean d = PomXmlUtil.exists(pom, "artifactId", "asakusa-directio-dmdl", "asakusa-sdk-directio");
		boolean w = PomXmlUtil.exists(pom, "artifactId", "asakusa-windgate-dmdl", "asakusa-sdk-windgate");
		boolean t = PomXmlUtil.exists(pom, "artifactId", "asakusa-thundergate-dmdl", "asakusa-sdk-thundergate");

		List<Library> list = new ArrayList<DMDLEditorConfiguration.Library>();
		addLib(list, "M2_REPO/com/asakusafw/asakusa-dmdl-core/${version}/asakusa-dmdl-core-${version}.jar", true,
				version);
		addLib(list, "M2_REPO/com/asakusafw/collections/${version}/collections-${version}.jar", true, version);
		addLib(list, "M2_REPO/com/asakusafw/simple-graph/${version}/simple-graph-${version}.jar", true, version);
		addLib(list, "M2_REPO/org/slf4j/slf4j-api/1.6.6/slf4j-api-1.6.6.jar", true, version);
		addLib(list, "M2_REPO/com/asakusafw/asakusa-directio-dmdl/${version}/asakusa-directio-dmdl-${version}.jar", d,
				version);
		addLib(list, "M2_REPO/com/asakusafw/asakusa-windgate-dmdl/${version}/asakusa-windgate-dmdl-${version}.jar", w,
				version);
		addLib(list,
				"M2_REPO/com/asakusafw/asakusa-thundergate-dmdl/${version}/asakusa-thundergate-dmdl-${version}.jar", t,
				version);
		return list;
	}

	private void addLib(List<Library> list, String path, boolean checked, String version) {
		String npath = path.replaceAll("\\$\\{version\\}", version);
		Library lib = new Library(npath, checked);
		list.add(lib);
	}
}
