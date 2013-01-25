package jp.hishidama.eclipse_plugin.dmdl_editor.editors.outline;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLImages;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class DMDLLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		if (element instanceof ModelToken) {
			ImageRegistry registry = Activator.getDefault().getImageRegistry();
			Image image = registry.get(DMDLImages.MODEL_IMANE);
			return image;
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof ModelToken) {
			ModelToken token = (ModelToken) element;
			return token.getModelName();
		}
		if (element instanceof PropertyToken) {
			PropertyToken token = (PropertyToken) element;
			String name = token.getPropertyName();
			String type = token.getDataType();
			if (type == null) {
				return name;
			}
			return name + " : " + type;
		}
		return element.toString();
	}
}
