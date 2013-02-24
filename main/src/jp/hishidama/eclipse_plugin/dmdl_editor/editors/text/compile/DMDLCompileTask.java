package jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.compile;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Properties;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.marker.DMDLErrorCheckHandler;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.marker.DMDLErrorCheckTask;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.marker.ParserClassUtil;
import jp.hishidama.eclipse_plugin.util.FileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class DMDLCompileTask implements IWorkspaceRunnable {
	private IProject project;
	private String source;
	private String output;

	public DMDLCompileTask(IProject project) {
		this.project = project;
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("DMDL Compile", 100);
		try {
			String arguments = createArguments(new SubProgressMonitor(monitor,
					10));
			if (arguments == null) {
				return;
			}
			ILaunchConfiguration config = createConfiguration(
					new SubProgressMonitor(monitor, 10), arguments);
			launch(new SubProgressMonitor(monitor, 60), config, false);
			install(new SubProgressMonitor(monitor, 10));
			mark(new SubProgressMonitor(monitor, 10));
			return;
		} finally {
			monitor.done();
		}
	}

	private String createArguments(IProgressMonitor monitor)
			throws CoreException {
		assert monitor != null;
		monitor.beginTask("create arguments", 5);
		try {
			monitor.subTask("create arguments");
			checkCancel(monitor);

			Properties properties = ParserClassUtil.getBuildProperties(project);
			if (properties == null) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						String file = ParserClassUtil
								.getBuildPropertiesFileName(project);
						MessageDialog
								.openWarning(
										null,
										"DMDL compile",
										MessageFormat
												.format("プロジェクト内にAsakusa Frameworkのbuild.propertiesが見つからない為、DMDLをコンパイルできません。\n"
														+ "プロジェクトのプロパティーでbuild.propertiesの場所を指定して下さい。\n\n"
														+ "現在指定されているパス={0}",
														file));
					}
				});
				return null;
			}

			monitor.worked(1);
			StringBuilder sb = new StringBuilder(256);
			{
				String value = getValue(properties, "asakusa.dmdl.dir");
				this.source = value;
				IFile file = FileUtil.getFile(project, value);
				append(sb, "-source", FileUtil.getLocation(file), true);
				monitor.worked(1);
			}
			{
				String value = getValue(properties, "asakusa.modelgen.output");
				this.output = value;
				IFile file = FileUtil.getFile(project, value);
				append(sb, "-output", FileUtil.getLocation(file), true);
				monitor.worked(1);
			}
			{
				String value = getValue(properties, "asakusa.modelgen.package");
				append(sb, "-package", value, false);
				monitor.worked(1);
			}
			{
				String charset = properties
						.getProperty("asakusa.dmdl.encoding");
				if (charset == null) {
					charset = project.getDefaultCharset(true);
					if (charset == null) {
						charset = Charset.defaultCharset().name();
					}
				}
				append(sb, "-sourceencoding", charset, false);
				append(sb, "-targetencoding", charset, false);
				monitor.worked(1);
			}
			return sb.toString();
		} finally {
			monitor.done();
		}
	}

	private String getValue(Properties properties, String key)
			throws CoreException {
		String value = properties.getProperty(key);
		if (value == null) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					MessageFormat.format(
							"not found property of build.properties. key={0}",
							key));
			throw new CoreException(status);
		}
		return value;
	}

	private void append(StringBuilder sb, String name, String value,
			boolean quote) {
		if (sb.length() != 0) {
			sb.append(" ");
		}
		sb.append(name);
		sb.append(" ");

		boolean space = value.indexOf(' ') >= 0;
		if (quote && space) {
			sb.append('\"');
		}
		sb.append(value);
		if (quote && space) {
			sb.append('\"');
		}
	}

	private ILaunchConfigurationWorkingCopy createConfiguration(
			IProgressMonitor monitor, String programArguments)
			throws CoreException {
		monitor.beginTask("create configuration", 1);
		try {
			monitor.subTask("create configuration");
			checkCancel(monitor);

			String mainClassName = "com.asakusafw.dmdl.java.Main";
			String classpathProviderId = DmdlClasspathProvider.ID;
			IPath workingDirectory = project.getLocation();
			String vmArguments = "";

			ILaunchManager manager = DebugPlugin.getDefault()
					.getLaunchManager();
			ILaunchConfigurationType type = manager
					.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
			ILaunchConfigurationWorkingCopy config = type.newInstance(null,
					Activator.PLUGIN_ID);
			config.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH,
					true);
			config.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_ALLOW_TERMINATE,
					true);
			config.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER,
					classpathProviderId);
			config.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
					mainClassName);
			config.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
					vmArguments);
			config.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
					programArguments);
			config.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					project.getName());
			config.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
					workingDirectory.toOSString());
			return config;
		} finally {
			monitor.done();
		}
	}

	private void launch(IProgressMonitor monitor, ILaunchConfiguration config,
			boolean debugMode) throws CoreException {
		monitor.beginTask("launch", 100);
		try {
			monitor.subTask("launch DMDL compiler");
			checkCancel(monitor);

			String mode = debugMode ? ILaunchManager.DEBUG_MODE
					: ILaunchManager.RUN_MODE;
			boolean build = false;
			boolean register = false;
			ILaunch launch = config.launch(mode, new SubProgressMonitor(
					monitor, 20), build, register);

			monitor.worked(10);
			while (!launch.isTerminated()) {
				checkCancel(monitor);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					launch.terminate();
					throw new OperationCanceledException();
				}
				monitor.worked(1);
			}
			launch.terminate();
		} finally {
			monitor.done();
		}
	}

	private void install(IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("refresh", 100);
		try {
			checkCancel(monitor);
			project.refreshLocal(IResource.DEPTH_INFINITE,
					new SubProgressMonitor(monitor, 20));
			if (project.findMember(output) == null) {
				return;
			}

			checkCancel(monitor);
			IJavaProject javaProject = JavaCore.create(project);
			if (!isInstalled(javaProject)) {
				IClasspathEntry[] classpath = javaProject.getRawClasspath();
				IClasspathEntry[] newClasspath = Arrays.copyOf(classpath,
						classpath.length + 1);
				newClasspath[classpath.length] = JavaCore
						.newSourceEntry(getAbsolutePath(output));
				javaProject.setRawClasspath(newClasspath,
						new SubProgressMonitor(monitor, 20));
				javaProject.getProject().build(
						IncrementalProjectBuilder.FULL_BUILD,
						new SubProgressMonitor(monitor, 30));
			}
		} finally {
			monitor.done();
		}
	}

	private boolean isInstalled(IJavaProject project) throws JavaModelException {
		IPath sourcePath = getAbsolutePath(output);
		for (IClasspathEntry entry : project.getRawClasspath()) {
			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE
					&& entry.getPath().equals(sourcePath)) {
				return true;
			}
		}
		return false;
	}

	private void mark(IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("create mark", 1);
		try {
			checkCancel(monitor);

			IFolder folder = FileUtil.getFolder(project, source);
			DMDLErrorCheckHandler handler = new DMDLErrorCheckHandler();
			DMDLErrorCheckTask task = handler.createTask(folder);
			task.run(new SubProgressMonitor(monitor, 1));
		} catch (InvocationTargetException e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"create mark error", e.getCause());
			throw new CoreException(status);
		} catch (InterruptedException e) {
			throw new OperationCanceledException();
		} finally {
			monitor.done();
		}
	}

	private IPath getAbsolutePath(String path) {
		IPath projectPath = project.getFullPath();
		return projectPath.append(path);
	}

	private void checkCancel(IProgressMonitor monitor)
			throws OperationCanceledException {
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
	}
}
