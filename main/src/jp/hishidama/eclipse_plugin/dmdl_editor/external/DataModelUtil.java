package jp.hishidama.eclipse_plugin.dmdl_editor.external;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.marker.ParserClassUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.index.IndexContainer;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.index.ModelIndex;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.index.PropertyIndex;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;

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

	public static List<DataModelInfo> getModels(IProject project) {
		if (project == null) {
			return null;
		}
		IndexContainer ic = IndexContainer.getContainer(project);
		if (ic == null) {
			return null;
		}

		Collection<ModelIndex> models = ic.getModels();
		List<DataModelInfo> list = new ArrayList<DataModelInfo>(models.size());
		for (ModelIndex mi : models) {
			list.add(createInfo(mi));
		}

		Collections.sort(list, new Comparator<DataModelInfo>() {
			@Override
			public int compare(DataModelInfo o1, DataModelInfo o2) {
				String name1 = o1.getName();
				String name2 = o2.getName();
				return name1.compareToIgnoreCase(name2);
			}
		});

		return list;
	}

	private static DataModelInfo createInfo(ModelIndex mi) {
		String name = mi.getName();
		String desc = decodeDescription(mi.getDescription());
		IFile file = mi.getFile();
		DataModelInfo info = new DataModelInfo(name, desc, file);
		return info;
	}

	public static String getModelClass(IProject project, String modelName) {
		Properties properties = ParserClassUtil.getBuildProperties(project);
		if (properties == null) {
			return null;
		}
		String pack = properties.getProperty("asakusa.modelgen.package");
		if (pack == null) {
			return null;
		}
		String sname = IndexContainer.convertSnake(modelName);
		return pack + ".dmdl.model." + sname;
	}

	public static List<DataModelProperty> getModelProperties(IProject project,
			String modelName) {
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
