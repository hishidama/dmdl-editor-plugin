package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLEditorConfiguration;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;

public class ExtensionLoader {

	private static final String CONFIGURATION_POINT_ID = Activator.PLUGIN_ID + ".dmdlEditorConfiguration";

	private List<DMDLEditorConfiguration> list;

	public List<DMDLEditorConfiguration> getConfigurations() {
		if (list != null) {
			return list;
		}

		IExtensionRegistry registory = Platform.getExtensionRegistry();
		IExtensionPoint point = registory.getExtensionPoint(CONFIGURATION_POINT_ID);
		if (point == null) {
			throw new IllegalStateException(CONFIGURATION_POINT_ID);
		}

		list = new ArrayList<DMDLEditorConfiguration>();
		for (IExtension extension : point.getExtensions()) {
			for (IConfigurationElement element : extension.getConfigurationElements()) {
				try {
					Object obj = element.createExecutableExtension("class");
					if (obj instanceof DMDLEditorConfiguration) {
						list.add((DMDLEditorConfiguration) obj);
					}
				} catch (CoreException e) {
					Activator.getDefault().getLog().log(e.getStatus());
				}
			}
		}
		Collections.sort(list, new Comparator<DMDLEditorConfiguration>() {
			@Override
			public int compare(DMDLEditorConfiguration c0, DMDLEditorConfiguration c1) {
				return c0.getConfigurationName().compareTo(c1.getConfigurationName());
			}
		});
		return list;
	}
}
