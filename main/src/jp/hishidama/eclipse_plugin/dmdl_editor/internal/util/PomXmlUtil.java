package jp.hishidama.eclipse_plugin.dmdl_editor.internal.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public class PomXmlUtil {

	public static String getValue(IProject project) {
		IFile file = project.getFile("pom.xml");
		if (!file.exists()) {
			return "";
		}
		try {
			StringBuilder sb = new StringBuilder(20 * 1024);

			InputStream is = file.getContents();
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				for (;;) {
					String line = r.readLine();
					if (line == null) {
						break;
					}
					sb.append(line);
					sb.append('\n');
				}
				return sb.toString();
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				e.printStackTrace();
				return sb.toString();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getValue(String text, String tag) {
		int n = text.indexOf("<" + tag + ">");
		if (n < 0) {
			return null;
		}
		n += tag.length() + 2;
		int m = text.indexOf("</" + tag + ">", n);
		if (m < 0) {
			return text.substring(n);
		} else {
			return text.substring(n, m);
		}
	}

	public static boolean exists(String pom, String tag, String value) {
		String begin = "<" + tag + ">";
		String end = "</" + tag + ">";
		for (int i = 0; i < pom.length();) {
			int n = pom.indexOf(begin, i);
			if (n < 0) {
				return false;
			}
			n += begin.length();
			int m = pom.indexOf(end, n);
			if (m < 0) {
				m = pom.length();
			}
			String v = pom.substring(n, m);
			if (v.trim().equals(value)) {
				return true;
			}
			i = m + end.length();
		}
		return false;
	}
}
