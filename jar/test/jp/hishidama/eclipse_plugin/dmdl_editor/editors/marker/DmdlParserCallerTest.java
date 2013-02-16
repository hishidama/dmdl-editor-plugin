package jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DmdlParserCallerTest {
	static final String TEMP_FILENAME = "dmdlparser-caller.test.dmdl";

	@Test
	public void parse0() throws URISyntaxException, IOException {
		DmdlParserCaller caller = new DmdlParserCaller();
		String text = "";
		List<Object[]> input = createInput(text);
		List<Object[]> list = caller.parse(input);
		assertEquals(0, list.size());
	}

	@Test
	public void parse1() throws URISyntaxException, IOException {
		DmdlParserCaller caller = new DmdlParserCaller();
		String text = "item={ name:INT; };";
		List<Object[]> input = createInput(text);
		List<Object[]> list = caller.parse(input);
		assertEquals(0, list.size());
	}

	@Test
	public void parse_error1() throws URISyntaxException, IOException {
		DmdlParserCaller caller = new DmdlParserCaller();
		String text = "item={ name:I NT; };";
		List<Object[]> input = createInput(text);
		List<Object[]> list = caller.parse(input);
		assertEquals(1, list.size());

		assertMarker(input.get(0)[0], 2, "Invalid DMDL Script "
				+ input.get(0)[0]
				+ " in the grammar: invalid token: \"I\" (in \"type\")", 1, 13,
				1, 13, list.get(0));
	}

	@Test
	public void parse_error2() throws URISyntaxException, IOException {
		DmdlParserCaller caller = new DmdlParserCaller();
		String text = "item={ name:int; };";
		List<Object[]> input = createInput(text);
		List<Object[]> list = caller.parse(input);
		assertEquals(1, list.size());

		assertMarker(
				input.get(0)[0],
				2,
				"Failed to resolve type \"com.asakusafw.dmdl.model.AstReferenceType",
				1, 13, 1, 15, list.get(0));
	}

	private List<Object[]> createInput(String dmdl) {
		File d = new File(System.getProperty("java.io.tmpdir"));
		File f = new File(d, TEMP_FILENAME);
		f.deleteOnExit();
		String encoding = "UTF-8";

		try {
			Writer w = new OutputStreamWriter(new FileOutputStream(f), encoding);
			try {
				w.write(dmdl);
			} finally {
				w.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		URI uri = f.toURI();
		List<Object[]> list = new ArrayList<Object[]>();
		list.add(new Object[] { uri, encoding });
		return list;
	}

	private void assertMarker(Object uri, int level, String message,
			int beginLine, int beginColumn, int endLine, int endColumn,
			Object[] actual) {
		assertEquals(uri, actual[0]);
		assertEquals(level, actual[1]);
		assertTrue((String) actual[2], ((String) actual[2]).startsWith(message));
		assertEquals(beginLine, actual[3]);
		assertEquals(beginColumn, actual[4]);
		assertEquals(endLine, actual[5]);
		assertEquals(endColumn, actual[6]);
	}
}
