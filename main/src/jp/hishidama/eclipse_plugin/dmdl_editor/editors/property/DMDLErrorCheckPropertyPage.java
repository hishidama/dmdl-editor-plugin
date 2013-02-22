package jp.hishidama.eclipse_plugin.dmdl_editor.editors.property;

import static jp.hishidama.eclipse_plugin.dmdl_editor.editors.preference.PreferenceConst.PARSER_BUILD_PROPERTIES;
import jp.hishidama.eclipse_plugin.dialog.NewVariableEntryDialog;
import jp.hishidama.eclipse_plugin.dialog.ProjectFileSelectionDialog;
import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker.ParserClassUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.QualifiedName;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

public class DMDLErrorCheckPropertyPage extends PropertyPage {

	private Text buildProperties;
	private CheckboxTableViewer viewer;
	private Button removeButton;

	@Override
	protected Control createContents(Composite parent) {
		setTitle("Dmdl Parser settings");
		final IProject project = (IProject) getElement();

		Composite composite = new Composite(parent, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 3; // 列数
			composite.setLayout(layout);
		}
		{ // build.propertiesのパスを入力する行
			Label label = new Label(composite, SWT.NONE);
			label.setText("build.properties path:");

			buildProperties = new Text(composite, SWT.SINGLE | SWT.BORDER);
			buildProperties
					.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			String value = getValue(project, PARSER_BUILD_PROPERTIES);
			if (value != null) {
				buildProperties.setText(value);
			}

			final Button button = new Button(composite, SWT.NONE);
			button.setText("Browse");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					ProjectFileSelectionDialog dialog = new ProjectFileSelectionDialog(
							getShell(), project);
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
		{ // クラスパス一覧
			Label label = new Label(composite, SWT.NONE);
			label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
			label.setText("jar files (classpath):");

			{
				Composite rows = new Composite(composite, SWT.NONE);
				{
					GridLayout layout = new GridLayout(1, false);
					layout.marginWidth = 0;
					layout.marginHeight = 0;
					layout.horizontalSpacing = 0;
					layout.verticalSpacing = 0;
					rows.setLayout(layout);
					rows.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
							false));
				}
				{
					viewer = CheckboxTableViewer.newCheckList(rows, SWT.BORDER
							| SWT.MULTI);
					Table table = viewer.getTable();
					table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					table.setLinesVisible(true);
					ParserClassUtil.initTable(viewer, project);
					viewer.addSelectionChangedListener(new ISelectionChangedListener() {
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
							viewer.setAllChecked(true);
							e.doit = true;
						}
					});

					Button button2 = new Button(cols, SWT.NONE);
					button2.setText("uncheck all");
					button2.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							viewer.setAllChecked(false);
							e.doit = true;
						}
					});
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

				Button button1 = new Button(rows, SWT.NONE);
				button1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
						false));
				button1.setText("Add JARs...");
				button1.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						ProjectFileSelectionDialog dialog = new ProjectFileSelectionDialog(
								getShell(), project);
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
								viewer.add(file);
							}
						}

						e.doit = true;
					}
				});

				Button button2 = new Button(rows, SWT.NONE);
				button2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
						false));
				button2.setText("Add External JARs...");
				button2.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						FileDialog dialog = new FileDialog(getShell(), SWT.OPEN
								| SWT.MULTI);
						dialog.setText("JAR Selection");
						dialog.setFilterExtensions(new String[] {
								"*.jar; *.zip", "*.*" });
						String file = dialog.open();
						if (file != null) {
							viewer.add(file);
						}

						e.doit = true;
					}
				});

				Button button3 = new Button(rows, SWT.NONE);
				button3.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
						false));
				button3.setText("Add Variable...");
				button3.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						NewVariableEntryDialog dialog = new NewVariableEntryDialog(
								getShell());
						dialog.setTitle("JAR Selection");
						dialog.open();
						IPath[] r = dialog.getResult();
						if (r != null) {
							for (IPath path : r) {
								String file = path.toPortableString();
								viewer.add(file);
							}
						}

						e.doit = true;
					}
				});

				Button button4 = new Button(rows, SWT.NONE);
				button4.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
						false));
				button4.setText("Remove");
				button4.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						IStructuredSelection selection = (IStructuredSelection) viewer
								.getSelection();
						viewer.remove(selection.toArray());
						e.doit = true;
					}
				});
				button4.setEnabled(false);
				removeButton = button4;
			}
		}

		return composite;
	}

	@Override
	public boolean performOk() {
		IProject project = (IProject) getElement();

		setValue(project, PARSER_BUILD_PROPERTIES, buildProperties.getText());
		ParserClassUtil.save(viewer, project);

		return true;
	}

	@Override
	protected void performDefaults() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		buildProperties.setText(store.getString(PARSER_BUILD_PROPERTIES));

		ParserClassUtil.initTable(viewer, store);

		super.performDefaults();
	}

	private String getValue(IProject project, String key) {
		try {
			String value = project.getPersistentProperty(new QualifiedName(
					Activator.PLUGIN_ID, key));
			if (value != null) {
				return value;
			}
			IPreferenceStore store = Activator.getDefault()
					.getPreferenceStore();
			return store.getString(key);
		} catch (CoreException e) {
			ILog log = Activator.getDefault().getLog();
			log.log(e.getStatus());
			return null;
		}
	}

	private void setValue(IProject project, String key, String value) {
		try {
			project.setPersistentProperty(new QualifiedName(
					Activator.PLUGIN_ID, key), value);
		} catch (CoreException e) {
			ILog log = Activator.getDefault().getLog();
			log.log(e.getStatus());
		}
	}
}
