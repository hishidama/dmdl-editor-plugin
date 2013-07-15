package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.portergen;

public class WindgateTsvImporterGenerator extends WindgateTsvGenerator {

	@Override
	public boolean isExporter() {
		return false;
	}

	@Override
	public String getDefaultClassName() {
		return "$(modelName.toCamelCase)FromTsv";
	}

	@Override
	public void initializeFields() {
		addFieldWindgate();
		addFieldWindgatePath(GROUP_WINDGATE_TSV);
		addFieldImporterDataSize();
	}

	@Override
	protected String getExtendsClassName(String modelCamelName) {
		return "com.asakusafw.vocabulary.windgate.FsImporterDescription";
	}

	@Override
	protected void appendMethods(StringBuilder sb) {
		appendMethodModelType(sb);
		appendMethodStreamSupport(sb);
		appendMethodProfileName(sb);
		appendMethodPath(sb);
		appendMethodDataSize(sb);
	}
}
