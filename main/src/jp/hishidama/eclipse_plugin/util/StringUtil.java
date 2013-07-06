package jp.hishidama.eclipse_plugin.util;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelUtil;

public class StringUtil {
	public static boolean nonEmpty(String s) {
		return s != null && !s.isEmpty();
	}

	public static boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}

	public static String toCamelCase(String name) {
		if (name == null) {
			return null;
		}
		String[] ss = name.split("\\_");
		StringBuilder sb = new StringBuilder(name.length());
		for (String s : ss) {
			sb.append(toFirstUpper(s));
		}
		return sb.toString();
	}

	public static String toLowerCamelCase(String name) {
		return toFirstLower(toCamelCase(name));
	}

	public static String toFirstUpper(String s) {
		if (s == null) {
			return null;
		}
		if (s.length() < 1) {
			return s;
		}
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	public static String toFirstLower(String s) {
		if (s == null) {
			return null;
		}
		if (s.length() < 1) {
			return s;
		}
		return Character.toLowerCase(s.charAt(0)) + s.substring(1);
	}

	public static String append(String packageName, String name) {
		if (packageName.endsWith(".")) {
			packageName = packageName.substring(0, packageName.length() - 1);
		}
		if (name.startsWith(".")) {
			name = name.substring(1);
		}
		if (packageName.isEmpty()) {
			return name;
		} else {
			return packageName + "." + name;
		}
	}

	public static String getPackageName(String name) {
		int n = name.lastIndexOf('.');
		if (n >= 0) {
			return name.substring(0, n);
		} else {
			return "";
		}
	}

	public static String getSimpleName(String name) {
		int n = name.lastIndexOf('.');
		if (n >= 0) {
			return name.substring(n + 1);
		} else {
			return name;
		}
	}

	public static String replace(String s, String modelName, String propName, String propDesc) {
		StringBuilder sb = new StringBuilder(s.length());
		for (int pos = 0;;) {
			int n = s.indexOf("$(", pos);
			if (n >= 0) {
				sb.append(s.substring(pos, n));
				pos = n;
				int m = s.indexOf(")", pos);
				if (m >= 0) {
					String key = s.substring(n + 2, m);
					sb.append(convert(key, modelName, propName, propDesc));
					pos = m + 1;
				} else {
					String key = s.substring(n + 2);
					sb.append(convert(key, modelName, propName, propDesc));
					break;
				}
			} else {
				sb.append(s.substring(pos));
				break;
			}
		}
		return sb.toString();
	}

	private static String convert(String key, String modelName, String propName, String propDesc) {
		String s;
		if ("modelName".equals(key)) {
			s = modelName;
		} else if ("modelName.toUpper".equals(key)) {
			s = modelName.toUpperCase();
		} else if ("modelName.toCamelCase".equals(key)) {
			s = toCamelCase(modelName);
		} else if ("name".equals(key)) {
			s = propName;
		} else if ("name.toUpper".equals(key)) {
			s = propName.toUpperCase();
		} else if ("description".equals(key)) {
			s = DataModelUtil.decodeDescription(propDesc);
		} else {
			s = "";
		}
		return (s != null) ? s : "";
	}

	public static String escapeQuote(String s) {
		return s.replaceAll("\"", "\\\"");
	}

	public static String toString(List<?> list) {
		StringBuilder sb = new StringBuilder(512);
		for (Object obj : list) {
			if (sb.length() != 0) {
				sb.append("\n");
			}
			sb.append(obj);
		}
		return sb.toString();
	}
}
