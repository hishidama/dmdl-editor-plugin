package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.WindgateDefinition;

public abstract class WindgateGenerator extends ImporterExporterGenerator {

	protected final void appendMethodProfileName(StringBuilder sb) {
		appendMethod(sb, "getProfileName", map.get(WindgateDefinition.KEY_PROFILE_NAME));
	}

	protected final void appendMethodPath(StringBuilder sb) {
		appendMethod(sb, "getPath", map.get(WindgateDefinition.KEY_PATH));
	}

	protected final void appendMethodTableName(StringBuilder sb) {
		String name = map.get(WindgateDefinition.KEY_TABLE_NAME);
		if (!name.trim().isEmpty()) {
			appendMethod(sb, "getTableName", name);
		}
	}

	protected final void appendMethodColumnNames(StringBuilder sb) {
		appendMethodList(sb, "getColumnNames", map.get(WindgateDefinition.KEY_COLUMN_NAMES));
	}

	protected final void appendMethodCondition(StringBuilder sb) {
		String cond = map.get(WindgateDefinition.KEY_CONDITION);
		if (cond.trim().isEmpty()) {
			appendMethodNull(sb, "getCondition");
		} else {
			appendMethod(sb, "getCondition", cond);
		}
	}
}
