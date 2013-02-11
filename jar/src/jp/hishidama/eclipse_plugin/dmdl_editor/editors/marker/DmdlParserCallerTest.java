package jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Test;

public class DmdlParserCallerTest {

	@Test
	public void parse0() throws URISyntaxException, IOException {
		DmdlParserCaller caller = new DmdlParserCaller();
		URI name = new URI("file:test");
		String text = "";
		List<Object[]> list = caller.parse(name, text);
		assertEquals(0, list.size());
	}

	@Test
	public void parse1() throws URISyntaxException, IOException {
		DmdlParserCaller caller = new DmdlParserCaller();
		URI name = new URI("file:test");
		String text = "item={ name:INT; };";
		List<Object[]> list = caller.parse(name, text);
		assertEquals(0, list.size());
	}

	@Test
	public void parse_error1() throws URISyntaxException, IOException {
		DmdlParserCaller caller = new DmdlParserCaller();
		URI name = new URI("file:test");
		String text = "item={ name:I NT; };";
		List<Object[]> list = caller.parse(name, text);
		assertEquals(1, list.size());

		assertMarker(
				2,
				"Invalid DMDL Script file:test in the grammar: invalid token: \"I\" (in \"type\")",
				1, 13, 1, 13, list.get(0));
	}

	@Test
	public void parse_error2() throws URISyntaxException, IOException {
		DmdlParserCaller caller = new DmdlParserCaller();
		URI name = new URI("file:test");
		String text = "item={ name:int; };";
		List<Object[]> list = caller.parse(name, text);
		assertEquals(1, list.size());

		assertMarker(
				2,
				"Failed to resolve type \"com.asakusafw.dmdl.model.AstReferenceType",
				1, 13, 1, 15, list.get(0));
	}

	private void assertMarker(int level, String message, int beginLine,
			int beginColumn, int endLine, int endColumn, Object[] actual) {
		assertEquals(level, actual[0]);
		assertTrue(((String) actual[1]).startsWith(message));
		assertEquals(beginLine, actual[2]);
		assertEquals(beginColumn, actual[3]);
		assertEquals(endLine, actual[4]);
		assertEquals(endColumn, actual[5]);
	}
}
