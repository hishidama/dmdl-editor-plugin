package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen;

public class DirectioCsvExporterGenerator extends DirectioGenerator {

	@Override
	protected String getExtendsClassName(String modelCamelName) {
		String sname = String.format("Abstract%sCsvOutputDescription", modelCamelName);
		return getGeneratedClassName(".dmdl.csv.", sname);
	}

	@Override
	protected void appendMethods(StringBuilder sb) {
		appendMethodBasePath(sb);
		appendMethodResourcePattern(sb);
		appendMethodOrder(sb);
		appendMethodDeletePatterns(sb);
	}
}
