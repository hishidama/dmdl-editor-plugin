package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLImporterExporterDefinition;
import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DmdlCompilerProperties;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.port.DirectioCsvExporterDefinition;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.port.DirectioCsvImporterDefinition;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.port.WindgateCsvExporterDefinition;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.port.WindgateCsvImporterDefinition;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.port.WindgateJdbcExporterDefinition;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.port.WindgateJdbcImporterDefinition;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.BuildPropertiesUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.DMDLFileUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen.ImporterExporterGenerator;
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
		DMDLImporterExporterDefinition[] defs = { new DirectioCsvImporterDefinition(),
				new DirectioCsvExporterDefinition(), new WindgateCsvImporterDefinition(),
				new WindgateCsvExporterDefinition(), new WindgateJdbcImporterDefinition(),
				new WindgateJdbcExporterDefinition() };

		modelPage = new SelectDataModelPage("Importer/Exporterを作成するデータモデルの指定", list);
		modelPage.setDescription("Importer/Exporterを作成するデータモデルを選択して下さい。");
		addPage(modelPage);
		namePage = new SetImporterExporterNamePage();
		namePage.setDefinitions(Arrays.asList(defs));
		addPage(namePage);
		for (DMDLImporterExporterDefinition def : defs) {
			SetImporterExporterMethodPage methodPage = new SetImporterExporterMethodPage(def);
			methodPageList.add(methodPage);
			addPage(methodPage);
		}
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage = super.getNextPage(page);

		Set<DMDLImporterExporterDefinition> set = null;
		while (nextPage instanceof SetImporterExporterMethodPage) {
			SetImporterExporterMethodPage methodPage = (SetImporterExporterMethodPage) nextPage;
			if (set == null) {
				set = namePage.getClassName().keySet();
			}
			if (set.contains(methodPage.getDefinition())) {
				break;
			}
			nextPage = super.getNextPage(methodPage);
		}

		return getPage(nextPage);
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		IWizardPage prevPage = super.getPreviousPage(page);

		Set<DMDLImporterExporterDefinition> set = null;
		while (prevPage instanceof SetImporterExporterMethodPage) {
			SetImporterExporterMethodPage methodPage = (SetImporterExporterMethodPage) prevPage;
			if (set == null) {
				set = namePage.getClassName().keySet();
			}
			if (set.contains(methodPage.getDefinition())) {
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
		Set<DMDLImporterExporterDefinition> set = namePage.getClassName().keySet();

		IWizardPage[] pages = getPages();
		for (IWizardPage page : pages) {
			if (page instanceof SetImporterExporterMethodPage) {
				if (!set.contains(((SetImporterExporterMethodPage) page).getDefinition())) {
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
		Map<DMDLImporterExporterDefinition, String> map = namePage.getClassName();

		boolean first = true;
		for (ModelFile mf : list) {
			IProject project = mf.file.getProject();
			if (properties == null) {
				properties = BuildPropertiesUtil.getBuildProperties(project, true);
			}
			for (SetImporterExporterMethodPage page : methodPageList) {
				String className = map.get(page.getDefinition());
				if (className != null) {
					ImporterExporterGenerator generator = page.getDefinition().getGenerator();
					try {
						String name = StringUtil.append(packName, className);
						generator.generate(project, properties, page.getValues(), mf.model, dir, name, first);
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
