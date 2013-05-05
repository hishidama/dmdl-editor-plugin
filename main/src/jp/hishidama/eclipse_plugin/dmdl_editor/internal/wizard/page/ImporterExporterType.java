package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

public enum ImporterExporterType {
	DIRECTIO_CSV_IMPORTER("@directio.csv Importer"), //
	DIRECTIO_CSV_EXPORTER("@directio.csv Exporter"), //
	WINDGATE_CSV_IMPORTER("@windgate.csv Importer"), //
	WINDGATE_CSV_EXPORTER("@windgate.csv Exporter"), //
	WINDGATE_JDBC_IMPORTER("@windgate.jdbc Importer"), //
	WINDGATE_JDBC_EXPORTER("@windgate.jdbc Exporter");

	private String display;

	private ImporterExporterType(String display) {
		this.display = display;
	}

	public String displayName() {
		return display;
	}
}
