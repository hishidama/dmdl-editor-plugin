package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

public class CreateDataModelProjectivePage extends CreateDataModelNormalPage {

	public CreateDataModelProjectivePage() {
		super("CreateDataModelProjectivePage", "射影データモデルの作成", "射影データモデルを定義して下さい。");
	}

	@Override
	protected String getModelType() {
		return "projective";
	}
}
