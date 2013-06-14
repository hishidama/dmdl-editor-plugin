package jp.hishidama.eclipse_plugin.dmdl_editor.extension;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IProject;

/**
 * DMDL Editorの設定情報.
 *
 * @since 2013.06.14
 */
public abstract class DMDLEditorConfiguration {

	/**
	 * 設定名.
	 *
	 * @return 設定名
	 */
	public abstract String getConfigurationName();

	/**
	 * 該当プロジェクトを受け付けられるかどうか.
	 *
	 * @param project
	 *            プロジェクト
	 * @return true：受け付けられる
	 */
	public abstract boolean acceptable(IProject project);

	/**
	 * build.propertiesファイルのデフォルトの場所.
	 *
	 * @return ファイルのパス
	 */
	public abstract String getDefaultBuildPropertiesPath();

	/**
	 * DMDLのコンパイルに必要なjarファイル.
	 *
	 * @param project
	 *            プロジェクト
	 * @return ライブラリー一覧
	 */
	public abstract List<Library> getDefaultLibraries(IProject project);

	/**
	 * ライブラリー情報.
	 */
	public static class Library {
		/** ライブラリーファイルのパス */
		public String path;
		/** デフォルトの選択状態 */
		public boolean selected;

		/**
		 * コンストラクター.
		 *
		 * @param path
		 *            ライブラリーファイルのパス
		 * @param defaultSelected
		 *            デフォルトの選択状態
		 */
		public Library(String path, boolean defaultSelected) {
			this.path = path;
			this.selected = defaultSelected;
		}

		/**
		 * @see #valudOf(String)
		 */
		@Override
		public String toString() {
			return selected + "\0" + path;
		}

		/**
		 * インスタンス生成.
		 *
		 * @param s
		 *            String
		 * @return Library
		 * @see #toString()
		 */
		public static Library valudOf(String s) {
			String[] ss = s.split("\0");
			return new Library(ss[1], Boolean.valueOf(ss[0]));
		}
	}

	/**
	 * DMDLコンパイラー用プロパティー.
	 *
	 * @param project
	 *            プロジェクト
	 * @param propertyFilePath
	 *            設定で指定されているbuild.propertiesファイルのパス
	 * @return DMDLコンパイラー用プロパティー
	 * @throws IOException
	 *             build.propertiesの読み込みに関する何らかの例外
	 */
	public abstract DmdlCompilerProperties getCompilerProperties(IProject project, String propertyFilePath)
			throws IOException;
}
