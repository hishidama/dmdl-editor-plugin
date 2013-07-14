package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLAttributeWizardDefinition;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.update.AttributeRemover;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.update.AttributeUpdater;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class SetRemoveAttributePage extends SetAttributePage {

	public SetRemoveAttributePage() {
		super("SetRemoveAttributePage");
		setTitle("削除する属性の指定");
		setDescription("削除する属性の名前を指定して下さい。");
	}

	@Override
	protected void createNoteArea(Group group) {
		group.setLayout(new FillLayout(SWT.VERTICAL));

		Label label = new Label(group, SWT.NONE);
		label.setText("指定された属性名の属性を削除します。 削除する属性名は複数指定することが出来ます。");
	}

	@Override
	protected String getDefaultModelAttribute(DMDLAttributeWizardDefinition def) {
		return def.getRemoveModelAttribute();
	}

	@Override
	protected String getDefaultPropertyAttribute(DMDLAttributeWizardDefinition def) {
		return def.getRemovePropertyAttribute();
	}

	@Override
	public AttributeUpdater getUpdater(SelectAddRemovePage selectPage) {
		return new AttributeRemover();
	}
}
