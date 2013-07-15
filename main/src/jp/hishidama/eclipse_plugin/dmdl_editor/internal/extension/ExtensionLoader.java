package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLAttributeWizardDefinition;
import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLEditorConfiguration;
import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLImporterExporterGenerator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class ExtensionLoader {
	private static final String CONFIGURATION_POINT_ID = Activator.PLUGIN_ID + ".dmdlEditorConfiguration";
	private static final String ATTRIBUTE_DEF_POINT_ID = Activator.PLUGIN_ID + ".dmdlAttributeWizardDefinition";
	private static final String GENERATOR_POINT_ID = Activator.PLUGIN_ID + ".dmdlImporterExporterGenerator";

	private List<DMDLEditorConfiguration> configList;
	private List<DMDLAttributeWizardDefinition> attrDefList;
	private List<DMDLImporterExporterGenerator> generatorList;

	public List<DMDLEditorConfiguration> getConfigurations() {
		if (configList != null) {
			return configList;
		}

		IExtensionPoint point = getExtensionPoint(CONFIGURATION_POINT_ID);

		configList = new ArrayList<DMDLEditorConfiguration>();
		for (IExtension extension : point.getExtensions()) {
			for (IConfigurationElement element : extension.getConfigurationElements()) {
				try {
					Object obj = element.createExecutableExtension("class");
					if (obj instanceof DMDLEditorConfiguration) {
						configList.add((DMDLEditorConfiguration) obj);
					}
				} catch (CoreException e) {
					Activator.getDefault().getLog().log(e.getStatus());
				}
			}
		}
		Collections.sort(configList, new Comparator<DMDLEditorConfiguration>() {
			@Override
			public int compare(DMDLEditorConfiguration c0, DMDLEditorConfiguration c1) {
				return c0.getConfigurationName().compareTo(c1.getConfigurationName());
			}
		});
		return configList;
	}

	public List<DMDLAttributeWizardDefinition> getAttributeWizardDefinitions() {
		if (attrDefList != null) {
			return attrDefList;
		}

		IExtensionPoint point = getExtensionPoint(ATTRIBUTE_DEF_POINT_ID);

		attrDefList = new ArrayList<DMDLAttributeWizardDefinition>();
		for (IExtension extension : point.getExtensions()) {
			for (IConfigurationElement element : extension.getConfigurationElements()) {
				try {
					Object obj = element.createExecutableExtension("class");
					if (obj instanceof DMDLAttributeWizardDefinition) {
						attrDefList.add((DMDLAttributeWizardDefinition) obj);
					}
				} catch (CoreException e) {
					Activator.getDefault().getLog().log(e.getStatus());
				}
			}
		}
		return attrDefList;
	}

	public List<DMDLImporterExporterGenerator> getImporterExporterGenerators() {
		if (generatorList != null) {
			return generatorList;
		}

		IExtensionPoint point = getExtensionPoint(GENERATOR_POINT_ID);

		generatorList = new ArrayList<DMDLImporterExporterGenerator>();
		for (IExtension extension : point.getExtensions()) {
			for (IConfigurationElement element : extension.getConfigurationElements()) {
				try {
					Object obj = element.createExecutableExtension("class");
					if (obj instanceof DMDLImporterExporterGenerator) {
						generatorList.add((DMDLImporterExporterGenerator) obj);
					}
				} catch (CoreException e) {
					Activator.getDefault().getLog().log(e.getStatus());
				}
			}
		}
		Collections.sort(generatorList, new Comparator<DMDLImporterExporterGenerator>() {
			@Override
			public int compare(DMDLImporterExporterGenerator c0, DMDLImporterExporterGenerator c1) {
				int c = c0.getName().compareTo(c1.getName());
				if (c != 0) {
					return c;
				}
				int b0 = c0.isExporter() ? 1 : 0;
				int b1 = c1.isExporter() ? 1 : 0;
				return b0 - b1;
			}
		});
		return generatorList;
	}

	private IExtensionPoint getExtensionPoint(String id) {
		IExtensionRegistry registory = Platform.getExtensionRegistry();
		IExtensionPoint point = registory.getExtensionPoint(id);
		if (point == null) {
			throw new IllegalStateException(id);
		}
		return point;
	}
}
