package jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.style;

import java.util.HashMap;
import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorManager {

	protected Map<RGB, Color> fColorTable = new HashMap<RGB, Color>();

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
		Color color = fColorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}

	public void dispose() {
		for (Color color : fColorTable.values()) {
			color.dispose();
		}
		fColorTable.clear();
	}
}
