package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.port;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.WindgateDefinition;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen.ImporterExporterGenerator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen.WindgateCsvImporterGenerator;

public class WindgateJdbcImporterDefinition extends WindgateDefinition {

	@Override
	public String getName() {
		return "@windgate.csv";
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
		addWindgate();
		addWindgateJdbc();
		addWindgateJdbcImporter();
		addImporterDataSize();
	}

	@Override
	public ImporterExporterGenerator getGenerator() {
		return new WindgateCsvImporterGenerator();
	}
}
