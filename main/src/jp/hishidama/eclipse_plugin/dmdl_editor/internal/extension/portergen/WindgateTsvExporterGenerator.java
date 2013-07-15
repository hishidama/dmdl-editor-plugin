package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.portergen;

public class WindgateTsvExporterGenerator extends WindgateTsvGenerator {

	@Override
	public boolean isExporter() {
		return true;
	}

	@Override
	public String getDefaultClassName() {
		return "$(modelName.toCamelCase)ToTsv";
	}

	@Override
	public void initializeFields() {
		addFieldWindgate();
		addFieldWindgatePath(GROUP_WINDGATE_TSV);
	}

	@Override
	protected String getExtendsClassName(String modelCamelName) {
		return "com.asakusafw.vocabulary.windgate.FsExporterDescription";
	}

	@Override
	protected void appendMethods(StringBuilder sb) {
		appendMethodModelType(sb);
		appendMethodStreamSupport(sb);
		appendMethodProfileName(sb);
		appendMethodPath(sb);
	}
}
