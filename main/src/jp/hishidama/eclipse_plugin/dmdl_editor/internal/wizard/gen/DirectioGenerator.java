package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DirectioDefinition;

public abstract class DirectioGenerator extends ImporterExporterGenerator {

	protected final void appendMethodBasePath(StringBuilder sb) {
		appendMethod(sb, "getBasePath", map.get(DirectioDefinition.KEY_BASE_PATH));
	}

	protected final void appendMethodResourcePattern(StringBuilder sb) {
		appendMethod(sb, "getResourcePattern", map.get(DirectioDefinition.KEY_RESOURCE_PATTERN));
	}

	protected final void appendMethodOrder(StringBuilder sb) {
		appendMethodList(sb, "getOrder", map.get(DirectioDefinition.KEY_ORDER));
	}

	protected final void appendMethodDeletePatterns(StringBuilder sb) {
		appendMethodList(sb, "getDeletePatterns", map.get(DirectioDefinition.KEY_DELETE_PATTERN));
	}
}
