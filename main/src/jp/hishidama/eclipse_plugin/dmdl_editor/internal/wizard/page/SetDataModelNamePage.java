package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.dialog.DmdlFileSelectionDialog;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelUtil;
import jp.hishidama.eclipse_plugin.util.FileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SetDataModelNamePage extends WizardPage {
	private IProject project;
	private String path;

	public static enum PositionType {
		FILE_FIRST, FILE_FIRST_COMMENT, FILE_LAST, DM_BEFORE, DM_REPLACE, DM_AFTER
	}

	private Text file;
	private List<Button> positionList = new ArrayList<Button>();
	private Text positionModelName;
	private Text desc;
	private Text modelName;
	private List<Field> fieldList = new ArrayList<Field>();
	private ModifyListener listener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			validate(true);
		}
	};

	public SetDataModelNamePage(IProject project) {
		super("SetDataModelNamePage");
		this.project = project;

		setTitle("データモデル名の指定");
		setDescription("作成するデータモデルの名前と種類を入力して下さい。");
	}

	public void setDmdlFile(String path) {
		this.path = path;
		if (file != null) {
			file.setText(path);
		}
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		{
			composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			composite.setLayout(new GridLayout(3, false));
		}

		{
			Label label = new Label(composite, SWT.NONE);
			label.setText("作成先ファイル名");

			file = new Text(composite, SWT.BORDER);
			file.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			file.setText(path);
			file.addModifyListener(listener);

			Button button = new Button(composite, SWT.PUSH);
			button.setText("select");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					DmdlFileSelectionDialog dialog = new DmdlFileSelectionDialog(getShell(), project);
					if (dialog.open() == Window.OK) {
						IFile f = dialog.getSelectedFile();
						file.setText(f.getProjectRelativePath().toPortableString());
						String model = dialog.getSelectedModelName();
						if (model != null) {
							positionModelName.setText(model);
						}
					}
				}
			});
		}
		{
			Label label = new Label(composite, SWT.NONE);
			label.setText("作成する位置");

			Composite field = new Composite(composite, SWT.NONE);
			field.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			field.setLayout(new GridLayout(5, false));

			createPosition(field, "ファイルの先頭", PositionType.FILE_FIRST);
			createPosition(field, "ファイルの先頭（コメントの後）", PositionType.FILE_FIRST_COMMENT);
			createPosition(field, "ファイルの末尾", PositionType.FILE_LAST).setSelection(true);
			new Label(field, SWT.NONE); // dummy
			new Label(field, SWT.NONE); // dummy

			createPosition(field, "データモデルの前", PositionType.DM_BEFORE);
			createPosition(field, "データモデルを置換", PositionType.DM_REPLACE);
			createPosition(field, "データモデルの後", PositionType.DM_AFTER);
			{
				new Label(field, SWT.NONE).setText("データモデル");
				positionModelName = new Text(field, SWT.BORDER);
				positionModelName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				positionModelName.addModifyListener(listener);
			}

			new Label(composite, SWT.NONE); // dummy
		}
		{
			Label label = new Label(composite, SWT.NONE);
			label.setText("データモデル名");

			modelName = new Text(composite, SWT.BORDER);
			modelName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			modelName.setText("");
			modelName.addModifyListener(listener);

			new Label(composite, SWT.NONE); // dummy
		}
		{
			Label label = new Label(composite, SWT.NONE);
			label.setText("データモデルの説明");

			desc = new Text(composite, SWT.BORDER);
			desc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			desc.setText("");
			desc.addModifyListener(listener);

			new Label(composite, SWT.NONE); // dummy
		}
		{
			Label label = new Label(composite, SWT.NONE);
			label.setText("データモデルの種類");

			Composite table = new Composite(composite, SWT.NONE);
			table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			table.setLayout(new GridLayout(1, true));
			createField(table, DataModelType.NORMAL).setSelection(true);
			createField(table, DataModelType.SUMMARIZED);
			createField(table, DataModelType.JOINED);
			createField(table, DataModelType.PROJECTIVE);

			new Label(composite, SWT.NONE); // dummy
		}

		validate(false);
		setControl(composite);
	}

	private Button createPosition(Composite field, String text, PositionType pos) {
		Button button = new Button(field, SWT.RADIO);
		button.setText(text);
		button.setData(pos);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate(true);
			}
		});

		positionList.add(button);

		return button;
	}

	private Button createField(Composite composite, DataModelType type) {
		Button button = new Button(composite, SWT.RADIO);
		button.setText(type.displayName());

		Field field = new Field();
		field.button = button;
		field.type = type;
		fieldList.add(field);

		return button;
	}

	private static class Field {
		public Button button;
		public DataModelType type;
	}

	private void validate(boolean setError) {
		setPageComplete(false);

		String path = file.getText().trim();
		if (path.isEmpty()) {
			if (setError) {
				setErrorMessage("作成先ファイル名を入力して下さい。");
			}
			return;
		}
		IFile f = project.getFile(path);
		if (FileUtil.exists(f) && !FileUtil.isFile(f)) {
			if (setError) {
				setErrorMessage("作成先ファイル名にはファイルを入力して下さい。");
			}
			return;
		}

		PositionType pos = getPosition();
		if (pos.name().startsWith("DM_")) {
			String src = positionModelName.getText().trim();
			if (src.isEmpty()) {
				if (setError) {
					setErrorMessage("作成位置のデータモデル名を入力して下さい。");
				}
				return;
			}
		}

		String name = modelName.getText().trim();
		if (name.isEmpty()) {
			if (setError) {
				setErrorMessage("データモデル名を入力して下さい。");
			}
			return;
		}
		{
			String message = DataModelUtil.validateName("データモデル名", name);
			if (message != null) {
				if (setError) {
					setErrorMessage(message);
				}
				return;
			}
		}

		int checked = 0;
		for (Field field : fieldList) {
			if (field.button.getSelection()) {
				checked++;
			}
		}
		switch (checked) {
		case 0:
			if (setError) {
				setErrorMessage("データモデルの種類を選択して下さい。");
			}
			return;
		case 1: // 正常
			break;
		default:
			if (setError) {
				setErrorMessage("データモデルの種類を1つだけ選択して下さい。");
			}
			return;
		}

		setErrorMessage(null);
		setPageComplete(true);
	}

	public static class FilePosition {
		public String filePath;
		public PositionType position;
		public String modelName;
	}

	public FilePosition getDmdlFile() {
		FilePosition f = new FilePosition();
		f.filePath = file.getText().trim();
		f.position = getPosition();
		f.modelName = positionModelName.getText().trim();
		return f;
	}

	private PositionType getPosition() {
		for (Button button : positionList) {
			if (button.getSelection()) {
				return (PositionType) button.getData();
			}
		}
		return null;
	}

	public String getDataModelName() {
		String value = modelName.getText().trim();
		return value;
	}

	public String getDataModelDescription() {
		String value = desc.getText().trim();
		return value;
	}

	public DataModelType getDataModelType() {
		for (Field field : fieldList) {
			if (field.button.getSelection()) {
				return field.type;
			}
		}
		return DataModelType.NORMAL;
	}
}
