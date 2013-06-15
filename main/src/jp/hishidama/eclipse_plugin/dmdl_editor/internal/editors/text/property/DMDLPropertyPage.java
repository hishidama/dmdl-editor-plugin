package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.property;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLEditorConfiguration;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.util.swt.CheckedTableUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.PropertyPage;

public class DMDLPropertyPage extends PropertyPage {

	private Table table;

	public DMDLPropertyPage() {
		// noDefaultAndApplyButton();
	}

	@Override
	protected Control createContents(Composite parent) {
		setTitle("Asakusa Framework DMDL Editor");

		Composite composite = new Composite(parent, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 2; // 列数
			composite.setLayout(layout);
		}
		createTable(composite);

		return composite;
	}

	private void createTable(Composite composite) {
		table = new Table(composite, SWT.BORDER | SWT.CHECK);
		GridData grid = GridDataFactory.fillDefaults().span(2, 1).minSize(256, 20 * 6).hint(256, 20 * 6).create();
		table.setLayoutData(grid);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		CheckedTableUtil.setSingleCheckedTable(table);

		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setWidth(256);
		col.setText("version name");

		IProject project = getProject();
		String defaultName = DMDLPropertyPageUtil.getConfigurationName(project);

		List<DMDLEditorConfiguration> list = Activator.getExtensionLoader().getConfigurations();
		for (DMDLEditorConfiguration c : list) {
			String name = null;
			try {
				name = c.getConfigurationName();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (name == null) {
				continue;
			}
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, name);
			item.setData(c);
			if (name.equals(defaultName)) {
				item.setChecked(true);
			}
		}
	}

	private IProject getProject() {
		IAdaptable element = getElement();
		if (element instanceof IResource) {
			return ((IResource) getElement()).getProject();
		}
		if (element instanceof IJavaElement) {
			return ((IJavaElement) element).getJavaProject().getProject();
		}
		IProject project = (IProject) element.getAdapter(IProject.class);
		if (project != null) {
			return project;
		}
		throw new UnsupportedOperationException("未対応:" + element.getClass());
	}

	@Override
	protected void performDefaults() {
		for (TableItem item : table.getItems()) {
			item.setChecked(false);
		}

		IProject project = getProject();
		DMDLEditorConfiguration dc = DMDLPropertyPageUtil.getDefaultConfiguration(project);
		if (dc != null) {
			String dname = dc.getConfigurationName();
			for (TableItem item : table.getItems()) {
				DMDLEditorConfiguration c = (DMDLEditorConfiguration) item.getData();
				if (dname.equals(c.getConfigurationName())) {
					item.setChecked(true);
					break;
				}
			}
		}

		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		String name = null;
		for (TableItem item : table.getItems()) {
			if (item.getChecked()) {
				DMDLEditorConfiguration c = (DMDLEditorConfiguration) item.getData();
				name = c.getConfigurationName();
				break;
			}
		}

		if (name == null) {
			name = "";
		}
		IProject project = getProject();
		DMDLPropertyPageUtil.setConfigurationName(project, name);
		return true;
	}
}
