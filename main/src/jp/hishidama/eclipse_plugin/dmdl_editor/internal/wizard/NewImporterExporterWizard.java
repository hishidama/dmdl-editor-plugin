package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLImporterExporterGenerator;
import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DmdlCompilerProperties;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.BuildPropertiesUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.DMDLFileUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SelectDataModelPage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SelectDataModelPage.ModelFile;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SetImporterExporterMethodPage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SetImporterExporterNamePage;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public class NewImporterExporterWizard extends Wizard implements IWorkbenchWizard {

	private SelectDataModelPage modelPage;
	private SetImporterExporterNamePage namePage;
	private List<SetImporterExporterMethodPage> methodPageList = new ArrayList<SetImporterExporterMethodPage>();

	private DmdlCompilerProperties properties = null;

	public NewImporterExporterWizard() {
		setWindowTitle("Importer/Exporterクラスの作成");
		setDialogSettings(Activator.getDefault().getDialogSettings());
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		List<IFile> list = DMDLFileUtil.getSelectionDmdlFiles();
		List<DMDLImporterExporterGenerator> gens = Activator.getExtensionLoader().getImporterExporterGenerators();

		modelPage = new SelectDataModelPage("Importer/Exporterを作成するデータモデルの指定", list);
		modelPage.setDescription("Importer/Exporterを作成するデータモデルを選択して下さい。");
		addPage(modelPage);
		namePage = new SetImporterExporterNamePage();
		namePage.setGenerators(gens);
		addPage(namePage);
		for (DMDLImporterExporterGenerator gen : gens) {
			SetImporterExporterMethodPage methodPage = new SetImporterExporterMethodPage(gen);
			methodPageList.add(methodPage);
			addPage(methodPage);
		}
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage = super.getNextPage(page);

		Set<DMDLImporterExporterGenerator> set = null;
		while (nextPage instanceof SetImporterExporterMethodPage) {
			SetImporterExporterMethodPage methodPage = (SetImporterExporterMethodPage) nextPage;
			if (set == null) {
				set = namePage.getClassName().keySet();
			}
			if (set.contains(methodPage.getGenerator())) {
				break;
			}
			nextPage = super.getNextPage(methodPage);
		}

		return getPage(nextPage);
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		IWizardPage prevPage = super.getPreviousPage(page);

		Set<DMDLImporterExporterGenerator> set = null;
		while (prevPage instanceof SetImporterExporterMethodPage) {
			SetImporterExporterMethodPage methodPage = (SetImporterExporterMethodPage) prevPage;
			if (set == null) {
				set = namePage.getClassName().keySet();
			}
			if (set.contains(methodPage.getGenerator())) {
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
				properties = BuildPropertiesUtil.getBuildProperties(project, true);
			}
			namePage.setProperties(properties);
		}
		return page;
	}

	@Override
	public boolean canFinish() {
		Set<DMDLImporterExporterGenerator> set = namePage.getClassName().keySet();

		IWizardPage[] pages = getPages();
		for (IWizardPage page : pages) {
			if (page instanceof SetImporterExporterMethodPage) {
				if (!set.contains(((SetImporterExporterMethodPage) page).getGenerator())) {
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
		Map<DMDLImporterExporterGenerator, String> map = namePage.getClassName();

		boolean first = true;
		for (ModelFile mf : list) {
			IProject project = mf.file.getProject();
			if (properties == null) {
				properties = BuildPropertiesUtil.getBuildProperties(project, true);
			}
			for (SetImporterExporterMethodPage page : methodPageList) {
				DMDLImporterExporterGenerator generator = page.getGenerator();
				String className = map.get(generator);
				if (className != null) {
					try {
						String name = StringUtil.append(packName, className);
						generator.generate(project, properties, page.getValues(), mf.model, dir, name, first);
						first = false;
					} catch (CoreException e) {
						String message = MessageFormat.format("生成中にエラーが発生しました\nmodel={0}\ntype={1}",
								mf.model.getModelName(), generator.getDisplayName());
						ErrorDialog.openError(getShell(), "error", message, e.getStatus());
					}
				}
			}
		}

		return true;
	}
}
