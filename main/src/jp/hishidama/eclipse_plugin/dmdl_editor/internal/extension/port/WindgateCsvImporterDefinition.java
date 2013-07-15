package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.port;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.WindgateDefinition;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen.ImporterExporterGenerator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen.WindgateCsvImporterGenerator;

public class WindgateCsvImporterDefinition extends WindgateDefinition {

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
		return "$(modelName.toCamelCase)FromCsv";
	}

	@Override
	public void initializeFields() {
		addWindgate();
		addWindgateCsv();
		addImporterDataSize();
	}

	@Override
	public ImporterExporterGenerator getGenerator() {
		return new WindgateCsvImporterGenerator();
	}
}
