package jp.hishidama.eclipse_plugin.dmdl_editor.editors.preference;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import static jp.hishidama.eclipse_plugin.dmdl_editor.editors.preference.PreferenceConst.*;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DMDLEditorFormatterPreferencePage extends
		FieldEditorPreferencePage implements IWorkbenchPreferencePage {

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

		IntegerFieldEditor indentArgument = new IntegerFieldEditor(
				FORMAT_INDENT_ARGUMENT, "indent(property argument)", parent);
		indentArgument.setValidRange(1, 8);
		addField(indentArgument);

		IntegerFieldEditor indentProperty = new IntegerFieldEditor(
				FORMAT_INDENT_PROPERTY, "intden(block)", parent);
		indentProperty.setValidRange(1, 8);
		addField(indentProperty);
	}
}
