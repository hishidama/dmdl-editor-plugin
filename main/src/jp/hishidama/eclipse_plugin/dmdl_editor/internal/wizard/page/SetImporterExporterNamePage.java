package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DmdlCompilerProperties;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen.ImporterExporterGenerator;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class SetImporterExporterNamePage extends WizardPage {
	private static final String SETTINGS_SRC = "SetImporterExporterNamePage.src";
	private static final String SETTINGS_PACKAGE = "SetImporterExporterNamePage.package";

	private List<ImporterExporterGenerator> generatorList;

	private Text srcText;
	private Text packageText;
	private List<Field> fieldList = new ArrayList<Field>();
	private ModifyListener listener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			validate(true);
		}
	};

	public SetImporterExporterNamePage() {
		super("SetImporterExporterNamePage");
		setTitle("Importer/Exporterクラス名の指定");
		setDescription("作成するImporter/Exporterの種類を選択し、生成するクラス名を入力して下さい。");
	}

	public void setGenerators(List<ImporterExporterGenerator> genList) {
		this.generatorList = genList;
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData compositeGrid = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(compositeGrid);

		{
			Label label = new Label(composite, SWT.NONE);
			label.setText("生成先ソースディレクトリー");

			srcText = new Text(composite, SWT.BORDER);
			GridData grid = new GridData(GridData.FILL_HORIZONTAL);
			srcText.setLayoutData(grid);
			srcText.setText(getSetting(SETTINGS_SRC, "src/main/java"));
			srcText.addModifyListener(listener);
		}
		{
			Label label = new Label(composite, SWT.NONE);
			label.setText("パッケージ名");

			packageText = new Text(composite, SWT.BORDER);
			GridData grid = new GridData(GridData.FILL_HORIZONTAL);
			packageText.setLayoutData(grid);
			packageText.setText(getSetting(SETTINGS_PACKAGE, ""));
			packageText.addModifyListener(listener);
		}

		final Table table = new Table(composite, SWT.SINGLE | SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		{
			GridData data = new GridData(GridData.FILL_BOTH);
			data.horizontalSpan = 2;
			data.heightHint = 18 * 7;
			table.setLayoutData(data);
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			TableColumn col0 = new TableColumn(table, SWT.LEFT);
			col0.setText("type");
			col0.setWidth(256);
			TableColumn col1 = new TableColumn(table, SWT.LEFT);
			col1.setText("class name");
			col1.setWidth(512);

			for (ImporterExporterGenerator gen : generatorList) {
				createField(table, gen);
			}
			TableViewer viewer = new TableViewer(table);
			viewer.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					validate(true);
				}
			});
			table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					Point point = new Point(e.x, e.y);
					TableItem item = table.getItem(point);
					if (item != null) {
						if (item.getBounds(0).contains(point)) {
							item.setChecked(!item.getChecked());
							validate(true);
						} else if (item.getBounds(COL_CLASS_NAME).contains(point)) {
							createTableEditor(table, item);
						}
					}
				}
			});
		}

		{
			Group group = new Group(composite, SWT.NONE);
			group.setText("例");
			GridData grid = new GridData(GridData.FILL_HORIZONTAL);
			grid.horizontalSpan = 2;
			group.setLayoutData(grid);
			group.setLayout(new GridLayout(1, false));

			Label label = new Label(group, SWT.NONE);
			label.setText("対象となるデータモデル（前のページで指定したデータモデル）が「word_count」、\n"
					+ "「パッケージ名」が「com.example」、「@directio.csv Importer」が「job.$(modelName.toCamelCase)FromCsv」のとき、\n"
					+ "「com.example.job.WordCountFromCsv」というクラスが生成されます。\n"
					+ "親クラスは、build.proerptiesの「asakusa.modelgen.package」で指定されているパッケージが「com.modelgen」だとすると\n"
					+ "「com.modelgen.dmdl.csv.AbstractWordCountCsvInputDescription」となります。（このクラスが無いと、生成されたソースはコンパイルエラーになります）");
		}

		validate(false);
		setControl(composite);
	}

	private static final int COL_CLASS_NAME = 1;

	private static class Field {
		public ImporterExporterGenerator generator;
		public TableItem item;
	}

	private void createField(Table table, ImporterExporterGenerator gen) {
		Field f = new Field();
		f.generator = gen;

		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(0, gen.getDisplayName());
		item.setText(COL_CLASS_NAME, nonNull(getSetting(gen, gen.getDefaultClassName())));
		item.setChecked(getSettingBoolean(gen));
		f.item = item;

		fieldList.add(f);
	}

	private void createTableEditor(Table table, final TableItem item) {
		final Text text = new Text(table, SWT.NONE);
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				item.setText(COL_CLASS_NAME, text.getText());
				text.dispose();
			}
		});
		text.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				switch (e.detail) {
				case SWT.TRAVERSE_RETURN:
					item.setText(COL_CLASS_NAME, text.getText());
					text.dispose();
					break;
				case SWT.TRAVERSE_ESCAPE:
					text.dispose();
					e.doit = false;
					break;
				}
			}
		});

		TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 64;
		editor.setEditor(text, item, COL_CLASS_NAME);

		text.setText(item.getText(COL_CLASS_NAME));
		// text.selectAll();
		text.setFocus();
	}

	private void validate(boolean setError) {
		setPageComplete(false);
		if (srcText.getText().trim().isEmpty()) {
			if (setError) {
				setErrorMessage("生成先ソースディレクトリーを入力して下さい。");
			}
			return;
		}
		if (packageText.getText().trim().isEmpty()) {
			if (setError) {
				setErrorMessage("パッケージ名を入力して下さい。");
			}
			return;
		}
		int checked = 0;
		for (Field f : fieldList) {
			boolean check = f.item.getChecked();
			if (check) {
				checked++;
				String value = f.item.getText(COL_CLASS_NAME).trim();
				if (value.isEmpty()) {
					if (setError) {
						setErrorMessage("選択した種類のクラス名を入力して下さい。");
					}
					return;
				}
			}
		}
		if (checked == 0) {
			if (setError) {
				setErrorMessage("Importer/Exporterの種類を選択して下さい。");
			}
			return;
		}

		setErrorMessage(null);
		setPageComplete(true);
	}

	public void setProperties(DmdlCompilerProperties properties) {
		if (properties != null && packageText.getText().isEmpty()) {
			String s = getSetting(SETTINGS_PACKAGE, null);
			if (s == null) {
				s = properties.getPackageDefault();
				packageText.setText(nonNull(s));
			}
		}
	}

	public String getSrcDirectory() {
		String value = srcText.getText().trim();
		setSetting(SETTINGS_SRC, value);
		return value;
	}

	public String getPackageName() {
		String value = packageText.getText().trim();
		setSetting(SETTINGS_PACKAGE, value);
		return value;
	}

	public Map<ImporterExporterGenerator, String> getClassName() {
		Map<ImporterExporterGenerator, String> map = new HashMap<ImporterExporterGenerator, String>();
		for (Field f : fieldList) {
			boolean check = f.item.getChecked();
			String value = f.item.getText(COL_CLASS_NAME).trim();

			setSetting(f.generator, check);
			setSetting(f.generator, value);

			if (check) {
				map.put(f.generator, value);
			}
		}
		return map;
	}

	// DialogSettings
	private String getSetting(String key, String defalutValue) {
		IDialogSettings settings = getDialogSettings();
		String value = settings.get(key);
		return (value != null) ? value : defalutValue;
	}

	private void setSetting(String key, String value) {
		IDialogSettings settings = getDialogSettings();
		settings.put(key, value);
	}

	private boolean getSettingBoolean(ImporterExporterGenerator gen) {
		IDialogSettings settings = getDialogSettings();
		return settings.getBoolean(getKey(gen, "checked"));
	}

	private void setSetting(ImporterExporterGenerator gen, boolean value) {
		IDialogSettings settings = getDialogSettings();
		settings.put(getKey(gen, "checked"), value);
	}

	private String getSetting(ImporterExporterGenerator gen, String defalutValue) {
		IDialogSettings settings = getDialogSettings();
		String value = settings.get(getKey(gen, "text"));
		return (value != null) ? value : defalutValue;
	}

	private void setSetting(ImporterExporterGenerator gen, String value) {
		IDialogSettings settings = getDialogSettings();
		settings.put(getKey(gen, "text"), value);
	}

	private String getKey(ImporterExporterGenerator gen, String suffix) {
		return String.format("SetImporterExporterNamePage.%s.%s", gen.getFullName(), suffix);
	}

	protected static String nonNull(String s) {
		return (s != null) ? s : "";
	}
}
