package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.attribute;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLAttributeWizardDefinition;

public class DirectioSeqfileAttributeDefinition extends DMDLAttributeWizardDefinition {

	@Override
	public String getName() {
		return "directio.sequence_file";
	}

	@Override
	public String getAddModelAttribute() {
		return "@directio.sequence_file\n";
	}

	@Override
	public String getAddPropertyAttribute() {
		return null;
	}

	@Override
	protected List<String> getRemoveModelAttributeList() {
		return Arrays.asList("@directio.sequence_file");
	}

	@Override
	protected List<String> getRemovePropertyAttributeList() {
		return Collections.emptyList();
	}
}
