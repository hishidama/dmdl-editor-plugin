package jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class DMDLErrorCheckHandler extends AbstractHandler {
	protected DMDLErrorMarkerCreator marker = new DMDLErrorMarkerCreator();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		FileList list = new FileList();

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TextSelection) {
			DMDLEditor editor = (DMDLEditor) HandlerUtil.getActiveEditor(event);
			if (editor != null) {
				IFile file = editor.getFile();
				search(list, file);
			}
		} else if (selection instanceof TreeSelection) {
			TreeSelection tree = (TreeSelection) selection;
			TreePath[] paths = tree.getPaths();
			for (TreePath path : paths) {
				Object segment = path.getLastSegment();
				if (segment instanceof IFile) {
					search(list, (IFile) segment);
				} else if (segment instanceof IFolder) {
					search(list, (IFolder) segment);
				}
			}
		}

		execute(list);
		return null;
	}

	public void execute(IFile file) {
		FileList list = new FileList();
		search(list, file);
		execute(list);
	}

	private void execute(FileList projects) {
		for (List<IFile> list : projects.values()) {
			marker.parse(list);
		}
	}

	private void search(FileList list, IFile file) {
		IPath path = file.getFullPath();
		IPath parent = path.removeLastSegments(1);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFolder folder = root.getFolder(parent);
		search(list, folder);
	}

	private void search(FileList list, IFolder folder) {
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

	static class FileList {
		private Map<String, List<IFile>> map = new HashMap<String, List<IFile>>();

		public void add(IFile file) {
			String name = file.getProject().getName();
			List<IFile> list = map.get(name);
			if (list == null) {
				list = new ArrayList<IFile>();
				map.put(name, list);
			}
			list.add(file);
		}

		public Collection<List<IFile>> values() {
			return map.values();
		}
	}
}
