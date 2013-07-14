package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLAttributeWizardDefinition;

public class DirectioCsvAttributeDefinition extends DMDLAttributeWizardDefinition {

	@Override
	public String getName() {
		return "directio.csv";
	}

	@Override
	public String getAddModelAttribute() {
		return "@directio.csv(\n" //
				+ "  charset = \"UTF-8\",\n" //
				+ "  allow_linefeed = FALSE,\n" //
				+ "  has_header = FALSE,\n" //
				+ "  true = \"true\",\n" //
				+ "  false = \"false\",\n" //
				+ "  date = \"yyyy-MM-dd\",\n" //
				+ "  datetime = \"yyyy-MM-dd HH:mm:ss\",\n" //
				+ ")";
	}

	@Override
	public String getAddPropertyAttribute() {
		return "@directio.csv.field(name = \"$(name)\")";
	}

	@Override
	protected List<String> getRemoveModelAttributeList() {
		return Arrays.asList("@directio.csv");
	}

	@Override
	protected List<String> getRemovePropertyAttributeList() {
		List<String> r = new ArrayList<String>();
		r.add("@directio.csv.field");
		r.add("@directio.csv.file_name");
		r.add("@directio.csv.line_number");
		r.add("@directio.csv.record_number");
		return r;
	}
}
