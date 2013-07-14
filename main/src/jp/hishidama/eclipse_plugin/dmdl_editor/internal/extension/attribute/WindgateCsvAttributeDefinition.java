package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLAttributeWizardDefinition;

public class WindgateCsvAttributeDefinition extends DMDLAttributeWizardDefinition {

	@Override
	public String getName() {
		return "windgate.csv";
	}

	@Override
	public String getAddModelAttribute() {
		return "@windgate.csv(\n" //
				+ "  charset = \"UTF-8\",\n" //
				+ "  has_header = FALSE,\n" //
				+ "  true = \"true\",\n" //
				+ "  false = \"false\",\n" //
				+ "  date = \"yyyy-MM-dd\",\n" //
				+ "  datetime = \"yyyy-MM-dd HH:mm:ss\",\n" //
				+ ")";
	}

	@Override
	public String getAddPropertyAttribute() {
		return "@windgate.csv.field(name = \"$(name)\")";
	}

	@Override
	protected List<String> getRemoveModelAttributeList() {
		return Arrays.asList("@windgate.csv");
	}

	@Override
	protected List<String> getRemovePropertyAttributeList() {
		List<String> r = new ArrayList<String>();
		r.add("@windgate.csv.field");
		r.add("@windgate.csv.file_name");
		r.add("@windgate.csv.line_number");
		r.add("@windgate.csv.record_number");
		return r;
	}
}
