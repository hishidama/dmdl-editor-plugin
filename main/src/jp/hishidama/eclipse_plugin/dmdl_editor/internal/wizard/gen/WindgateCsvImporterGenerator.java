package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen;

public class WindgateCsvImporterGenerator extends WindgateGenerator {

	@Override
	protected String getExtendsClassName(String modelCamelName) {
		String sname = String.format("Abstract%sCsvImporterDescription", modelCamelName);
		return getGeneratedClassName(".dmdl.csv.", sname);
	}

	@Override
	protected void appendMethods(StringBuilder sb) {
		appendMethodProfileName(sb);
		appendMethodPath(sb);
		appendMethodDataSize(sb);
	}
}
