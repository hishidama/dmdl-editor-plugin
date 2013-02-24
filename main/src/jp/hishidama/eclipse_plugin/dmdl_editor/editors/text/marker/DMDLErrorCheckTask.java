package jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.marker;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.DMDLSimpleParser;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.DocumentScanner;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.index.IndexContainer;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.index.ModelIndex;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;

public class DMDLErrorCheckTask implements IRunnableWithProgress {

	public static class FileList {
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

		public int getCount(boolean createIndex, boolean checkMark) {
			int n = 0;
			for (List<IFile> list : map.values()) {
				n += list.size();
			}
			int r = 0;
			if (createIndex) {
				r += n;
			}
			if (checkMark) {
				r += n + map.size();
			}
			return r;
		}
	}

	public static final QualifiedName KEY = new QualifiedName(
			Activator.PLUGIN_ID, "DMDLErrorCheckTask.parser");

	private FileList projects;
	private boolean createIndex;
	private boolean checkMark;

	public DMDLErrorCheckTask(FileList projects, boolean createIndex,
			boolean checkMark) {
		this.projects = projects;
		this.createIndex = createIndex;
		this.checkMark = checkMark;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		int totalWork = projects.getCount(createIndex, checkMark);
		String title;
		if (createIndex) {
			if (checkMark) {
				title = "DMDL create index & error check";
			} else {
				title = "DMDL create index";
			}
		} else {
			title = "DMDL error check";
		}
		monitor.beginTask(title, totalWork);
		try {
			for (List<IFile> list : projects.values()) {
				cancelCheck(monitor);
				parse(monitor, list);
			}
		} finally {
			monitor.done();
		}
	}

	protected void parse(IProgressMonitor monitor, List<IFile> files)
			throws InterruptedException {
		FileDocumentProvider provider = new FileDocumentProvider();
		if (createIndex) {
			createIndex(monitor, files, provider);
		}
		if (checkMark) {
			checkMark(monitor, files, provider);
		}
	}

	protected final IJavaProject getJavaProject(IFile file) {
		IProject project = file.getProject();
		return JavaCore.create(project);
	}

	protected void createIndex(IProgressMonitor monitor, List<IFile> files,
			FileDocumentProvider provider) throws InterruptedException {
		cancelCheck(monitor);
		IProject project = files.get(0).getProject();
		IndexContainer ic = IndexContainer.createContainer(project);

		cancelCheck(monitor);
		DMDLSimpleParser parser = new DMDLSimpleParser();
		for (IFile file : files) {
			monitor.subTask(file.getName());
			cancelCheck(monitor);
			IDocument document = getDocument(provider, file);
			DocumentScanner scanner = new DocumentScanner(document);
			ModelList models = parser.parse(scanner);
			for (ModelToken model : models.getNamedModelList()) {
				cancelCheck(monitor);
				String modelName = model.getModelName();
				if (modelName != null) {
					ModelIndex mi = ic.createModel(modelName, file, model);
					for (PropertyToken prop : model.getPropertyList()) {
						cancelCheck(monitor);
						String propName = prop.getName();
						if (propName != null) {
							mi.addProperty(propName, prop);
						}
					}
				}
			}
			monitor.worked(1);
		}
	}

	protected void checkMark(IProgressMonitor monitor, List<IFile> files,
			FileDocumentProvider provider) throws InterruptedException {
		cancelCheck(monitor);
		IJavaProject project = getJavaProject(files.get(0));
		if (project == null) {
			monitor.worked(files.size() + 1);
			return;
		}

		cancelCheck(monitor);
		DmdlParserWrapper wrapper = null;
		try {
			wrapper = (DmdlParserWrapper) project.getProject()
					.getSessionProperty(KEY);
			if (wrapper == null) {
				wrapper = new DmdlParserWrapper(project);
				project.getProject().setSessionProperty(KEY, wrapper);
			}
		} catch (CoreException e) {
			if (wrapper == null) {
				wrapper = new DmdlParserWrapper(project);
			}
		}
		if (wrapper.isValid()) {
			cancelCheck(monitor);
			Map<URI, IFile> fileMap = new HashMap<URI, IFile>();
			for (IFile file : files) {
				monitor.subTask(file.getName());
				cancelCheck(monitor);
				try {
					file.deleteMarkers(IMarker.PROBLEM, true,
							IResource.DEPTH_INFINITE);
					fileMap.put(file.getLocationURI(), file);
				} catch (Exception e) {
				}
				monitor.worked(1);
			}
			List<ParseErrorInfo> list = wrapper.parse(files);
			if (list != null) {
				cancelCheck(monitor);
				for (ParseErrorInfo pe : list) {
					cancelCheck(monitor);
					IFile file = fileMap.get(pe.file);
					IDocument document = getDocument(provider, file);
					createErrorMarker(file, document, pe);
				}
			}
			monitor.worked(1);
		} else {
			monitor.worked(files.size() + 1);
		}
	}

	protected IDocument getDocument(FileDocumentProvider provider, IFile file) {
		FileEditorInput input = new FileEditorInput(file);
		try {
			provider.connect(input);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		return provider.getDocument(input);
	}

	protected void createErrorMarker(IFile file, IDocument document,
			ParseErrorInfo pe) {
		try {
			int beginOffset = document.getLineOffset(pe.beginLine - 1)
					+ pe.beginColumn - 1;
			int endOffset = document.getLineOffset(pe.endLine - 1)
					+ pe.endColumn;

			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.SEVERITY, pe.level); // IMarker.SEVERITY_ERROR
			marker.setAttribute(IMarker.MESSAGE, pe.message);
			// marker.setAttribute(IMarker.LINE_NUMBER, pe.beginLine - 1);
			marker.setAttribute(IMarker.CHAR_START, beginOffset);
			marker.setAttribute(IMarker.CHAR_END, endOffset);
			marker.setAttribute(IMarker.LOCATION, String.format("%d:%d-%d:%d",
					pe.beginLine, pe.beginColumn, pe.endLine, pe.endColumn));
			marker.setAttribute(IMarker.TRANSIENT, false);
		} catch (Exception e) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
					"DMDLMarker#createErrorMarker() error.", e));
		}
	}

	protected void cancelCheck(IProgressMonitor monitor)
			throws InterruptedException {
		if (monitor.isCanceled()) {
			throw new InterruptedException();
		}
	}
}
