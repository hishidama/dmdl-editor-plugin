package jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.outline;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLImages;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.index.IndexContainer;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;

import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

public class DMDLLabelProvider extends StyledCellLabelProvider {

	private DMDLEditor editor;

	public DMDLLabelProvider(DMDLEditor editor) {
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

		String modelType = model.getModelType();
		Image image = getModelImage(modelType);
		cell.setImage(image);
	}

	protected Image getModelImage(String modelType) {
		if ("summarized".equals(modelType) || "joined".equals(modelType)
				|| "projective".equals(modelType)) {
			ImageRegistry registry = Activator.getDefault().getImageRegistry();
			return registry.get(DMDLImages.MODEL_IMAGE + modelType);
		} else {
			return JavaUI.getSharedImages().getImage(
					ISharedImages.IMG_OBJS_CLASS);
		}
	}

	protected void update(ViewerCell cell, PropertyToken prop) {
		StyledString styledString = new StyledString(prop.getPropertyName());

		IndexContainer ic = IndexContainer.getContainer(editor.getProject(),
				editor.getFile());
		String type = prop.getDataType(ic);
		if (type != null) {
			styledString.append(" : " + type, StyledString.DECORATIONS_STYLER);
		}

		cell.setText(styledString.toString());
		cell.setStyleRanges(styledString.getStyleRanges());
	}
}
