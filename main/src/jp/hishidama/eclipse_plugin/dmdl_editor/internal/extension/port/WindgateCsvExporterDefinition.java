package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.port;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.WindgateDefinition;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen.ImporterExporterGenerator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen.WindgateCsvExporterGenerator;

public class WindgateCsvExporterDefinition extends WindgateDefinition {

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
	public ImporterExporterGenerator getGenerator() {
		return new WindgateCsvExporterGenerator();
	}
}
