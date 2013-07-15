package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.port;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DirectioDefinition;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen.DirectioCsvImporterGenerator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen.ImporterExporterGenerator;

public class DirectioCsvImporterDefinition extends DirectioDefinition {

	@Override
	public String getName() {
		return "@directio.csv";
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
		addDirectioCsv();
		addImporterDataSize();
	}
	@Override
	public ImporterExporterGenerator getGenerator() {
		return new DirectioCsvImporterGenerator();
	}

}
