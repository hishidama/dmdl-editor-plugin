package jp.hishidama.eclipse_plugin.dmdl_editor.viewer;

import java.text.MessageFormat;

import jp.hishidama.eclipse_plugin.dmdl_editor.util.DMDLImages;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelFile;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelInfo;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelProperty;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class DMDLLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		Object obj = element;
		if (element instanceof DMDLTreeData) {
			obj = ((DMDLTreeData) element).getData();
		}

		if (obj instanceof DataModelFile) {
			return DMDLImages.getDmdlFileImage();
		}
		if (obj instanceof DataModelInfo) {
			DataModelInfo info = (DataModelInfo) obj;
			return DMDLImages.getDataModelImage(info.getModelType());
		}
		if (obj instanceof DataModelProperty) {
			return DMDLImages.getPropertyImage();
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		Object obj = element;
		if (element instanceof DMDLTreeData) {
			obj = ((DMDLTreeData) element).getData();
		}

		if (obj instanceof DataModelFile) {
			DataModelFile file = (DataModelFile) obj;
			return file.getFilePath();
		}
		if (obj instanceof DataModelInfo) {
			DataModelInfo info = (DataModelInfo) obj;
			String name = info.getModelName();
			String desc = info.getModelDescription();
			String title = (desc != null) ? MessageFormat.format("{0} : {1}", name, desc) : name;
			return title;
		}
		if (obj instanceof DataModelProperty) {
			DataModelProperty prop = (DataModelProperty) obj;
			StringBuilder sb = new StringBuilder(64);
			{
				String name = prop.getName();
				sb.append(name);

				String desc = prop.getDescription();
				if (desc != null) {
					sb.append(" ");
					sb.append(desc);
				}

				String type = prop.getDataType();
				if (type != null) {
					sb.append(" : ");
					sb.append(type);
				}
			}
			return sb.toString();
		}
		return obj.toString();
	}
}
