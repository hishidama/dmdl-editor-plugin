package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

public class DMDLPropertyPage extends PropertyPage {

	public DMDLPropertyPage() {
		noDefaultAndApplyButton();
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
