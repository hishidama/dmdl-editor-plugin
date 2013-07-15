package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.port;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DirectioDefinition;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen.DirectioCsvExporterGenerator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen.ImporterExporterGenerator;

public class DirectioCsvExporterDefinition extends DirectioDefinition {

	@Override
	public String getName() {
		return "@directio.csv";
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
		addDirectioCsv();
		addDirectioCsvExporter();
	}

	@Override
	public ImporterExporterGenerator getGenerator() {
		return new DirectioCsvExporterGenerator();
	}
}
