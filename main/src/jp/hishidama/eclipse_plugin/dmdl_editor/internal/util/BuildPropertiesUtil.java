package jp.hishidama.eclipse_plugin.dmdl_editor.internal.util;

import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.PARSER_BUILD_PROPERTIES;

import java.io.FileNotFoundException;
import java.text.MessageFormat;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLEditorConfiguration;
import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DmdlCompilerProperties;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.property.DMDLPropertyPageUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class BuildPropertiesUtil {

	public static String getBuildPropertiesFileName(IProject project) {
		DMDLPropertyPageUtil.initializeProjectParser(project);

		return DMDLPropertyPageUtil.getValue(project, PARSER_BUILD_PROPERTIES);
	}

	public static DmdlCompilerProperties getBuildProperties(IProject project, boolean putError) {
		String path = getBuildPropertiesFileName(project);
		try {
			DMDLEditorConfiguration c = DMDLPropertyPageUtil.getConfiguration(project);
			if (c == null) {
				if (putError) {
					openMessageDialog("Asakusa FrameworkのバージョンをDMDL Editorのプロパティーページで設定して下さい。");
				}
				return null;
			}
			return c.getCompilerProperties(project, path);
		} catch (FileNotFoundException e) {
			if (putError) {
				openFileNotFoundErrorDialog(path);
			}
			return null;
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "build.properties read error", e);
			Activator.getDefault().getLog().log(status);
			if (putError) {
				openErrorDialog(status);
			}
			return null;
		}
	}

	private static void openMessageDialog(final String message) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openWarning(null, "DMDL Editor - build.properties error", message);
			}
		});
	}

	private static void openFileNotFoundErrorDialog(String fname) {
		final String message = MessageFormat.format("プロジェクト内にAsakusa Frameworkのbuild.propertiesが見つかりません。\n"
				+ "DMDL EditorのParserプロパティーページでbuild.propertiesの場所を指定して下さい。\n\n" + "現在指定されているパス={0}", fname);
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openWarning(null, "DMDL Editor - build.properties error", message);
			}
		});
	}

	private static void openErrorDialog(final IStatus status) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				ErrorDialog.openError(null, "DMDL Editor - build.properties error",
						"build.propertiesの読み込み中にエラーが発生しました。", status);
			}
		});
	}
}
