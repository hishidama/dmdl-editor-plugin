package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.portergen;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.WindgateGenerator;

public abstract class WindgateTsvGenerator extends WindgateGenerator {
	public static final String GROUP_WINDGATE_TSV = "@windgate.tsv";

	@Override
	public String getName() {
		return "@windgate.tsv";
	}

	protected void appendMethodStreamSupport(StringBuilder sb) {
		String sname = getSimpleName(getValue(KEY_MODEL_CLASS_NAME)) + "StreamSupport";
		String streamName = getCachedClassName(getGeneratedClassName(".dmdl.stream.", sname));
		String rtype = String.format("Class<%s>", streamName);
		String value = String.format("%s.class", streamName);
		appendMethod(sb, rtype, "getStreamSupport", value, "");
	}
}
