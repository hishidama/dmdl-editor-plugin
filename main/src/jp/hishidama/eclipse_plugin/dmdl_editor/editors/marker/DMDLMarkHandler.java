package jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;

public class DMDLMarkHandler extends AbstractHandler {
	protected DMDLMarker marker = new DMDLMarker();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		List<IFile> list = new ArrayList<IFile>();

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TextSelection) {
			DMDLEditor editor = (DMDLEditor) HandlerUtil.getActiveEditor(event);
			if (editor != null) {
				IFileEditorInput input = (IFileEditorInput) editor
						.getEditorInput();
				list.add(input.getFile());
			}
		} else if (selection instanceof TreeSelection) {
			TreeSelection tree = (TreeSelection) selection;
			TreePath[] paths = tree.getPaths();
			for (TreePath path : paths) {
				Object segment = path.getLastSegment();
				if (segment instanceof IFile) {
					list.add((IFile) segment);
				} else if (segment instanceof IFolder) {
					search(list, (IFolder) segment);
				}
			}
		}

		if (!list.isEmpty()) {
			marker.parse(list);
		}
		return null;
	}

	protected void search(List<IFile> list, IFolder folder) {
		IResource[] rs;
		try {
			rs = folder.members();
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		for (IResource r : rs) {
			if (r instanceof IFile) {
				String ext = r.getFileExtension();
				if ("dmdl".equalsIgnoreCase(ext)) {
					list.add((IFile) r);
				}
			} else if (r instanceof IFolder) {
				search(list, (IFolder) r);
			}
		}
	}
}
