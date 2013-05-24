package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.CreateDataModelJoinKeyPage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.CreateDataModelJoinPage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.CreateDataModelNormalPage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.CreateDataModelPage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.CreateDataModelProjectivePage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.CreateDataModelSummarizePage;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.DataModelType;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SetDataModelNamePage;
import jp.hishidama.eclipse_plugin.util.FileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public class NewDataModelWizard extends Wizard implements IWorkbenchWizard {
	private IProject project;
	private String defaultFile = "src/main/dmdl/";

	private SetDataModelNamePage modelPage;
	private CreateDataModelJoinPage joinPage;
	private CreateDataModelJoinKeyPage joinKeyPage;
	private Map<DataModelType, List<IWizardPage>> createPageMap = new EnumMap<DataModelType, List<IWizardPage>>(
			DataModelType.class);

	public NewDataModelWizard() {
		setWindowTitle("データモデルの作成");
		setDialogSettings(Activator.getDefault().getDialogSettings());
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		Object obj = selection.getFirstElement();
		if (obj instanceof IFile) {
			init((IFile) obj);
			return;
		}
		if (obj instanceof IResource) {
			IResource resource = (IResource) obj;
			project = resource.getProject();
			return;
		}
		if (obj instanceof IJavaElement) {
			project = ((IJavaElement) obj).getJavaProject().getProject();
			return;
		}

		IEditorPart editor = workbench.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editor != null) {
			IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput) input).getFile();
				init(file);
				return;
			}
		}

	}

	private void init(IFile file) {
		project = file.getProject();
		if ("dmdl".equals(file.getFileExtension())) {
			defaultFile = file.getProjectRelativePath().toPortableString();
		}
	}

	@Override
	public void addPages() {
		modelPage = new SetDataModelNamePage(project);
		modelPage.setDmdlFile(defaultFile);
		addPage(modelPage);

		addPage(DataModelType.NORMAL, new CreateDataModelNormalPage());
		addPage(DataModelType.SUMMARIZED, new CreateDataModelSummarizePage());
		joinPage = new CreateDataModelJoinPage();
		joinKeyPage = new CreateDataModelJoinKeyPage();
		addPage(DataModelType.JOINED, joinPage);
		addPage(DataModelType.JOINED, joinKeyPage);
		addPage(DataModelType.PROJECTIVE, new CreateDataModelProjectivePage());
	}

	private void addPage(DataModelType type, WizardPage page) {
		List<IWizardPage> list = createPageMap.get(type);
		if (list == null) {
			list = new ArrayList<IWizardPage>();
			createPageMap.put(type, list);
		}
		list.add(page);

		if (page instanceof CreateDataModelPage) {
			((CreateDataModelPage<?>) page).setProject(project);
		}
		addPage(page);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		DataModelType type = modelPage.getDataModelType();
		List<IWizardPage> list = createPageMap.get(type);

		IWizardPage nextPage;
		if (page == modelPage) {
			nextPage = list.get(0);
		} else {
			nextPage = null;
			for (int i = 0; i < list.size(); i++) {
				IWizardPage p = list.get(i);
				if (p == page) {
					if (i + 1 < list.size()) {
						nextPage = list.get(i + 1);
					} else {
						nextPage = null;
					}
					break;
				}
			}
		}
		return getPage(nextPage);
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		DataModelType type = modelPage.getDataModelType();
		List<IWizardPage> list = createPageMap.get(type);

		IWizardPage prevPage;
		if (page == list.get(0)) {
			prevPage = modelPage;
		} else {
			prevPage = modelPage;
			for (int i = 0; i < list.size(); i++) {
				IWizardPage p = list.get(i);
				if (p == page) {
					if (i - 1 >= 0) {
						prevPage = list.get(i - 1);
					} else {
						prevPage = modelPage;
					}
					break;
				}
			}
		}
		return getPage(prevPage);
	}

	private IWizardPage getPage(IWizardPage page) {
		if (page == joinKeyPage) {
			joinKeyPage.setSourceList(joinPage.getSelectedModelList());
		}
		if (page instanceof CreateDataModelPage) {
			CreateDataModelPage<?> createPage = (CreateDataModelPage<?>) page;
			createPage.setModelName(modelPage.getDataModelName(), modelPage.getDataModelDescription());
			return createPage;
		}
		return page;
	}

	@Override
	public boolean canFinish() {
		IWizardPage page = getContainer().getCurrentPage();
		CreateDataModelPage<?> lastPage = getLastPage();
		if (page != lastPage) {
			return false;
		}
		return lastPage.isPageComplete();
	}

	private CreateDataModelPage<?> getLastPage() {
		DataModelType type = modelPage.getDataModelType();
		List<IWizardPage> list = createPageMap.get(type);
		IWizardPage lastPage = list.get(list.size() - 1);
		return (CreateDataModelPage<?>) lastPage;
	}

	@Override
	public boolean performFinish() {
		try {
			save();
			return true;
		} catch (CoreException e) {
			ErrorDialog.openError(getShell(), "error", "error", e.getStatus());
			return false;
		}
	}

	private void save() throws CoreException {
		CreateDataModelPage<?> createPage = getLastPage();
		String text = createPage.getDataModelText();

		String path = modelPage.getDmdlFile();
		IFile file = project.getFile(path);
		if (!file.exists()) {
			FileUtil.save(file, text);
		} else {
			StringBuilder sb = FileUtil.load(file);
			sb.append("\n");
			sb.append(text);
			FileUtil.save(file, sb.toString());
		}
		FileUtil.openFile(file);
	}
}
