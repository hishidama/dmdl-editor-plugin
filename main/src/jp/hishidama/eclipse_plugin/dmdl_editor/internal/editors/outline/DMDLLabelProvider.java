package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.outline;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.DMDLMultiPageEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index.IndexContainer;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DMDLImages;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

public class DMDLLabelProvider extends StyledCellLabelProvider {

	private DMDLMultiPageEditor editor;

	public DMDLLabelProvider(DMDLMultiPageEditor editor) {
		this.editor = editor;
	}

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

		Image image = DMDLImages.getDataModelImage(model);
		cell.setImage(image);
	}

	protected void update(ViewerCell cell, PropertyToken prop) {
		StyledString styledString = new StyledString(prop.getPropertyName());

		IndexContainer ic = IndexContainer.getContainer(editor.getProject(), null);
		String type = prop.getDataType(ic);
		if (type != null) {
			styledString.append(" : " + type, StyledString.DECORATIONS_STYLER);
		}

		cell.setText(styledString.toString());
		cell.setImage(DMDLImages.getPropertyImage());
		cell.setStyleRanges(styledString.getStyleRanges());
	}
}
