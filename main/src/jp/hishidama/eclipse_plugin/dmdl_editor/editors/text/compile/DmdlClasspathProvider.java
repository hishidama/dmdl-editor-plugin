package jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.compile;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.marker.ParserClassUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.StandardClasspathProvider;

public class DmdlClasspathProvider extends StandardClasspathProvider {

	public static final String ID = "jp.hishidama.eclipse_plugin.dmdl_editor.dmdlClasspathProvider";

	@Override
	public IRuntimeClasspathEntry[] computeUnresolvedClasspath(
			ILaunchConfiguration configuration) throws CoreException {
		List<IRuntimeClasspathEntry> results = new ArrayList<IRuntimeClasspathEntry>();
		Collections.addAll(results,
				super.computeUnresolvedClasspath(configuration));
		getDmdlCompilerLibraries(results, configuration);
		IRuntimeClasspathEntry[] r = results
				.toArray(new IRuntimeClasspathEntry[results.size()]);
		return r;
	}

	private void getDmdlCompilerLibraries(List<IRuntimeClasspathEntry> results,
			ILaunchConfiguration configuration) throws CoreException {
		IJavaProject javaProject = JavaRuntime.getJavaProject(configuration);
		IProject project = javaProject.getProject();

		List<URL> list = new ArrayList<URL>();
		ParserClassUtil.getClassPath(list, project);

		for (URL url : list) {
			IPath path = new Path(url.getPath());
			results.add(JavaRuntime.newArchiveRuntimeClasspathEntry(path));
		}
	}
}
