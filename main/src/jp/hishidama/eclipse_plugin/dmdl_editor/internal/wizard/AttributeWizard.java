package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard;

import java.io.IOException;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.marker.DMDLErrorCheckHandler;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.DMDLFileUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SelectDataModelPage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SelectDataModelPage.ModelFile;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SetAttributePage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.update.AttributeAppender;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.update.AttributeRemover;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.update.AttributeUpdater;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

public class AttributeWizard extends Wizard {

	private IProject project;

	private SetAttributePage selectPage;
	private SelectDataModelPage modelPage;

	public AttributeWizard() {
		setWindowTitle("属性の更新");
		setDialogSettings(Activator.getDefault().getDialogSettings());
	}

	@Override
	public void addPages() {
		this.project = getProject();
		List<IFile> list = DMDLFileUtil.getSelectionDmdlFiles();

		selectPage = new SetAttributePage();
		addPage(selectPage);
		modelPage = new SelectDataModelPage(list);
		addPage(modelPage);
	}

	private IProject getProject() {
		IEditorPart editor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
		return input.getFile().getProject();
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage = super.getNextPage(page);
		return nextPage;
	}

	@Override
	public boolean performFinish() {
		boolean add = selectPage.isAdd();
		String modelAttr = selectPage.getModelAttribute();
		String propAttr = selectPage.getPropertyAttribute();
		List<ModelFile> list = modelPage.getModelList();

		try {
			AttributeUpdater<?> updater;
			if (add) {
				updater = new AttributeAppender();
			} else {
				updater = new AttributeRemover();
			}
			updater.setAttribute(modelAttr, propAttr);
			if (!updater.execute(list)) {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		DMDLErrorCheckHandler handler = new DMDLErrorCheckHandler();
		handler.execute(project, true, true);
		return true;
	}
}
