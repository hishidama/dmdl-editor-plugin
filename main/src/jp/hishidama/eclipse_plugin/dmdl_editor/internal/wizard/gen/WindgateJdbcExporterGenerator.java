package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen;

public class WindgateJdbcExporterGenerator extends WindgateGenerator {

	@Override
	protected String getExtendsClassName(String modelCamelName) {
		String sname = String.format("Abstract%sJdbcExporterDescription", modelCamelName);
		return getGeneratedClassName(".dmdl.jdbc.", sname);
	}

	@Override
	protected void appendMethods(StringBuilder sb) {
		appendMethodProfileName(sb);
		appendMethodTableName(sb);
		appendMethodColumnNames(sb);
	}
}
