package jp.hishidama.eclipse_plugin.dmdl_editor.util;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;

public class DMDLImages {

	private static final String DMDL_FILE = "dmdlFile";
	private static final String MODEL_IMAGE = "MODEL_IMAGE.";
	private static final String MODEL_SUM_IMAGE = MODEL_IMAGE + "summarized";
	private static final String MODEL_JOIN_IMAGE = MODEL_IMAGE + "joined";
	private static final String MODEL_PROJ_IMAGE = MODEL_IMAGE + "projective";

	public static void initializeImageRegistry(ImageRegistry reg) {
		reg.put(DMDLImages.DMDL_FILE, Activator.getImageDescriptor("/icons/hishidama16.gif"));
		reg.put(DMDLImages.MODEL_SUM_IMAGE, createModelImage("/icons/model_sum.gif"));
		reg.put(DMDLImages.MODEL_JOIN_IMAGE, createModelImage("/icons/model_join.gif"));
		reg.put(DMDLImages.MODEL_PROJ_IMAGE, createModelImage("/icons/model_proj.gif"));
	}

	private static ImageDescriptor createModelImage(String path) {
		Image baseImage = getDataModelImage();
		ImageDescriptor decorateDescriptor = Activator.getImageDescriptor(path);
		ImageDescriptor descriptor = new DecorationOverlayIcon(baseImage, decorateDescriptor, IDecoration.BOTTOM_RIGHT);
		return descriptor;
	}

	public static Image getImage(String key) {
		ImageRegistry registry = Activator.getDefault().getImageRegistry();
		return registry.get(key);
	}

	public static Image getDmdlFileImage() {
		return getImage(DMDL_FILE);
	}

	public static Image getDataModelImage() {
		return JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CLASS);
	}

	public static Image getDataModelImage(ModelToken model) {
		String modelType = model.getModelType();
		return getDataModelImage(modelType);
	}

	public static Image getDataModelImage(String modelType) {
		if ("summarized".equals(modelType) || "joined".equals(modelType) || "projective".equals(modelType)) {
			return getImage(DMDLImages.MODEL_IMAGE + modelType);
		} else {
			return DMDLImages.getDataModelImage();
		}
	}

	public static Image getPropertyImage() {
		return JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_FIELD_PUBLIC);
	}
}
