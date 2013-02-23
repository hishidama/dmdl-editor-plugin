package jp.hishidama.eclipse_plugin.dmdl_editor.editors.hyperlink;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.index.Index;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.index.IndexContainer;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;

public class HyperlinkUtil {

	public static boolean gotoPosition(IProject project, DMDLToken token) {
		for (; token != null; token = token.getParent()) {
			if (token instanceof ModelToken) {
				ModelToken model = (ModelToken) token;
				return gotoPosition(project, null, model.getModelName(), null);
			} else if (token instanceof PropertyToken) {
				PropertyToken prop = (PropertyToken) token;
				ModelToken model = prop.getModelToken();
				if (model == null) {
					return false;
				}
				return gotoPosition(project, null, model.getModelName(),
						prop.getName());
			}
		}
		return false;
	}

	public static boolean gotoPosition(IProject project, IFile file,
			String modelName, String propertyName) {
		if (modelName == null) {
			return false;
		}

		IndexContainer ic = IndexContainer.getContainer(project, file);
		if (ic == null) {
			return false;
		}

		Index index;
		if (propertyName == null) {
			index = ic.findModel(modelName);
		} else {
			index = ic.findProperty(modelName, propertyName);
		}

		return gotoPosition(index);
	}

	private static boolean gotoPosition(Index index) {
		if (index != null) {
			return gotoPosition(index.getFile(), index.getOffset(),
					index.getEnd());
		}
		return false;
	}

	public static boolean gotoPosition(IFile file, int start, int end) {
		try {
			IMarker marker = file.createMarker(IMarker.TEXT);
			try {
				marker.setAttribute(IMarker.CHAR_START, start);
				marker.setAttribute(IMarker.CHAR_END, end);

				IWorkbench workbench = PlatformUI.getWorkbench();
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				IWorkbenchPage page = window.getActivePage();
				IEditorPart editor = IDE.openEditor(page, file);
				IGotoMarker target = (IGotoMarker) editor
						.getAdapter(IGotoMarker.class);
				target.gotoMarker(marker);
				return true;
			} finally {
				marker.delete();
			}
		} catch (Exception e) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
					"gotoMarker error.", e));
			return false;
		}
	}
}
