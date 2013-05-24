package jp.hishidama.eclipse_plugin.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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

	public static boolean exists(IResource file) {
		File f = new File(file.getLocationURI());
		return f.exists();
	}

	public static boolean isFile(IResource file) {
		File f = new File(file.getLocationURI());
		return f.isFile();
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

	public static StringBuilder load(IFile file) throws CoreException {
		InputStream is = file.getContents();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			try {
				StringBuilder sb = new StringBuilder(1024);
				for (;;) {
					String s = br.readLine();
					if (s == null) {
						break;
					}
					sb.append(s);
					sb.append("\n");
				}
				return sb;
			} catch (IOException e) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "read error", e);
				throw new CoreException(status);
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
