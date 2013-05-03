package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DMDLEditorPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	public DMDLEditorPreferencePage() {
		super("DMDLEditorPreferencePage");
		noDefaultAndApplyButton();
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		setTitle("Asakusa Framework DMDL editor");

		Composite composite = new Composite(parent, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 2; // 列数
			composite.setLayout(layout);
		}

		return composite;
	}
}
