package jp.hishidama.eclipse_plugin.dmdl_editor.editors.outline;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLImages;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

public class DMDLLabelProvider extends StyledCellLabelProvider {

	@Override
	public void update(ViewerCell cell) {

		Object element = cell.getElement();
		if (element instanceof ModelToken) {
			ModelToken token = (ModelToken) element;
			update(cell, token);
		} else if (element instanceof PropertyToken) {
			PropertyToken token = (PropertyToken) element;
			update(cell, token);
		}

		super.update(cell);
	}

	protected void update(ViewerCell cell, ModelToken model) {
		StyledString styledString = new StyledString(model.getModelName());

		cell.setText(styledString.toString());
		cell.setStyleRanges(styledString.getStyleRanges());

		ImageRegistry registry = Activator.getDefault().getImageRegistry();
		Image image = registry.get(DMDLImages.MODEL_IMAGE);
		cell.setImage(image);
	}

	protected void update(ViewerCell cell, PropertyToken prop) {
		StyledString styledString = new StyledString(prop.getPropertyName());

		String type = prop.getDataType();
		if (type != null) {
			styledString.append(" : " + type, StyledString.DECORATIONS_STYLER);
		}

		cell.setText(styledString.toString());
		cell.setStyleRanges(styledString.getStyleRanges());
	}
}
