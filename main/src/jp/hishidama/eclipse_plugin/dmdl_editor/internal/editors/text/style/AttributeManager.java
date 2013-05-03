package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.style;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.graphics.Color;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst.*;

public class AttributeManager {
	private ColorManager colorManager;

	/**
	 * コンストラクター.
	 *
	 * @param colorManager
	 */
	public AttributeManager(ColorManager colorManager) {
		this.colorManager = colorManager;
	}

	public TextAttribute getDefaultAttribute() {
		Color color = colorManager.getDefaultColor();
		return new TextAttribute(color);
	}

	public TextAttribute getCommentAttribute() {
		return createAttribute(COLOR_COMMENT, STYLE_COMMENT);
	}

	public TextAttribute getAnnotationAttribute() {
		return createAttribute(COLOR_ANNOTATION, STYLE_ANNOTATION);
	}

	public TextAttribute getDescriptionAttribute() {
		return createAttribute(COLOR_DESCRIPTION, STYLE_DESCRIPTION);
	}

	public TextAttribute getModelTypeAttribute() {
		return createAttribute(COLOR_MODEL_TYPE, STYLE_MODEL_TYPE);
	}

	public TextAttribute getDataTypeAttribute() {
		return createAttribute(COLOR_DATA_TYPE, STYLE_DATA_TYPE);
	}

	public TextAttribute getSumTypeAttribute() {
		return createAttribute(COLOR_SUM_TYPE, STYLE_SUM_TYPE);
	}

	protected TextAttribute createAttribute(String colorKey, String styleKey) {
		Color color = getColor(colorKey);
		int style = getStyle(styleKey);
		return new TextAttribute(color, null, style);
	}

	protected Color getColor(String key) {
		return colorManager.getColor(key);
	}

	protected int getStyle(String key) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		return store.getInt(key);
	}
}
