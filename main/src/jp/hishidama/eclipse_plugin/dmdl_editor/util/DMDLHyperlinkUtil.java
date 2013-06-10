package jp.hishidama.eclipse_plugin.dmdl_editor.util;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index.IndexContainer;

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

/**
 * DMDLハイパーリンクユーティリティー.
 *
 * @since 2013.03.17
 */
public class DMDLHyperlinkUtil {

	/**
	 * 指定された名前の場所へ移動する（ファイルを開く）.
	 *
	 * @param project
	 *            プロジェクト（null不可）
	 * @param modelName
	 *            モデル名（nullの場合は移動に失敗する）
	 * @param propertyName
	 *            プロパティー名（nullの場合はモデル名へジャンプ）
	 * @return 移動が成功した場合true、失敗した場合false
	 */
	public static boolean gotoPosition(IProject project, String modelName, String propertyName) {
		assert project != null;

		if (modelName == null) {
			return false;
		}

		IndexContainer ic = IndexContainer.getContainer(project);

		DataModelPosition index;
		if (propertyName == null) {
			index = ic.getModel(modelName);
		} else {
			index = ic.getProperty(modelName, propertyName);
		}

		return gotoPosition(index);
	}

	public static boolean gotoPosition(DataModelPosition index) {
		if (index != null) {
			return gotoPosition(index.getFile(), index.getOffset(), index.getEnd());
		}
		return false;
	}

	public static boolean gotoPosition(IFile file, int start, int end) {
		assert file != null;
		try {
			IMarker marker = file.createMarker(IMarker.TEXT);
			try {
				marker.setAttribute(IMarker.CHAR_START, start);
				marker.setAttribute(IMarker.CHAR_END, end);

				IWorkbench workbench = PlatformUI.getWorkbench();
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				IWorkbenchPage page = window.getActivePage();
				IEditorPart editor = IDE.openEditor(page, file);
				IGotoMarker target = (IGotoMarker) editor.getAdapter(IGotoMarker.class);
				target.gotoMarker(marker);
				return true;
			} finally {
				marker.delete();
			}
		} catch (Exception e) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID, "gotoMarker error.", e));
			return false;
		}
	}
}
