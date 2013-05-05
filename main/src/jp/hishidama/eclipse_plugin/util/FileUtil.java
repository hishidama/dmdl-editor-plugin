package jp.hishidama.eclipse_plugin.util;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class FileUtil {

	public static void createFolder(IProject project, IPath path) throws CoreException {
		IFolder folder = project.getFolder(path);
		if (!folder.exists()) {
			createFolder(project, path.removeLastSegments(1));
			folder.create(false, true, null);
		}
	}

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

	public static boolean openFile(IFile file, String className) {
		if (className != null) {
			IProject project = file.getProject();
			IJavaProject javaProject = JavaCore.create(project);
			if (javaProject != null) {
				try {
					IType type = javaProject.findType(className);
					JavaUI.openInEditor(type);
					return true;
				} catch (Exception e) {
					// fall through
				}
			}
		}

		return openFile(file);
	}

	public static boolean openFile(IFile file) {
		if (file.exists()) {
			try {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IDE.openEditor(page, file);
				return true;
			} catch (Exception e) {
				// fall through
			}
		}
		return false;
	}

	public static void save(IFile file, String contents) throws CoreException {
		ByteArrayInputStream is;
		try {
			is = new ByteArrayInputStream(contents.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		if (file.exists()) {
			file.setContents(is, true, false, null);
		} else {
			file.create(is, true, null);
		}
	}
}
