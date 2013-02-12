package jp.hishidama.eclipse_plugin.dmdl_editor.parser;

import static org.junit.Assert.*;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelList;

import org.junit.Test;

public class DMDLSimpleParserEofTest extends DMDLSimpleParserTestCase {

	@Test
	public void parse_eof_slash() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "/";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());

		DMDLToken expected = model(word("/"));
		assertEqualsToken(expected, list.get(0));

		assertEnd(actual, models);
	}

	@Test
	public void parse_eof_hyphen() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "-";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());

		DMDLToken expected = model(word("-"));
		assertEqualsToken(expected, list.get(0));

		assertEnd(actual, models);
	}

	@Test
	public void parse_eof_block_slash() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "{/";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());

		DMDLToken expected = model(block(word("{"), prop(word("/"))));
		assertEqualsToken(expected, list.get(0));

		assertEnd(actual, models);
	}

	@Test
	public void parse_eof_block_hyphen() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "{-";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());

		DMDLToken expected = model(block(word("{"), prop(word("-"))));
		assertEqualsToken(expected, list.get(0));

		assertEnd(actual, models);
	}

	@Test
	public void parse_eof_comment_aster() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "/*aaa*";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());

		DMDLToken expected = model(comm("/*aaa*"));
		assertEqualsToken(expected, list.get(0));

		assertEnd(actual, models);
	}

	@Test
	public void parse_eof_lineComment_n() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "//aaa\n";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());

		DMDLToken expected = model(comm("//aaa\n"));
		assertEqualsToken(expected, list.get(0));

		assertEnd(actual, models);
	}

	@Test
	public void parse_eof_lineComment_r() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "//aaa\r";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());

		DMDLToken expected = model(comm("//aaa\r"));
		assertEqualsToken(expected, list.get(0));

		assertEnd(actual, models);
	}

	@Test
	public void parse_eof_lineComment_rn() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "//aaa\r\n";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());

		DMDLToken expected = model(comm("//aaa\r\n"));
		assertEqualsToken(expected, list.get(0));

		assertEnd(actual, models);
	}

	@Test
	public void parse_eof_desc_n() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "\"aaa\n";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());

		DMDLToken expected = model(desc("aaa\n", false));
		assertEqualsToken(expected, list.get(0));

		assertEnd(actual, models);
	}

	@Test
	public void parse_eof_desc_r() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "\"aaa\r";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());

		DMDLToken expected = model(desc("aaa\r", false));
		assertEqualsToken(expected, list.get(0));

		assertEnd(actual, models);
	}

	@Test
	public void parse_eof_desc_rn() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "\"aaa\r\n";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());

		DMDLToken expected = model(desc("aaa\r\n", false));
		assertEqualsToken(expected, list.get(0));

		assertEnd(actual, models);
	}

	@Test
	public void parse_eof_args_minus() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "(item -";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());

		DMDLToken expected = model(args(false, arg(word("item"), word("-"))));
		assertEqualsToken(expected, list.get(0));

		assertEnd(actual, models);
	}

	@Test
	public void parse_eof_args_slash() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "(item /";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());

		DMDLToken expected = model(args(false, arg(word("item"), word("/"))));
		assertEqualsToken(expected, list.get(0));

		assertEnd(actual, models);
	}

	@Test
	public void parse_eof_array() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "(value ={ abc";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());

		DMDLToken expected = model(args(false,
				arg(word("value"), word("="), arr(false, "abc"))));
		assertEqualsToken(expected, list.get(0));

		assertEnd(actual, models);
	}

	@Test
	public void parse_eof_symbol_eq() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "item =";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());

		DMDLToken expected = model(word("item"), word("="));
		assertEqualsToken(expected, list.get(0));

		assertEnd(actual, models);
	}

	@Test
	public void parse_eof_symbol_hyphen() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "item -";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());

		DMDLToken expected = model(word("item"), word("-"));
		assertEqualsToken(expected, list.get(0));

		assertEnd(actual, models);
	}
}
