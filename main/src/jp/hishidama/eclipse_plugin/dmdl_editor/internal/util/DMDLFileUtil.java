package jp.hishidama.eclipse_plugin.dmdl_editor.internal.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.DMDLMultiPageEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.DMDLSimpleParser;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.DocumentScanner;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelList;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class DMDLFileUtil {

	public static List<IFile> getDmdlFiles(IContainer folder) {
		List<IFile> list = new ArrayList<IFile>();
		try {
			getDmdlFiles(folder, list);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return list;
	}

	private static void getDmdlFiles(IContainer folder, Collection<IFile> list) throws CoreException {
		for (IResource r : folder.members()) {
			if (r instanceof IFile) {
				IFile file = (IFile) r;
				if ("dmdl".equals(file.getFileExtension())) {
					list.add(file);
				}
			} else if (r instanceof IFolder) {
				getDmdlFiles((IFolder) r, list);
			}
		}
	}

	public static List<IFile> getSelectionDmdlFiles() {
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Set<IFile> set = new HashSet<IFile>();
			for (Iterator<?> i = ss.iterator(); i.hasNext();) {
				Object obj = i.next();
				if (obj instanceof IJavaElement) {
					IJavaElement java = (IJavaElement) obj;
					obj = java.getResource();
				}
				if (obj instanceof IFile) {
					IFile file = (IFile) obj;
					if ("dmdl".equals(file.getFileExtension())) {
						set.add(file);
					}
				} else if (obj instanceof IContainer) {
					try {
						getDmdlFiles((IContainer) obj, set);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
			List<IFile> list = new ArrayList<IFile>(set);
			sort(list);
			return list;
		}

		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editor != null) {
			IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFileEditorInput finput = (IFileEditorInput) input;
				IFile file = finput.getFile();
				if ("dmdl".equals(file.getFileExtension())) {
					return Arrays.asList(file);
				}
			}
		}

		return Collections.emptyList();
	}

	private static void sort(List<IFile> list) {
		Collections.sort(list, new Comparator<IFile>() {
			@Override
			public int compare(IFile o1, IFile o2) {
				String s1 = o1.getFullPath().toPortableString();
				String s2 = o2.getFullPath().toPortableString();
				return s1.compareTo(s2);
			}
		});
	}

	public static DocumentManager getDocument(IFile file) {
		IPath path = file.getFullPath();
		return getDocument(path);
	}

	public static DocumentManager getDocument(IPath path) {
		try {
			return new DocumentManager(path);
		} catch (CoreException e) {
			ILog log = Activator.getDefault().getLog();
			log.log(e.getStatus());
			return null;
		}
	}

	public static class DocumentManager implements Closeable {
		private IPath path;
		private ITextFileBuffer buffer;

		DocumentManager(IPath path) throws CoreException {
			this.path = path;
			ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
			manager.connect(path, LocationKind.IFILE, null);
			buffer = manager.getTextFileBuffer(path, LocationKind.IFILE);
		}

		public IDocument getDocument() {
			return buffer.getDocument();
		}

		public void commit() throws IOException {
			try {
				buffer.commit(null, true);
			} catch (CoreException e) {
				throw new IOException(e);
			}
		}

		@Override
		public void close() throws IOException {
			try {
				ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
				manager.disconnect(path, LocationKind.IFILE, null);
			} catch (CoreException e) {
				throw new IOException(e);
			}
		}
	}

	public static ModelList getModels(IFile file) throws IOException {
		DocumentManager dm = getDocument(file);
		try {
			IDocument doc = dm.getDocument();
			DocumentScanner scanner = new DocumentScanner(doc);
			DMDLSimpleParser parser = new DMDLSimpleParser();
			return parser.parse(scanner);
		} finally {
			dm.close();
		}
	}

	public static DMDLMultiPageEditor openEditor(IFile file) {
		if (file.exists()) {
			try {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IEditorPart part = IDE.openEditor(page, file);
				if (part instanceof DMDLMultiPageEditor) {
					DMDLMultiPageEditor editor = (DMDLMultiPageEditor) part;
					editor.refresh();
					return editor;
				}
				return null;
			} catch (Exception e) {
				// fall through
			}
		}
		return null;
	}
}
