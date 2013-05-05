package jp.hishidama.eclipse_plugin.dmdl_editor.internal;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLImages;

import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "jp.hishidama.eclipse_plugin.dmdl_editor"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		reg.put(DMDLImages.DMDL_FILE, getImageDescriptor("/icons/hishidama16.gif"));
		reg.put(DMDLImages.MODEL_SUM_IMAGE, createModelImage("/icons/model_sum.gif"));
		reg.put(DMDLImages.MODEL_JOIN_IMAGE, createModelImage("/icons/model_join.gif"));
		reg.put(DMDLImages.MODEL_PROJ_IMAGE, createModelImage("/icons/model_proj.gif"));
	}

	protected Image createModelImage(String path) {
		Image baseImage = JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_CLASS);
		ImageDescriptor decorateDescriptor = getImageDescriptor(path);
		ImageDescriptor descriptor = new DecorationOverlayIcon(baseImage, decorateDescriptor, IDecoration.BOTTOM_RIGHT);
		return descriptor.createImage();
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
