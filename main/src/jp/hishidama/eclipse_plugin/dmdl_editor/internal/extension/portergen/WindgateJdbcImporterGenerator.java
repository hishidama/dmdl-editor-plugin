package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.portergen;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.WindgateGenerator;

public class WindgateJdbcImporterGenerator extends WindgateGenerator {

	@Override
	public String getName() {
		return "@windgate.jdbc";
	}

	@Override
	public boolean isExporter() {
		return false;
	}

	@Override
	public String getDefaultClassName() {
		return "$(modelName.toCamelCase)FromTable";
	}

	@Override
	public void initializeFields() {
		addFieldWindgate();
		addFieldWindgateJdbc();
		addFieldWindgateJdbcImporter();
		addFieldImporterDataSize();
	}

	@Override
	protected String getExtendsClassName(String modelCamelName) {
		String sname = String.format("Abstract%sJdbcImporterDescription", modelCamelName);
		return getGeneratedClassName(".dmdl.jdbc.", sname);
	}

	@Override
	protected void appendMethods(StringBuilder sb) {
		appendMethodProfileName(sb);
		appendMethodTableName(sb);
		appendMethodColumnNames(sb);
		appendMethodCondition(sb);
		appendMethodDataSize(sb);
	}
}
