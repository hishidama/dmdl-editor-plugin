package jp.hishidama.eclipse_plugin.dmdl_editor.extension;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.AttributeWizard;
import jp.hishidama.eclipse_plugin.util.StringUtil;

/**
 * 属性追加削除ウィザードの定義.
 * 
 * @see AttributeWizard
 * @since 2013.07.14
 */
public abstract class DMDLAttributeWizardDefinition {

	/**
	 * 定義名取得.
	 * 
	 * @return 定義名（null不可）
	 */
	public abstract String getName();

	/**
	 * 属性追加時のモデルの属性.
	 * 
	 * @return モデルの属性
	 */
	public abstract String getAddModelAttribute();

	/**
	 * 属性追加時のプロパティーの属性.
	 * 
	 * @return プロパティーの属性
	 */
	public abstract String getAddPropertyAttribute();

	/**
	 * 属性削除時のモデルの属性.
	 * 
	 * @return モデルの属性（改行コード区切りで複数指定）
	 */
	public String getRemoveModelAttribute() {
		return StringUtil.toString(getRemoveModelAttributeList());
	}

	/**
	 * 属性削除時のモデルの属性.
	 * 
	 * @return モデルの属性
	 */
	protected abstract List<String> getRemoveModelAttributeList();

	/**
	 * 属性削除時のプロパティーの属性.
	 * 
	 * @return プロパティーの属性（改行コード区切りで複数指定）
	 */
	public String getRemovePropertyAttribute() {
		return StringUtil.toString(getRemovePropertyAttributeList());
	}

	/**
	 * 属性削除時のプロパティーの属性.
	 * 
	 * @return プロパティーの属性
	 */
	protected abstract List<String> getRemovePropertyAttributeList();
}
