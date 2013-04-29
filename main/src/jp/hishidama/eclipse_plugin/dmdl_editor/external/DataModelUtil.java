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

import org.eclipse.core.resources.IProject;

public class DataModelUtil {

	public static List<DataModelInfo> getModels(IProject project) {
		assert project != null;

		IndexContainer ic = IndexContainer.getContainer(project);
		if (ic == null) {
			return null;
		}

		Collection<ModelIndex> models = ic.getModels();
		List<DataModelInfo> list = new ArrayList<DataModelInfo>(models.size());
		for (ModelIndex mi : models) {
			DataModelInfo info = new DataModelInfo(mi.getName(),
					decodeDescription(mi.getDescription()), mi.getFile());
			list.add(info);
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
