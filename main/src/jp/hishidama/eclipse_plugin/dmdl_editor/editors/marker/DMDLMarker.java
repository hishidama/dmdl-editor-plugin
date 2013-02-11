package jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLDocument;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.IDocument;

public class DMDLMarker {

	protected Map<IJavaProject, DmdlParserWrapper> map = new HashMap<IJavaProject, DmdlParserWrapper>();

	public void parse(IFile file, DMDLDocument document) {
		IJavaProject project = getJavaProject(file);
		if (project == null) {
			return;
		}

		DmdlParserWrapper wrapper = map.get(project);
		if (wrapper == null) {
			wrapper = new DmdlParserWrapper(project);
			map.put(project, wrapper);
		}
		if (wrapper.isValid()) {
			List<ParseError> list = wrapper.parse(file, document);
			if (list != null) {
				for (ParseError pe : list) {
					createErrorMarker(file, document, pe);
				}
			}
		}
	}

	protected final IJavaProject getJavaProject(IFile file) {
		IProject project = file.getProject();
		return JavaCore.create(project);
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
			marker.setAttribute(IMarker.TRANSIENT, true);
		} catch (Exception e) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
					"DMDLMarker#createErrorMarker() error.", e));
		}
	}
}
