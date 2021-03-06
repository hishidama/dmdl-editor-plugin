package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard;

import java.io.IOException;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.marker.DMDLErrorCheckHandler;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.DMDLFileUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SelectAddRemovePage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SelectDataModelPage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SelectDataModelPage.ModelFile;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SetAddAttributePage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SetAttributePage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SetRemoveAttributePage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.update.AttributeUpdater;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public class AttributeWizard extends Wizard implements IWorkbenchWizard {

	private IProject project;

	private SelectAddRemovePage selectPage;
	private SetAddAttributePage setAddAttrPage;
	private SetRemoveAttributePage setRemoveAttrPage;
	private SelectDataModelPage modelPage;

	public AttributeWizard() {
		setWindowTitle("属性の追加/削除");
		setDialogSettings(Activator.getDefault().getDialogSettings());
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		List<IFile> list = DMDLFileUtil.getSelectionDmdlFiles();
		if (!list.isEmpty()) {
			this.project = list.get(0).getProject();
		}

		selectPage = new SelectAddRemovePage();
		addPage(selectPage);
		setAddAttrPage = new SetAddAttributePage();
		addPage(setAddAttrPage);
		setRemoveAttrPage = new SetRemoveAttributePage();
		addPage(setRemoveAttrPage);
		modelPage = new SelectDataModelPage("変更するデータモデルの指定", list);
		addPage(modelPage);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == setAddAttrPage) {
			page = setRemoveAttrPage;
		}
		IWizardPage nextPage = super.getNextPage(page);
		return getPage(nextPage);
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		if (page == setRemoveAttrPage) {
			page = setAddAttrPage;
		}
		IWizardPage prevPage = super.getPreviousPage(page);
		return getPage(prevPage);
	}

	private IWizardPage getPage(IWizardPage page) {
		if (page == setAddAttrPage || page == setRemoveAttrPage) {
			return getSetAttributePage();
		}
		if (page == modelPage) {
			if (selectPage.isAdd()) {
				modelPage.setDescription("属性を追加するデータモデルを選択して下さい。");
			} else {
				modelPage.setDescription("属性を削除するデータモデルを選択して下さい。");
			}
		}
		return page;
	}

	private SetAttributePage getSetAttributePage() {
		if (selectPage.isAdd()) {
			return setAddAttrPage;
		} else {
			return setRemoveAttrPage;
		}
	}

	@Override
	public boolean canFinish() {
		IWizardPage[] pages = getPages();
		for (IWizardPage p : pages) {
			IWizardPage page = getPage(p);
			if (!page.isPageComplete()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean performFinish() {
		SetAttributePage setAttrPage = getSetAttributePage();
		String modelAttr = setAttrPage.getModelAttribute();
		String propAttr = setAttrPage.getPropertyAttribute();
		List<ModelFile> list = modelPage.getModelList();

		try {
			AttributeUpdater updater = setAttrPage.getUpdater(selectPage);
			updater.setAttribute(modelAttr, propAttr);
			if (!updater.execute(list)) {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		if (project != null) {
			DMDLErrorCheckHandler handler = new DMDLErrorCheckHandler();
			handler.execute(project, true, true, getContainer());
		}
		return true;
	}
}
