package jp.hishidama.eclipse_plugin.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

public class FileUtil {

	public static IFile getFile(IProject project, String path) {
		IPath ap = project.getFullPath().append(path);
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		return workspaceRoot.getFile(ap);
	}

	public static IFolder getFolder(IProject project, String path) {
		IPath ap = project.getFullPath().append(path);
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		return workspaceRoot.getFolder(ap);
	}
}
