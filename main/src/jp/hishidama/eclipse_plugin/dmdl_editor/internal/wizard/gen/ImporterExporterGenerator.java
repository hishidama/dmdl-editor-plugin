package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.gen;

import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DMDLImporterExporterDefinition;
import jp.hishidama.eclipse_plugin.dmdl_editor.extension.DmdlCompilerProperties;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.BuildPropertiesUtil;
import jp.hishidama.eclipse_plugin.util.FileUtil;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public abstract class ImporterExporterGenerator extends ClassGenerator {

	protected Map<String, String> map;
	protected IProject project;
	protected ModelToken model;
	protected String dir;
	protected DmdlCompilerProperties properties;

	public void generate(IProject project, DmdlCompilerProperties properties, Map<String, String> map,
			ModelToken model, String dir, String name, boolean open) throws CoreException {
		this.properties = properties;
		this.map = map;
		this.project = project;
		this.model = model;
		this.dir = dir;
		String resolvedName = StringUtil.replace(name, model.getModelName(), "", "");
		String packageName = StringUtil.getPackageName(resolvedName);
		String simpleName = StringUtil.getSimpleName(resolvedName);
		String contents = super.generate(packageName, simpleName);

		IFile file = getFile(packageName);
		FileUtil.save(file, contents);
		if (open) {
			FileUtil.openFile(file, resolvedName);
		}
	}

	private IFile getFile(String packageName) throws CoreException {
		IFolder folder = project.getFolder(dir);
		folder = folder.getFolder(packageName.replace('.', '/'));
		IFile file = folder.getFile(className + ".java");
		FileUtil.createFolder(project, folder.getProjectRelativePath());
		return file;
	}

	@Override
	protected void initialize() {
		properties = BuildPropertiesUtil.getBuildProperties(project, true);
	}

	protected String getGeneratedClassName(String middle, String simpleName) {
		String pack = properties.getModelgenPackage();

		StringBuilder sb = new StringBuilder(64);
		sb.append(pack);
		sb.append(middle);
		sb.append(simpleName);
		return sb.toString();
	}

	@Override
	protected void appendClass(StringBuilder sb) {
		sb.append("public class ");
		sb.append(className);
		sb.append(" extends ");
		sb.append(getExtendsClassName());
		sb.append(" {\n");

		appendMethods(sb);

		sb.append("}\n");
	}

	protected String getExtendsClassName() {
		String camelName = StringUtil.toCamelCase(model.getModelName());
		String fullName = getExtendsClassName(camelName);
		return getCachedClassName(fullName);
	}

	protected abstract String getExtendsClassName(String modelCamelName);

	protected abstract void appendMethods(StringBuilder sb);

	protected final void appendMethodDataSize(StringBuilder sb) {
		// DataSizeは親クラスで定義されている内部クラスなので、importしなくてよい。
		// getCachedClassName("com.asakusafw.vocabulary.external.ImporterDescription.DataSize");
		String name = "DataSize";
		String size = String.format("%s.%s", name, map.get(DMDLImporterExporterDefinition.KEY_DATA_SIZE));
		appendMethod(sb, name, "getDataSize", size, "");
	}

	protected final void appendMethod(StringBuilder sb, String method, String value) {
		appendMethod(sb, "String", method, StringUtil.escapeQuote(value), "\"");
	}

	protected final void appendMethodNull(StringBuilder sb, String method) {
		appendMethod(sb, "String", method, "null", "");
	}

	protected final void appendMethodList(StringBuilder sb, String method, String value) {
		StringBuilder buf = new StringBuilder(value.length());
		if (value.trim().isEmpty()) {
			buf.append("super.");
			buf.append(method);
			buf.append("()");
		} else {
			buf.append(getCachedClassName("java.util.Arrays"));
			buf.append(".asList(");
			String[] ss = value.split(",");
			boolean first = true;
			for (String s : ss) {
				if (first) {
					first = false;
				} else {
					buf.append(", ");
				}
				buf.append("\"");
				buf.append(StringUtil.escapeQuote(s.trim()));
				buf.append("\"");
			}
			buf.append(")");
		}

		String rtype = getCachedClassName("java.util.List") + "<String>";
		appendMethod(sb, rtype, method, buf, "");
	}

	private void appendMethod(StringBuilder sb, String rtype, String method, CharSequence value, String quote) {
		sb.append("\n\t@Override\n");
		sb.append("\tpublic ");
		sb.append(rtype);
		sb.append(" ");
		sb.append(method);
		sb.append("() {\n");
		sb.append("\t\treturn ");
		sb.append(quote);
		sb.append(value);
		sb.append(quote);
		sb.append(";\n");
		sb.append("\t}\n");
	}
}
