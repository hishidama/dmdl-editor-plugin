package jp.hishidama.eclipse_plugin.dmdl_editor.internal.extension.portergen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ClassGenerator {
	protected String packageName;
	protected String className;

	public String generate(String packageName, String className) {
		this.packageName = packageName;
		this.className = className;
		initialize();
		return generate();
	}

	protected abstract void initialize();

	private String generate() {
		StringBuilder body = new StringBuilder(1024);
		appendClass(body);

		StringBuilder sb = new StringBuilder(body.length() + 512);
		appendPackage(sb);
		appendImport(sb);
		sb.append(body);
		return sb.toString();
	}

	private void appendPackage(StringBuilder sb) {
		sb.append("package ");
		sb.append(packageName);
		sb.append(";\n\n");
	}

	private void appendImport(StringBuilder sb) {
		List<String> list = new ArrayList<String>(classNameMap.keySet());
		Collections.sort(list);
		for (String s : list) {
			if (!s.isEmpty()) {
				sb.append("import ");
				sb.append(s);
				sb.append(";\n");
			}
		}
		sb.append("\n");
	}

	protected abstract void appendClass(StringBuilder sb);

	//
	private Map<String, String> classNameMap = new HashMap<String, String>();
	private Set<String> simpleNameSet = new HashSet<String>();

	protected String getCachedClassName(String className) {
		if (className == null) {
			className = "";
		}
		String name = classNameMap.get(className);
		if (name != null) {
			return name;
		}
		String sname = getSimpleName(className);
		if (simpleNameSet.contains(sname)) {
			classNameMap.put(className, className);
			return className;
		} else {
			classNameMap.put(className, sname);
			simpleNameSet.add(sname);
			return sname;
		}
	}

	protected static String getSimpleName(String name) {
		if (name == null) {
			return null;
		}
		int n = name.lastIndexOf('.');
		if (n >= 0) {
			return name.substring(n + 1);
		} else {
			return name;
		}
	}
}
