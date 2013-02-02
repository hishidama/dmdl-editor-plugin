package jp.hishidama.eclipse_plugin.dmdl_editor.parser;

import static org.junit.Assert.*;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelList;

import org.junit.Test;

public class DMDLSimpleParserTest extends DMDLSimpleParserTestCase {

	@Test
	public void parse1() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "test = { aaa: TEXT; };";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());
		DMDLToken token = list.get(0);
		// System.out.println(token);

		DMDLToken expected = model(
				word("test"),
				word("="),
				block(word("{"),
						prop(word("aaa"), word(":"), word("TEXT"), word(";")),
						word("}")), word(";"));
		assertEqualsToken(expected, token);

		assertEnd(actual, models);
	}

	@Test
	public void parse2() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "/*\n" //
				+ " * コメント\n" //
				+ " */\n"//
				+ "\"test2\"\n" //
				+ "aaa = {\n" //
				+ "// comment1\n" //
				+ "\"desc1\"\n" //
				+ "  prop1: TEXT;\n" //
				+ "-- comment2\n" //
				+ "\"desc2\"\n" //
				+ "  prop2: LONG;\n" //
				+ "};";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());
		DMDLToken token = list.get(0);
		// System.out.println(token);

		DMDLToken expected = model(
				comm("/*\n * コメント\n */"),
				desc("test2"),
				word("aaa"),
				word("="),
				block(word("{"),
						prop(comm("// comment1\n"), desc("desc1"),
								word("prop1"), word(":"), word("TEXT"),
								word(";")),
						prop(comm("-- comment2\n"), desc("desc2"),
								word("prop2"), word(":"), word("LONG"),
								word(";")), word("}")), word(";"));
		assertEqualsToken(expected, token);

		assertEnd(actual, models);
	}

	@Test
	public void parse_annotation() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "@zzz\n"//
				+ "@abc()\n" //
				+ "@foo.bar(name = \"baaa\")\n" //
				+ "\"test3\"\n" //
				+ "aaa = {\n" //
				+ "@line\n" //
				+ "  prop1: TEXT;\n" //
				+ "@line2(name = \"abc\",value=TRUE)\n" //
				+ "  prop2: LONG;\n" //
				+ "};";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());
		DMDLToken token = list.get(0);
		// System.out.println(token);

		DMDLToken expected = model(
				ann("@zzz"),
				ann("@abc"),
				args(),
				ann("@foo.bar"),
				args(argq("name", "baaa")),
				desc("test3"),
				word("aaa"),
				word("="),
				block(word("{"),
						prop(ann("@line"), word("prop1"), word(":"),
								word("TEXT"), word(";")),
						prop(ann("@line2"),
								args(argq("name", "abc"), arg("value", "TRUE")),
								word("prop2"), word(":"), word("LONG"),
								word(";")), word("}")), word(";"));
		// System.out.println("expected=" + expected);
		// System.out.println("actual  =" + token);
		assertEqualsToken(expected, token);

		assertEnd(actual, models);
	}

	@Test
	public void parse_join1() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "joined item_order = item % code, id + order % item_code, item_id;";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());
		DMDLToken token = list.get(0);
		// System.out.println(token);

		DMDLToken expected = model(word("joined"), word("item_order"),
				word("="), word("item"), word("%"), word("code"), word(","),
				word("id"), word("+"), word("order"), word("%"),
				word("item_code"), word(","), word("item_id"), word(";"));
		// System.out.println("expected=" + expected);
		// System.out.println("actual  =" + token);
		assertEqualsToken(expected, token);

		assertEnd(actual, models);
	}

	@Test
	public void parse_join2() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "joined item_order = item -> {\n" //
				+ "    code -> code;\n"
				+ "    price -> price;\n"
				+ "} % code + order -> {\n"
				+ "    item_code -> code;\n"
				+ "    amount -> total;\n" + "} % code;\n";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());
		DMDLToken token = list.get(0);
		// System.out.println(token);

		DMDLToken expected = model(
				word("joined"),
				word("item_order"),
				word("="),
				word("item"),
				word("->"),
				block(word("{"),
						prop(word("code"), word("->"), word("code"), word(";")),
						prop(word("price"), word("->"), word("price"),
								word(";")), word("}")), //
				word("%"),
				word("code"),
				word("+"),
				word("order"),
				word("->"), //
				block(word("{"),
						prop(word("item_code"), word("->"), word("code"),
								word(";")),
						prop(word("amount"), word("->"), word("total"),
								word(";")), word("}")), //
				word("%"), word("code"), word(";"));
		// System.out.println("expected=" + expected);
		// System.out.println("actual  =" + token);
		assertEqualsToken(expected, token);

		assertEnd(actual, models);
	}

	@Test
	public void parse_summarize() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "\"test4\"\n" //
				+ "summarized word_count_total = word_count_model => {\n"
				+ "  any   word  -> word;\n"
				+ "  count count -> count;\n"
				+ "};";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());
		DMDLToken token = list.get(0);
		// System.out.println(token);

		DMDLToken expected = model(
				desc("test4"),
				word("summarized"),
				word("word_count_total"),
				word("="),
				word("word_count_model"),
				word("=>"),
				block(word("{"),
						prop(word("any"), word("word"), word("->"),
								word("word"), word(";")),
						prop(word("count"), word("count"), word("->"),
								word("count"), word(";")), word("}")),
				word(";"));
		// System.out.println("expected=" + expected);
		// System.out.println("actual  =" + token);
		assertEqualsToken(expected, token);

		assertEnd(actual, models);
	}

	@Test
	public void parse_summarize2() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "summarized order_summary = order => {\n"
				+ "    any item_code -> code;\n" + "    sum price -> total;\n"
				+ "    count item_code -> count;\n" + "} % code;\n";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(1, list.size());
		DMDLToken token = list.get(0);
		// System.out.println(token);

		DMDLToken expected = model(
				word("summarized"),
				word("order_summary"),
				word("="),
				word("order"),
				word("=>"),
				block(word("{"),
						prop(word("any"), word("item_code"), word("->"),
								word("code"), word(";")),
						prop(word("sum"), word("price"), word("->"),
								word("total"), word(";")),
						prop(word("count"), word("item_code"), word("->"),
								word("count"), word(";")), word("}")),
				word("%"), word("code"), word(";"));
		// System.out.println("expected=" + expected);
		// System.out.println("actual  =" + token);
		assertEqualsToken(expected, token);

		assertEnd(actual, models);
	}

	@Test
	public void parse_projective1() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "projective proj_model = {\n"//
				+ "    value : INT;\n"//
				+ "};\n" + "\n" //
				+ "conc_model = proj_model + {" //
				+ "    other : INT;" //
				+ "};\n";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(2, list.size());

		DMDLToken expected0 = model(
				word("projective"),
				word("proj_model"),
				word("="),
				block(word("{"),
						prop(word("value"), word(":"), word("INT"), word(";")),
						word("}")), word(";"));
		DMDLToken expected1 = model(
				word("conc_model"),
				word("="),
				word("proj_model"),
				word("+"),
				block(word("{"),
						prop(word("other"), word(":"), word("INT"), word(";")),
						word("}")), word(";"));
		assertEqualsToken(expected0, list.get(0));
		assertEqualsToken(expected1, list.get(1));

		assertEnd(actual, models);
	}

	@Test
	public void parse_projective2() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "projective super_proj = { a : INT; };\n"
				+ "projective sub_proj = super_proj + { b : INT; };\n"
				+ "record = sub_proj;\n";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(3, list.size());

		DMDLToken expected0 = model(
				word("projective"),
				word("super_proj"),
				word("="),
				block(word("{"),
						prop(word("a"), word(":"), word("INT"), word(";")),
						word("}")), word(";"));
		DMDLToken expected1 = model(
				word("projective"),
				word("sub_proj"),
				word("="),
				word("super_proj"),
				word("+"),
				block(word("{"),
						prop(word("b"), word(":"), word("INT"), word(";")),
						word("}")), word(";"));
		DMDLToken expected2 = model(word("record"), word("="),
				word("sub_proj"), word(";"));
		assertEqualsToken(expected0, list.get(0));
		assertEqualsToken(expected1, list.get(1));
		assertEqualsToken(expected2, list.get(2));

		assertEnd(actual, models);
	}

	@Test
	public void parse_comment() {
		DMDLSimpleParser parser = new DMDLSimpleParser();
		String actual = "item = {\n"
				+ "    code : LONG; -- XYZコード体系で表現される商品コード\n"
				+ "    id : TEXT;\n" //
				+ "//  name : TEXT;\n" //
				+ "};\n" //
				+ "\n" //
				+ "/*\n" //
				+ "order = {\n" //
				+ "    item_code : LONG;\n" //
				+ "    item_id : TEXT;\n" //
				+ "    name : TEXT;\n" //
				+ "};\n" //
				+ "*/\n";

		ModelList models = parser.parse(new StringScanner(actual));
		List<DMDLToken> list = models.getBody();

		assertEquals(2, list.size());

		DMDLToken expected0 = model(
				word("item"),
				word("="),
				block(word("{"),
						prop(word("code"), word(":"), word("LONG"), word(";")),
						prop(comm("-- XYZコード体系で表現される商品コード\n"), word("id"),
								word(":"), word("TEXT"), word(";")),
						prop(comm("//  name : TEXT;\n")), word("}")), word(";"));
		DMDLToken expected1 = model(comm("/*\n" //
				+ "order = {\n" //
				+ "    item_code : LONG;\n" //
				+ "    item_id : TEXT;\n" //
				+ "    name : TEXT;\n" //
				+ "};\n" //
				+ "*/"));
		assertEqualsToken(expected0, list.get(0));
		assertEqualsToken(expected1, list.get(1));

		assertEnd(actual, models);
	}
}
