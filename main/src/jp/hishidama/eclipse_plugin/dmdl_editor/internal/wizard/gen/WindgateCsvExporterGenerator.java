package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen;

public class WindgateCsvExporterGenerator extends WindgateGenerator {

	@Override
	public String getName() {
		return "@windgate.csv";
	}

	@Override
	public boolean isExporter() {
		return true;
	}

	@Override
	public String getDefaultClassName() {
		return "$(modelName.toCamelCase)ToCsv";
	}

	@Override
	public void initializeFields() {
		addWindgate();
		addWindgateCsv();
	}

	@Override
	protected String getExtendsClassName(String modelCamelName) {
		String sname = String.format("Abstract%sCsvExporterDescription", modelCamelName);
		return getGeneratedClassName(".dmdl.csv.", sname);
	}

	@Override
	protected void appendMethods(StringBuilder sb) {
		appendMethodProfileName(sb);
		appendMethodPath(sb);
	}
}
