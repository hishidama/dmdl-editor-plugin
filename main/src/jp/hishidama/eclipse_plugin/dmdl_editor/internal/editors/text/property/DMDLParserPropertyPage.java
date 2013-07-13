package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.property;

import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.PARSER_BUILD_PROPERTIES;

import java.util.List;

import jp.hishidama.eclipse_plugin.dialog.NewVariableEntryDialog;
import jp.hishidama.eclipse_plugin.dialog.ProjectFileSelectionDialog;
import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLEditorConfiguration;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.marker.ParserClassUtil;
import jp.hishidama.eclipse_plugin.util.swt.CheckedTableUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

public class DMDLParserPropertyPage extends PropertyPage {

	private Table versionTable;
	private Text buildProperties;
	private CheckboxTableViewer classpathViewer;
	private Button replaceButton;
	private Button removeButton;

	@Override
	protected Control createContents(Composite parent) {
		setTitle("Dmdl Parser(Compiler)");
		final IProject project = getProject();

		Composite composite = new Composite(parent, SWT.NONE);
		{
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			GridLayout layout = new GridLayout();
			layout.numColumns = 3; // 列数
			composite.setLayout(layout);
		}

		{
			Label label = new Label(composite, SWT.NONE);
			label.setLayoutData(GridDataFactory.swtDefaults().span(3, 1).create());
			label.setText("Asakusa Framework本体のDmdlParserを呼び出す為の設定を指定して下さい。");
		}
		createVersionTable(composite);
		createBuildPropertiesField(composite, project);
		createClasspathTable(composite, project);

		return composite;
	}

	private void createVersionTable(Composite composite) {
		{
			Label label = new Label(composite, SWT.NONE);
			label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			label.setText("version name:");
		}

		versionTable = new Table(composite, SWT.BORDER | SWT.CHECK);
		GridData grid = GridDataFactory.swtDefaults().span(2, 1).minSize(256, 20 * 4).hint(256, 20 * 4).create();
		versionTable.setLayoutData(grid);
		versionTable.setHeaderVisible(false);
		versionTable.setLinesVisible(true);
		CheckedTableUtil.setSingleCheckedTable(versionTable);
		versionTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshReplaceButton();
			}
		});

		TableColumn col = new TableColumn(versionTable, SWT.NONE);
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
			TableItem item = new TableItem(versionTable, SWT.NONE);
			item.setText(0, name);
			item.setData(c);
			if (name.equals(defaultName)) {
				item.setChecked(true);
			}
		}

		// refreshReplaceButton();
	}

	private void refreshReplaceButton() {
		int checked = 0;
		for (TableItem item : versionTable.getItems()) {
			if (item.getChecked()) {
				checked++;
			}
		}
		replaceButton.setEnabled(checked == 1);
	}

	private void createBuildPropertiesField(Composite composite, final IProject project) {
		Label label = new Label(composite, SWT.NONE);
		label.setText("build.properties path:");

		buildProperties = new Text(composite, SWT.SINGLE | SWT.BORDER);
		buildProperties.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		String value = DMDLPropertyPageUtil.getValue(project, PARSER_BUILD_PROPERTIES);
		if (value != null) {
			buildProperties.setText(value);
		}

		final Button button = new Button(composite, SWT.NONE);
		button.setText("Browse");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ProjectFileSelectionDialog dialog = new ProjectFileSelectionDialog(getShell(), project);
				dialog.setTitle("build.properties Selection");
				dialog.setMessage("Select the build.properties for Asakusa Framework:");
				dialog.setAllowMultiple(false);
				dialog.setHelpAvailable(false);
				dialog.setInitialSelection(buildProperties.getText());
				dialog.open();
				String[] r = dialog.getResult();
				if (r != null && r.length >= 1) {
					buildProperties.setText(r[0]);
				}

				e.doit = true;
			}
		});
	}

	private void createClasspathTable(Composite composite, final IProject project) {
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		label.setText("jar files (classpath):");

		{
			Composite rows = new Composite(composite, SWT.NONE);
			{
				rows.setLayoutData(new GridData(GridData.FILL_BOTH));
				GridLayout layout = new GridLayout(1, false);
				layout.marginWidth = 0;
				layout.marginHeight = 0;
				layout.horizontalSpacing = 0;
				layout.verticalSpacing = 0;
				rows.setLayout(layout);
			}
			{
				classpathViewer = CheckboxTableViewer.newCheckList(rows, SWT.BORDER | SWT.MULTI);
				Table table = classpathViewer.getTable();
				GridData tableGrid = new GridData(GridData.FILL_BOTH);
				tableGrid.widthHint = 380;
				tableGrid.minimumHeight = 20 * 9;
				table.setLayoutData(tableGrid);
				table.setLinesVisible(true);
				ParserClassUtil.initTable(classpathViewer, project);
				classpathViewer.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						if (removeButton != null) {
							ISelection selection = event.getSelection();
							removeButton.setEnabled(!selection.isEmpty());
						}
					}
				});

				Composite cols = new Composite(rows, SWT.NONE);
				{
					GridLayout layout = new GridLayout(2, false);
					layout.marginWidth = 0;
					cols.setLayout(layout);
				}
				Button button1 = new Button(cols, SWT.NONE);
				button1.setText("check all");
				button1.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						classpathViewer.setAllChecked(true);
						e.doit = true;
					}
				});

				Button button2 = new Button(cols, SWT.NONE);
				button2.setText("uncheck all");
				button2.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						classpathViewer.setAllChecked(false);
						e.doit = true;
					}
				});
			}
			{
				new Label(rows, SWT.NONE).setText("※DmdlParserを実行する際には、上記のライブラリーの他にプロジェクトのビルドパスも追加されます。");
			}
		}

		{
			Composite rows = new Composite(composite, SWT.NONE);
			GridLayout layout = new GridLayout(1, false);
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			layout.horizontalSpacing = 0;
			layout.verticalSpacing = 5;
			rows.setLayout(layout);
			rows.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

			Button button0 = new Button(rows, SWT.NONE);
			button0.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
			button0.setText("Replace default");
			button0.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					DMDLEditorConfiguration c = null;
					for (TableItem item : versionTable.getItems()) {
						if (item.getChecked()) {
							c = (DMDLEditorConfiguration) item.getData();
							break;
						}
					}
					ParserClassUtil.initTableDefault(classpathViewer, project, c);
				}
			});
			replaceButton = button0;
			refreshReplaceButton();

			Button button1 = new Button(rows, SWT.NONE);
			button1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
			button1.setText("Add JARs...");
			button1.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					ProjectFileSelectionDialog dialog = new ProjectFileSelectionDialog(getShell(), project);
					dialog.setTitle("JAR Selection");
					dialog.setMessage("Choose the archives to be added to the classpath of DmdlCompiler:");
					dialog.setAllowMultiple(true);
					dialog.setHelpAvailable(false);
					dialog.setInitialSelection(buildProperties.getText());
					dialog.addFileterExtension("jar", "zip");
					dialog.open();
					String[] r = dialog.getResult();
					if (r != null) {
						for (String file : r) {
							classpathViewer.add(file);
						}
					}

					e.doit = true;
				}
			});

			Button button2 = new Button(rows, SWT.NONE);
			button2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
			button2.setText("Add External JARs...");
			button2.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.MULTI);
					dialog.setText("JAR Selection");
					dialog.setFilterExtensions(new String[] { "*.jar; *.zip", "*.*" });
					String file = dialog.open();
					if (file != null) {
						classpathViewer.add(file);
					}

					e.doit = true;
				}
			});

			Button button3 = new Button(rows, SWT.NONE);
			button3.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
			button3.setText("Add Variable...");
			button3.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					NewVariableEntryDialog dialog = new NewVariableEntryDialog(getShell());
					dialog.setTitle("JAR Selection");
					dialog.open();
					IPath[] r = dialog.getResult();
					if (r != null) {
						for (IPath path : r) {
							String file = path.toPortableString();
							classpathViewer.add(file);
						}
					}

					e.doit = true;
				}
			});

			Button button4 = new Button(rows, SWT.NONE);
			button4.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
			button4.setText("Remove");
			button4.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					IStructuredSelection selection = (IStructuredSelection) classpathViewer.getSelection();
					classpathViewer.remove(selection.toArray());
					e.doit = true;
				}
			});
			button4.setEnabled(false);
			removeButton = button4;
		}
	}

	@Override
	protected void performDefaults() {
		IProject project = getProject();

		for (TableItem item : versionTable.getItems()) {
			item.setChecked(false);
		}
		DMDLEditorConfiguration dc = DMDLPropertyPageUtil.getDefaultConfiguration(project);
		if (dc != null) {
			String dname = dc.getConfigurationName();
			for (TableItem item : versionTable.getItems()) {
				DMDLEditorConfiguration c = (DMDLEditorConfiguration) item.getData();
				if (dname.equals(c.getConfigurationName())) {
					item.setChecked(true);
					break;
				}
			}
		}

		buildProperties.setText(getDefaultBuildPropertiesPath(project));

		ParserClassUtil.initTableDefault(classpathViewer, project);

		super.performDefaults();
	}

	private String getDefaultBuildPropertiesPath(IProject project) {
		DMDLEditorConfiguration c = DMDLPropertyPageUtil.getConfiguration(project);
		if (c != null) {
			return c.getDefaultBuildPropertiesPath();
		}

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getString(PARSER_BUILD_PROPERTIES);
	}

	@Override
	public boolean performOk() {
		IProject project = getProject();

		{
			String versionName = null;
			for (TableItem item : versionTable.getItems()) {
				if (item.getChecked()) {
					DMDLEditorConfiguration c = (DMDLEditorConfiguration) item.getData();
					versionName = c.getConfigurationName();
					break;
				}
			}

			if (versionName == null) {
				versionName = "";
			}
			DMDLPropertyPageUtil.setConfigurationName(project, versionName);
		}

		DMDLPropertyPageUtil.setValue(project, PARSER_BUILD_PROPERTIES, buildProperties.getText());
		ParserClassUtil.save(classpathViewer, project);

		return true;
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
}
