package jp.hishidama.eclipse_plugin.dialog;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("restriction")
public class NewVariableEntryDialog extends
		org.eclipse.jdt.internal.ui.wizards.buildpaths.NewVariableEntryDialog {

	public NewVariableEntryDialog(Shell parent) {
		super(parent);
	}

	@Override
	public IPath[] getResult() {
		int r = getReturnCode();
		if (r == Window.OK) {
			return super.getResult();
		} else {
			return null;
		}
	}
}
