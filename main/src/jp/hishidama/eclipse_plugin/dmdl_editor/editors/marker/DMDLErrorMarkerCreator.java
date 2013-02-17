package jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker;

import java.net.URI;
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
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;

public class DMDLErrorMarkerCreator {

	protected Map<String, DmdlParserWrapper> map = new HashMap<String, DmdlParserWrapper>();

	public void parse(List<IFile> files) {
		// 全ファイルが同一プロジェクトである想定
		FileDocumentProvider provider = new FileDocumentProvider();
		createIndex(files, provider);
		checkMark(files, provider);
	}

	protected final IJavaProject getJavaProject(IFile file) {
		IProject project = file.getProject();
		return JavaCore.create(project);
	}

	protected void createIndex(List<IFile> files, FileDocumentProvider provider) {
		IProject project = files.get(0).getProject();
		IndexContainer ic = IndexContainer.createContainer(project);

		DMDLSimpleParser parser = new DMDLSimpleParser();
		for (IFile file : files) {
			IDocument document = getDocument(provider, file);
			DocumentScanner scanner = new DocumentScanner(document);
			ModelList models = parser.parse(scanner);
			for (ModelToken model : models.getNamedModelList()) {
				String modelName = model.getModelName();
				if (modelName != null) {
					ModelIndex mi = ic.createModel(modelName, file, model);
					for (PropertyToken prop : model.getPropertyList()) {
						String propName = prop.getName();
						if (propName != null) {
							mi.addProperty(propName, prop);
						}
					}
				}
			}
		}
	}

	protected void checkMark(List<IFile> files, FileDocumentProvider provider) {
		IJavaProject project = getJavaProject(files.get(0));
		if (project == null) {
			return;
		}

		String projectName = project.getProject().getName();
		DmdlParserWrapper wrapper = map.get(projectName);
		if (wrapper == null) {
			wrapper = new DmdlParserWrapper(project);
			map.put(projectName, wrapper);
		}
		if (wrapper.isValid()) {
			Map<URI, IFile> fileMap = new HashMap<URI, IFile>();
			for (IFile file : files) {
				try {
					file.deleteMarkers(IMarker.PROBLEM, true,
							IResource.DEPTH_INFINITE);
					fileMap.put(file.getLocationURI(), file);
				} catch (Exception e) {
				}
			}
			List<ParseError> list = wrapper.parse(files);
			if (list != null) {
				for (ParseError pe : list) {
					IFile file = fileMap.get(pe.file);
					IDocument document = getDocument(provider, file);
					createErrorMarker(file, document, pe);
				}
			}
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
			ParseError pe) {
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
}
