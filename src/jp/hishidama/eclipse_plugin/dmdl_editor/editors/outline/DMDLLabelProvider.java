package jp.hishidama.eclipse_plugin.dmdl_editor.editors.outline;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class DMDLLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
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
