package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.style;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class ColorManager {
	private ISharedTextColors colors;

	public ColorManager(ISharedTextColors colors) {
		this.colors = colors;
	}

	public Color getDefaultColor() {
		return getColor(new RGB(0, 0, 0));
	}

	public Color getColor(String key) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String s = store.getString(key);
		RGB rgb = StringConverter.asRGB(s);
		return getColor(rgb);
	}

	private Color getColor(RGB rgb) {
		return colors.getColor(rgb);
	}
}
