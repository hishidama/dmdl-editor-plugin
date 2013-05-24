package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

public enum DataModelType {
	NORMAL, SUMMARIZED, JOINED, PROJECTIVE;

	public String displayName() {
		return name().toLowerCase();
	}
}
