package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.port;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.WindgateDefinition;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen.ImporterExporterGenerator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen.WindgateJdbcExporterGenerator;

public class WindgateJdbcExporterDefinition extends WindgateDefinition {

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
		return "$(modelName.toCamelCase)ToTable";
	}

	@Override
	public void initializeFields() {
		addWindgate();
		addWindgateJdbc();
	}

	@Override
	public ImporterExporterGenerator getGenerator() {
		return new WindgateJdbcExporterGenerator();
	}
}
