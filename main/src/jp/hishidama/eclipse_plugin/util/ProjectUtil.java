package jp.hishidama.eclipse_plugin.util;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

public class ProjectUtil {

	public static IProject getProject(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TextSelection) {
			IEditorPart editor = HandlerUtil.getActiveEditor(event);
			IFile file = FileUtil.getFile(editor);
			if (file != null) {
				return file.getProject();
			}
		} else if (selection instanceof TreeSelection) {
			TreeSelection tree = (TreeSelection) selection;
			TreePath[] paths = tree.getPaths();
			for (TreePath treePath : paths) {
				Object segment = treePath.getLastSegment();
				if (segment instanceof IResource) {
					return ((IResource) segment).getProject();
				} else if (segment instanceof IJavaElement) {
					return ((IJavaElement) segment).getJavaProject().getProject();
				} else if (segment instanceof IAdaptable) {
					IAdaptable adaptable = (IAdaptable) segment;
					return (IProject) adaptable.getAdapter(IProject.class);
				}
			}
		}
		return null;
	}
}
