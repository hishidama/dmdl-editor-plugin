package jp.hishidama.eclipse_plugin.dmdl_editor.extension;

/**
 * DMDLコンパイラーを実行する為のプロパティー.
 *
 * @since 2013.06.14
 */
public abstract class DmdlCompilerProperties {

	/**
	 * デフォルトのパッケージ名.
	 *
	 * @return パッケージ名
	 */
	public abstract String getPackageDefault();

	/**
	 * DMDLのソースディレクトリー.
	 *
	 * @return ディレクトリーのパス
	 */
	public abstract String getDmdlDir();

	/**
	 * データモデルクラスの生成先ディレクトリー.
	 *
	 * @return ディレクトリーのパス
	 */
	public abstract String getModelgenOutput();

	/**
	 * データモデルクラスのパッケージ名.
	 *
	 * @return パッケージ名
	 */
	public abstract String getModelgenPackage();

	/**
	 * DMDLファイルのエンコーディング
	 *
	 * @return エンコーディング（null可）
	 */
	public abstract String getDmdlEncoding();
}
