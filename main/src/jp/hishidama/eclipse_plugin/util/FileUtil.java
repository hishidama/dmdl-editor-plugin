package jp.hishidama.eclipse_plugin.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public class FileUtil {

	public static IFile getFile(IProject project, String path) {
		return project.getFile(path);
		// IPath ap = project.getFullPath().append(path);
		// IWorkspaceRoot workspaceRoot =
		// ResourcesPlugin.getWorkspace().getRoot();
		// return workspaceRoot.getFile(ap);
	}

	public static IFile getFile(IProject project, IPath path) {
		return project.getFile(path);
	}

	public static IFolder getFolder(IProject project, String path) {
		return project.getFolder(path);
		// IPath ap = project.getFullPath().append(path);
		// IWorkspaceRoot workspaceRoot =
		// ResourcesPlugin.getWorkspace().getRoot();
		// return workspaceRoot.getFolder(ap);
	}

	public static IFile getFile(IEditorPart editor) {
		if (editor != null) {
			IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				return ((IFileEditorInput) input).getFile();
			}
		}
		return null;
	}

	public static String getLocation(IFile file) {
		return file.getLocation().toOSString();
	}
}
