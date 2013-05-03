package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference;

import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.FORMAT_INDENT_ARGUMENT;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.FORMAT_INDENT_PROPERTY;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.format.DMDLContentFormatter;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.DMDLSimpleParser;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.DocumentScanner;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelList;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DMDLEditorFormatterPreferencePage extends
		FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private IntegerFieldEditor indentArgument;
	private IntegerFieldEditor indentProperty;

	public DMDLEditorFormatterPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		indentArgument = new IntegerFieldEditor(FORMAT_INDENT_ARGUMENT,
				"indent(property argument)", parent);
		indentArgument.setValidRange(1, 8);
		addField(indentArgument);

		indentProperty = new IntegerFieldEditor(FORMAT_INDENT_PROPERTY,
				"indent(block)", parent);
		indentProperty.setValidRange(1, 8);
		addField(indentProperty);
	}

	protected static final String TEXT = "@test(name=\"zzz\")\n"
			+ "@test(name=\"aaa\", value=123)\n" + "model={\n" + "@test2\n"
			+ "@test2(name=\"zzz\")\n" + "@test2(name=\"aaa\",value=456)\n"
			+ "value1:INT;\n" + "};\n";
	private Text simulationText;

	@Override
	protected Control createContents(Composite parent) {
		Control body = super.createContents(parent);

		simulationText = new Text(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER | SWT.READ_ONLY);
		GridData gd = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL);
		// gd.horizontalSpan = 2;
		simulationText.setLayoutData(gd);
		initText();

		return body;
	}

	private void initText() {
		if (isValid()) {
			DMDLDocument document = new DMDLDocument();
			document.set(TEXT);
			DMDLSimpleParser parser = new DMDLSimpleParser();
			ModelList models = parser.parse(new DocumentScanner(document));
			document.setModelList(models);

			DMDLContentFormatter formatter = new DMDLContentFormatter();
			formatter.setSimulate(PreferenceConst.FORMAT_INDENT_ARGUMENT,
					indentArgument.getIntValue());
			formatter.setSimulate(PreferenceConst.FORMAT_INDENT_PROPERTY,
					indentProperty.getIntValue());

			Region region = new Region(0, document.getLength());
			formatter.format(document, region);
			simulationText.setText(document.get());
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event); // checkState()が呼ばれるので、isValid()が使えるようになる
		initText();
	}
}
