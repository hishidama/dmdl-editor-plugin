package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class AttributeWizardHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = null;
		AttributeWizard wizard = new AttributeWizard();
		WizardDialog dialog = new WizardDialog(shell, wizard);
		boolean old = TrayDialog.isDialogHelpAvailable();
		try {
			TrayDialog.setDialogHelpAvailable(false);
			dialog.open();
		} finally {
			TrayDialog.setDialogHelpAvailable(old);
		}
		return null;
	}
}
