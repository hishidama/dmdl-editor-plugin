package jp.hishidama.eclipse_plugin.dmdl_editor.util;

import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index.IndexContainer;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.BuildPropertiesUtil;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

public class DataModelUtil {

	public static List<DataModelFile> getDmdlFiles(IProject project) {
		if (project == null) {
			return null;
		}
		IndexContainer ic = IndexContainer.getContainer(project);
		return ic.getFiles();
	}

	public static List<DataModelInfo> getModels(IFile file) {
		if (file == null) {
			return null;
		}
		IndexContainer ic = IndexContainer.getContainer(file.getProject());
		return ic.getModels(file);
	}

	public static DataModelInfo findModel(IProject project, String modelName) {
		if (project == null) {
			return null;
		}
		IndexContainer ic = IndexContainer.getContainer(project);
		return ic.getModel(modelName);
	}

	public static List<DataModelProperty> getModelProperties(IProject project, String modelName) {
		DataModelInfo info = findModel(project, modelName);
		if (info == null) {
			return null;
		}
		return info.getProperties();
	}

	public static String getResolvedDataType(IProject project, String modelName, String propertyName) {
		IndexContainer ic = IndexContainer.getContainer(project);
		return ic.getResolvedDataType(modelName, propertyName);
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

	public static String validateName(String title, String name) {
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (('a' <= c && c <= 'z') || ('0' <= c && c <= '9' || c == '_')) {
				continue;
			} else {
				return MessageFormat.format("{0}に使える文字は英小文字・数字・アンダースコアのみです。", title);
			}
		}
		if (name.startsWith("_")) {
			return MessageFormat.format("{0}の最初の文字にアンダースコアは使用できません。", title);
		}
		if (name.endsWith("_")) {
			return MessageFormat.format("{0}の最後の文字にアンダースコアは使用できません。", title);
		}
		if (name.indexOf("__") >= 0) {
			return MessageFormat.format("{0}の中でアンダースコアを2つ以上続けて使うことは出来ません。", title);
		}

		String[] ss = name.split("\\_");
		for (String s : ss) {
			if (s.length() > 0) {
				char c = s.charAt(0);
				if ('a' <= c && c <= 'z') {
					// ok
				} else {
					return MessageFormat.format("{0}に含まれる各単語（アンダースコアで区切られた文字列）はアルファベットから始めることが推奨されています。", title);
				}
			}
		}

		return null;
	}

	public static String getQualifiedModelName(String name, String description, String type) {
		StringBuilder sb = new StringBuilder(64);

		if (description != null) {
			sb.append(description);
			sb.append(" ");
		}

		if (type != null) {
			sb.append(type);
			sb.append(" ");
		}

		sb.append(name);

		return sb.toString();
	}

	public static String getQualifiedPropertyName(String name, String description, String type) {
		StringBuilder sb = new StringBuilder(64);

		if (description != null) {
			sb.append(description);
			sb.append(" ");
		}

		sb.append(name);

		if (type != null) {
			sb.append(" : ");
			sb.append(type);
		}

		return sb.toString();
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
