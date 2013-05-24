package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.dialog.DmdlFileSelectionDialog;
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

	private Text file;
	private Text desc;
	private Text text;
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
					}
				}
			});
		}
		{
			Label label = new Label(composite, SWT.NONE);
			label.setText("データモデル名");

			text = new Text(composite, SWT.BORDER);
			text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			text.setText("");
			text.addModifyListener(listener);

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

		if (text.getText().trim().isEmpty()) {
			if (setError) {
				setErrorMessage("データモデル名を入力して下さい。");
			}
			return;
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

	public String getDmdlFile() {
		String value = file.getText().trim();
		return value;
	}

	public String getDataModelName() {
		String value = text.getText().trim();
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
