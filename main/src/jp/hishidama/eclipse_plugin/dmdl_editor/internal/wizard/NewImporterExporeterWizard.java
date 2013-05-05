package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.BuildPropertiesUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.DMDLFileUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen.ImporterExporterGenerator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.ImporterExporterType;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SelectDataModelPage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SetImporterExporterMethodPage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SetImporterExporterNamePage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SelectDataModelPage.ModelFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class NewImporterExporeterWizard extends Wizard {

	private SelectDataModelPage modelPage;
	private SetImporterExporterNamePage namePage;
	private List<SetImporterExporterMethodPage> methodPageList = new ArrayList<SetImporterExporterMethodPage>();

	private Properties properties = null;

	public NewImporterExporeterWizard() {
		setWindowTitle("Importer/Exporterクラスの作成");
		setDialogSettings(Activator.getDefault().getDialogSettings());
	}

	@Override
	public void addPages() {
		List<IFile> list = DMDLFileUtil.getSelectionDmdlFiles();

		modelPage = new SelectDataModelPage("Importer/Exporterを作成するデータモデルの指定", list);
		modelPage.setDescription("Importer/Exporterを作成するデータモデルを選択して下さい。");
		addPage(modelPage);
		namePage = new SetImporterExporterNamePage();
		addPage(namePage);
		for (ImporterExporterType type : ImporterExporterType.values()) {
			SetImporterExporterMethodPage methodPage = new SetImporterExporterMethodPage(type);
			methodPageList.add(methodPage);
			addPage(methodPage);
		}
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage = super.getNextPage(page);

		Set<ImporterExporterType> set = null;
		while (nextPage instanceof SetImporterExporterMethodPage) {
			SetImporterExporterMethodPage methodPage = (SetImporterExporterMethodPage) nextPage;
			if (set == null) {
				set = namePage.getClassName().keySet();
			}
			if (set.contains(methodPage.getType())) {
				break;
			}
			nextPage = super.getNextPage(methodPage);
		}

		return getPage(nextPage);
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		IWizardPage prevPage = super.getPreviousPage(page);

		Set<ImporterExporterType> set = null;
		while (prevPage instanceof SetImporterExporterMethodPage) {
			SetImporterExporterMethodPage methodPage = (SetImporterExporterMethodPage) prevPage;
			if (set == null) {
				set = namePage.getClassName().keySet();
			}
			if (set.contains(methodPage.getType())) {
				break;
			}
			prevPage = super.getPreviousPage(methodPage);
		}

		return getPage(prevPage);
	}

	private IWizardPage getPage(IWizardPage page) {
		if (page == namePage) {
			if (properties == null) {
				List<ModelFile> list = modelPage.getModelList();
				IProject project = list.get(0).file.getProject();
				properties = BuildPropertiesUtil.getBuildProperties(project);
			}
			namePage.setProperties(properties);
		}
		return page;
	}

	@Override
	public boolean canFinish() {
		Set<ImporterExporterType> set = namePage.getClassName().keySet();

		IWizardPage[] pages = getPages();
		for (IWizardPage page : pages) {
			if (page instanceof SetImporterExporterMethodPage) {
				if (!set.contains(((SetImporterExporterMethodPage) page).getType())) {
					continue;
				}
			}
			if (!page.isPageComplete()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean performFinish() {
		List<ModelFile> list = modelPage.getModelList();
		String dir = namePage.getSrcDirectory();
		String packName = namePage.getPackageName();
		Map<ImporterExporterType, String> map = namePage.getClassName();

		boolean first = true;
		for (ModelFile mf : list) {
			IProject project = mf.file.getProject();
			if (properties == null) {
				properties = BuildPropertiesUtil.getBuildProperties(project);
			}
			for (SetImporterExporterMethodPage page : methodPageList) {
				String className = map.get(page.getType());
				if (className != null) {
					ImporterExporterGenerator generator = ImporterExporterGenerator.get(page.getType());
					try {
						generator.generate(project, properties, page, mf.model, dir, packName, className, first);
						first = false;
					} catch (CoreException e) {
						ErrorDialog.openError(getShell(), "error", "生成中にエラーが発生しました。", e.getStatus());
					}
				}
			}
		}

		return true;
	}
}
