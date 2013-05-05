package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen;

public abstract class DirectioGenerator extends ImporterExporterGenerator {

	protected final void appendMethodBasePath(StringBuilder sb) {
		appendMethod(sb, "getBasePath", page.getBasePath());
	}

	protected final void appendMethodResourcePattern(StringBuilder sb) {
		appendMethod(sb, "getResourcePattern", page.getResourcePattern());
	}

	protected final void appendMethodOrder(StringBuilder sb) {
		appendMethodList(sb, "getOrder", page.getOrder());
	}

	protected final void appendMethodDeletePatterns(StringBuilder sb) {
		appendMethodList(sb, "getDeletePatterns", page.getDeletePatterns());
	}
}
