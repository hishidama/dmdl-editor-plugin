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

	public static String getValue(IProject project, String tag) {
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
					String value = getValue(line, tag);
					if (value != null) {
						return value.trim();
					}
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
}
