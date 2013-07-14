package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen;

public class DirectioCsvImporterGenerator extends DirectioGenerator {

	@Override
	protected String getExtendsClassName(String modelCamelName) {
		String sname = String.format("Abstract%sCsvInputDescription", modelCamelName);
		return getGeneratedClassName(".dmdl.csv.", sname);
	}

	@Override
	protected void appendMethods(StringBuilder sb) {
		appendMethodBasePath(sb);
		appendMethodResourcePattern(sb);
		appendMethodDataSize(sb);
	}
}