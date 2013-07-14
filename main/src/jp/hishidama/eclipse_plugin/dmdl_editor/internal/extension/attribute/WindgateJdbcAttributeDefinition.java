package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.attribute;

import java.util.Arrays;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLAttributeWizardDefinition;

public class WindgateJdbcAttributeDefinition extends DMDLAttributeWizardDefinition {

	@Override
	public String getName() {
		return "windgate.jdbc";
	}

	@Override
	public String getAddModelAttribute() {
		return "@windgate.jdbc.table(name = \"$(modelName.toUpper)\")";
	}

	@Override
	public String getAddPropertyAttribute() {
		return "@windgate.jdbc.column(name = \"$(name.toUpper)\")";
	}

	@Override
	protected List<String> getRemoveModelAttributeList() {
		return Arrays.asList("@windgate.jdbc.table");
	}

	@Override
	protected List<String> getRemovePropertyAttributeList() {
		return Arrays.asList("@windgate.jdbc.column");
	}
}
