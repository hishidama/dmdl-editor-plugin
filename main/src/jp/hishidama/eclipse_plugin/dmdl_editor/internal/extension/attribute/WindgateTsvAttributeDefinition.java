package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.attribute;

import java.util.Arrays;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLAttributeWizardDefinition;

// http://asakusafw.s3.amazonaws.com/documents/sandbox/ja/html/windgate/stream.html
public class WindgateTsvAttributeDefinition extends DMDLAttributeWizardDefinition {

	@Override
	public String getName() {
		return "windgate.tsv";
	}

	@Override
	public String getAddModelAttribute() {
		return "@windgate.stream_format(type=\"tsv\")";
	}

	@Override
	public String getAddPropertyAttribute() {
		return "";
	}

	@Override
	protected List<String> getRemoveModelAttributeList() {
		return Arrays.asList("@windgate.stream_format");
	}

	@Override
	protected List<String> getRemovePropertyAttributeList() {
		return Arrays.asList("");
	}
}
