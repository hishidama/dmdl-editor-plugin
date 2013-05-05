package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen;

public abstract class WindgateGenerator extends ImporterExporterGenerator {

	protected final void appendMethodProfileName(StringBuilder sb) {
		appendMethod(sb, "getProfileName", page.getProfileName());
	}

	protected final void appendMethodPath(StringBuilder sb) {
		appendMethod(sb, "getPath", page.getPath());
	}

	protected final void appendMethodTableName(StringBuilder sb) {
		String name = page.getTableName();
		if (!name.trim().isEmpty()) {
			appendMethod(sb, "getTableName", name);
		}
	}

	protected final void appendMethodColumnNames(StringBuilder sb) {
		appendMethodList(sb, "getColumnNames", page.getColumnNames());
	}

	protected final void appendMethodCondition(StringBuilder sb) {
		String cond = page.getCondition();
		if (cond.trim().isEmpty()) {
			appendMethodNull(sb, "getCondition");
		} else {
			appendMethod(sb, "getCondition", cond);
		}
	}
}
