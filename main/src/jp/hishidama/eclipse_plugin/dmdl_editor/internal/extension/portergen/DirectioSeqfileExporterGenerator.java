package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.portergen;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DirectioGenerator;

public class DirectioSeqfileExporterGenerator extends DirectioGenerator {

	@Override
	public String getName() {
		return "@directio.sequence_file";
	}

	@Override
	public boolean isExporter() {
		return true;
	}

	@Override
	public String getDefaultClassName() {
		return "$(modelName.toCamelCase)ToSeq";
	}

	@Override
	public void initializeFields() {
		addFieldDirectioSeqfile();
		addFieldDirectioSeqfileExporter();
	}

	@Override
	protected String getExtendsClassName(String modelCamelName) {
		String sname = String.format("Abstract%sSequenceFileOutputDescription", modelCamelName);
		return getGeneratedClassName(".dmdl.sequencefile.", sname);
	}

	@Override
	protected void appendMethods(StringBuilder sb) {
		appendMethodBasePath(sb);
		appendMethodResourcePattern(sb);
		appendMethodOrder(sb);
		appendMethodDeletePatterns(sb);
	}
}
