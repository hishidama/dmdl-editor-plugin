package jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLDocument;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;

public class DMDLMarker {

	protected Map<IJavaProject, ParserWrapper> map = new HashMap<IJavaProject, ParserWrapper>();

	public void parse(IFile file, DMDLDocument document) {
		IJavaProject project = getJavaProject(file);
		if (project == null) {
			return;
		}

		ParserWrapper wrapper = map.get(project);
		if (wrapper == null) {
			wrapper = new ParserWrapper(project);
			map.put(project, wrapper);
		}
		if (wrapper.isValid()) {
			wrapper.parse(file, document);
		}
	}

	protected final IJavaProject getJavaProject(IFile file) {
		IProject project = file.getProject();
		return JavaCore.create(project);
	}

	protected static class ParserWrapper {
		public ParserWrapper(IJavaProject project) {
			initClassLoader(project);
		}

		private ClassLoader parserLoader;

		public boolean isValid() {
			return parserLoader != null;
		}

		protected void initClassLoader(IJavaProject project) {
			List<URL> parserClassList = new ArrayList<URL>();
			URL parserUrl = findClassPath(parserClassList, project,
					"com.asakusafw.dmdl.parser.DmdlParser");
			if (parserUrl == null) {
				return;
			}
			findClassPath(parserClassList, project, "org.slf4j.LoggerFactory");
			findClassPath(parserClassList, project,
					"com.asakusafw.utils.collections.Lists");
			parserLoader = URLClassLoader.newInstance(parserClassList
					.toArray(new URL[parserClassList.size()]));
		}

		protected URL findClassPath(List<URL> list, IJavaProject project,
				String className) {
			try {
				IType type = project.findType(className);
				if (type != null) {
					IClassFile f = type.getClassFile();
					URL url = f.getPath().toFile().toURI().toURL();
					list.add(url);
					return url;
				}
				return null;
			} catch (Exception e) {
				return null;
			}
		}

		protected Object parser;
		protected Method parser_parse;

		public Object parse(IFile file, DMDLDocument document) {
			try {
				if (parser == null) {
					Class<?> c;
					try {
						c = parserLoader
								.loadClass("com.asakusafw.dmdl.parser.DmdlParser");
					} catch (ClassNotFoundException e) {
						ILog log = Activator.getDefault().getLog();
						log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
								"DMDLMarker#loadClass() error.", e));
						parserLoader = null;
						return null;
					}
					parser = c.newInstance();
				}
				if (parser_parse == null) {
					parser_parse = parser.getClass().getMethod("parse",
							Reader.class, URI.class);
				}
				StringReader reader = new StringReader(document.get());
				URI uri = file.getFullPath().toFile().toURI();
				try {
					return parser_parse.invoke(parser, reader, uri);
				} catch (InvocationTargetException e) {
					createErrorMarker(e.getCause(), file, document);
					return null;
				} finally {
					reader.close();
				}
			} catch (Exception e) {
				ILog log = Activator.getDefault().getLog();
				log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
						"DMDLMarker#parse() error.", e));
				return null;
			}
		}

		protected void createErrorMarker(Throwable cause, IFile file,
				DMDLDocument document) {
			if (!cause.getClass().getName()
					.equals("com.asakusafw.dmdl.parser.DmdlSyntaxException")) {
				throw new RuntimeException(cause);
			}
			try {
				Method getRegion = cause.getClass().getMethod("getRegion");
				Object region = getRegion.invoke(cause);
				int beginLine = region.getClass().getField("beginLine")
						.getInt(region);
				int beginColumn = region.getClass().getField("beginColumn")
						.getInt(region);
				int endLine = region.getClass().getField("endLine")
						.getInt(region);
				int endColumn = region.getClass().getField("endColumn")
						.getInt(region);

				int beginOffset = document.getLineOffset(beginLine - 1)
						+ beginColumn - 1;
				int endOffset = document.getLineOffset(endLine - 1) + endColumn;

				IMarker marker = file
						.createMarker("org.eclipse.core.resources.problemmarker");
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				marker.setAttribute(IMarker.MESSAGE, cause.getMessage());
				// marker.setAttribute(IMarker.LINE_NUMBER, beginLine - 1);
				marker.setAttribute(IMarker.CHAR_START, beginOffset);
				marker.setAttribute(IMarker.CHAR_END, endOffset);
				marker.setAttribute(IMarker.LOCATION, String.format(
						"%d:%d-%d:%d", beginLine, beginColumn, endLine,
						endColumn));
			} catch (Exception e) {
				ILog log = Activator.getDefault().getLog();
				log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
						"DMDLMarker#createErrorMarker() error.", e));
			}
		}
	}
}
