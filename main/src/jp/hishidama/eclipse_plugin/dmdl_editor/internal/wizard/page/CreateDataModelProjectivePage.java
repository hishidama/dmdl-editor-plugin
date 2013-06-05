package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

public class CreateDataModelProjectivePage extends CreateDataModelNormalPage {

	public CreateDataModelProjectivePage() {
		super("CreateDataModelProjectivePage", "射影データモデルの作成", "射影データモデルを定義して下さい。");
	}

	@Override
	protected String getCopyToolTipText() {
		return "データモデルのプロパティーをコピーします。\n" + "例えばp1とp2をコピーすると、\n" + "projective model = { p1:TYPE; p2:TYPE; }\n"
				+ "となります。";
	}

	@Override
	protected String getReferenceToolTipText() {
		return "データモデルの参照を作成します。\n" + "例えばm1とm2を参照すると、\n" + "projective model = m1 + m2;\n" + "となります。";
	}

	@Override
	protected String getModelType() {
		return "projective";
	}
}
