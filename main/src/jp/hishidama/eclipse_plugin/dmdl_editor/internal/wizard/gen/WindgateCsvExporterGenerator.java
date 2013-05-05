package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen;


public class WindgateCsvExporterGenerator extends WindgateGenerator {

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
