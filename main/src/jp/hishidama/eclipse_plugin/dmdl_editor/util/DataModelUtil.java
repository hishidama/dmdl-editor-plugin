package jp.hishidama.eclipse_plugin.dmdl_editor.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index.IndexContainer;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index.ModelIndex;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index.PropertyIndex;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.BuildPropertiesUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.DMDLFileUtil;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

public class DataModelUtil {

	public static DataModelInfo findModel(IProject project, String modelName) {
		ModelIndex mi = findModelIndex(project, modelName);
		if (mi == null) {
			return null;
		}
		return createInfo(mi);
	}

	private static ModelIndex findModelIndex(IProject project, String modelName) {
		if (project == null) {
			return null;
		}
		IndexContainer ic = IndexContainer.getContainer(project);
		if (ic == null) {
			return null;
		}
		return ic.findModel(modelName);
	}

	public static List<IFile> getDmdlFiles(IProject project) {
		if (project == null) {
			return null;
		}

		List<IFile> files = DMDLFileUtil.getDmdlFiles(project);
		Collections.sort(files, new Comparator<IFile>() {
			@Override
			public int compare(IFile o1, IFile o2) {
				String name1 = o1.getFullPath().toPortableString();
				String name2 = o2.getFullPath().toPortableString();
				return name1.compareToIgnoreCase(name2);
			}
		});

		return files;
	}

	public static List<DataModelInfo> getModels(IProject project) {
		List<IFile> files = getDmdlFiles(project);
		if (files == null) {
			return null;
		}

		List<DataModelInfo> list = new ArrayList<DataModelInfo>(files.size() * 8);
		for (IFile file : files) {
			getModels(list, file);
		}
		return list;
	}

	public static List<DataModelInfo> getModels(IFile file) {
		List<DataModelInfo> list = new ArrayList<DataModelInfo>();
		getModels(list, file);
		return list;
	}

	private static void getModels(List<DataModelInfo> list, IFile file) {
		try {
			ModelList models = DMDLFileUtil.getModels(file);
			for (ModelToken model : models.getNamedModelList()) {
				list.add(createInfo(file, model));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static DataModelInfo createInfo(ModelIndex mi) {
		return createInfo(mi.getFile(), mi.getModel());
	}

	private static DataModelInfo createInfo(IFile file, ModelToken model) {
		String name = model.getModelName();
		String desc = decodeDescription(model.getDescription());
		String type = model.getModelType();
		DataModelInfo info = new DataModelInfo(name, desc, type, file);
		return info;
	}

	public static String getModelClass(IProject project, String modelName) {
		Properties properties = BuildPropertiesUtil.getBuildProperties(project);
		String pack = BuildPropertiesUtil.getModelgenPackage(properties);
		if (pack == null) {
			return null;
		}
		String sname = StringUtil.toCamelCase(modelName);
		return pack + ".dmdl.model." + sname;
	}

	public static List<DataModelProperty> getModelProperties(IProject project, String modelName) {
		ModelIndex mi = findModelIndex(project, modelName);
		if (mi == null) {
			return null;
		}
		IndexContainer ic = IndexContainer.getContainer(project);

		List<DataModelProperty> list = new ArrayList<DataModelProperty>();
		for (PropertyIndex pi : mi.getProperties()) {
			PropertyToken token = pi.getToken();
			String name = token.getPropertyName();
			String desc = decodeDescription(token.getPropertyDescription());
			String type = token.getDataType(ic);
			DataModelProperty p = new DataModelProperty(name, desc, type);
			list.add(p);
		}
		return list;
	}

	public static String decodeDescription(String s) {
		if (s == null) {
			return s;
		}
		if (s.startsWith("\"")) {
			s = s.substring(1);
		}
		if (s.endsWith("\"")) {
			s = s.substring(0, s.length() - 1);
		}
		return s;
	}

	public static String encodeDescription(String s) {
		if (s == null) {
			return null;
		}
		return "\"" + s + "\"";
	}
}
