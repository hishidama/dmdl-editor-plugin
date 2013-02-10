package jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker;

import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.asakusafw.dmdl.Region;
import com.asakusafw.dmdl.parser.DmdlParser;
import com.asakusafw.dmdl.parser.DmdlSyntaxException;

public class DmdlParserCaller {

	public List<Object[]> parse(URI name, String text) {
		List<Object[]> list = new ArrayList<Object[]>();

		StringReader reader = new StringReader(text);
		try {
			DmdlParser parser = new DmdlParser();
			parser.parse(reader, name);
		} catch (DmdlSyntaxException e) {
			Region region = e.getRegion();
			Object[] result = { e.getMessage(), region.beginLine,
					region.beginColumn, region.endLine, region.endColumn };
			list.add(result);
		} finally {
			reader.close();
		}

		return list;
	}
}
