package jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import com.asakusafw.dmdl.Diagnostic;
import com.asakusafw.dmdl.Region;
import com.asakusafw.dmdl.analyzer.DmdlAnalyzer;
import com.asakusafw.dmdl.analyzer.DmdlSemanticException;
import com.asakusafw.dmdl.model.AstModelDefinition;
import com.asakusafw.dmdl.model.AstScript;
import com.asakusafw.dmdl.parser.DmdlParser;
import com.asakusafw.dmdl.parser.DmdlSyntaxException;
import com.asakusafw.dmdl.source.DmdlSourceRepository;
import com.asakusafw.dmdl.source.DmdlSourceRepository.Cursor;
import com.asakusafw.dmdl.spi.AttributeDriver;
import com.asakusafw.dmdl.spi.TypeDriver;

public class DmdlParserCaller {

	public List<Object[]> parse(URI name, String text) throws IOException {
		List<Object[]> list = new ArrayList<Object[]>();

		try {
			analyze(name, text);
		} catch (DmdlSyntaxException e) {
			list.add(createResult(2, e.getMessage(), e.getRegion()));
		} catch (DmdlSemanticException e) {
			for (Diagnostic diagnostic : e.getDiagnostics()) {
				int level;
				switch (diagnostic.level) {
				case INFO:
					level = 0; // IMarker.SEVERITY_INFO
					break;
				case WARN:
					level = 1; // IMarker.SEVERITY_WARNING
					break;
				default:
					level = 2; // IMarker.SEVERITY_ERROR
					break;
				}
				list.add(createResult(level, diagnostic.message,
						diagnostic.region));
			}
		}

		return list;
	}

	protected Object[] createResult(int level, String message, Region region) {
		return new Object[] { level, message, region.beginLine,
				region.beginColumn, region.endLine, region.endColumn };
	}

	protected void analyze(URI name, String text) throws IOException,
			DmdlSyntaxException, DmdlSemanticException {
		DmdlSourceRepository repository = new DmdlSourceString(name, text);
		DmdlAnalyzer analyzer = parse(repository);
		analyzer.resolve();
	}

	// see com.asakusafw.dmdl.util.AnalyzeTask
	protected DmdlAnalyzer parse(DmdlSourceRepository source)
			throws IOException, DmdlSyntaxException {
		DmdlParser parser = new DmdlParser();

		ClassLoader loader = getClass().getClassLoader();
		DmdlAnalyzer analyzer = new DmdlAnalyzer(ServiceLoader.load(
				TypeDriver.class, loader), ServiceLoader.load(
				AttributeDriver.class, loader));
		Cursor cursor = source.createCursor();
		try {
			while (cursor.next()) {
				URI name = cursor.getIdentifier();
				Reader resource = cursor.openResource();
				try {
					AstScript script = parser.parse(resource, name);
					for (AstModelDefinition<?> model : script.models) {
						analyzer.addModel(model);
					}
				} finally {
					resource.close();
				}
			}
		} finally {
			cursor.close();
		}
		return analyzer;
	}
}
