package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.DMDLSimpleParser;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.StringScanner;

import org.junit.Test;

/**
 * {@link DMDLBodyToken#getPropertyList()}のテスト.
 */
@SuppressWarnings("unchecked")
public class GetPropertyListTest {

	protected static final String[] TYPES = { "BYTE", "SHORT", "INT", "LONG",
			"FLOAT", "DOUBLE", "DECIMAL", "BOOLEAN", "TEXT", "DATE", "DATETIME" };
	protected static final String[] SUM_TYPES = { "LONG", "LONG", "LONG",
			"LONG", "DOUBLE", "DOUBLE", "DECIMAL", null, null, null, null };

	@Test
	public void simple() {
		String actual = "simple={\n" + "  v1:INT;\n" + "};";

		DMDLSimpleParser parser = new DMDLSimpleParser();
		ModelList models = parser.parse(new StringScanner(actual));

		List<Property> expected = Arrays.asList(prop("v1", "INT"));
		assertProperties(models, expected);
	}

	@Test
	public void simple2() {
		List<Property> expected = new ArrayList<Property>();

		StringBuilder sb = new StringBuilder(128);
		sb.append("simple={\n");
		for (int i = 0; i < TYPES.length; i++) {
			sb.append(String.format("  v%d : %s;\n", i, TYPES[i]));
			expected.add(prop("v" + i, TYPES[i]));
		}
		sb.append("};");
		String actual = sb.toString();

		DMDLSimpleParser parser = new DMDLSimpleParser();
		ModelList models = parser.parse(new StringScanner(actual));

		assertProperties(models, expected);
	}

	@Test
	public void sum() {
		List<Property> expected1 = new ArrayList<Property>();
		StringBuilder sb1 = new StringBuilder(128);
		sb1.append("simple={\n");
		for (int i = 0; i < TYPES.length; i++) {
			sb1.append(String.format("  v%d : %s;\n", i, TYPES[i]));
			expected1.add(prop("v" + i, TYPES[i]));
		}
		sb1.append("};");

		List<Property> expected2 = new ArrayList<Property>();
		StringBuilder sb2 = new StringBuilder(128);
		sb2.append("summarized sum_item = simple => {\n");
		for (int i = 0; i < TYPES.length; i++) {
			sb2.append(String.format("  any v%d -> av%d;\n", i, i));
			expected2.add(prop("av" + i, TYPES[i]));
		}
		for (int i = 0; i < TYPES.length; i++) {
			sb2.append(String.format("  sum v%d -> sv%d;\n", i, i));
			expected2.add(prop("sv" + i, SUM_TYPES[i]));
		}
		for (int i = 0; i < TYPES.length; i++) {
			sb2.append(String.format("  count v%d -> cv%d;\n", i, i));
			expected2.add(prop("cv" + i, "LONG"));
		}
		for (int i = 0; i < TYPES.length; i++) {
			sb2.append(String.format("  min v%d -> nv%d;\n", i, i));
			expected2.add(prop("nv" + i, TYPES[i]));
		}
		for (int i = 0; i < TYPES.length; i++) {
			sb2.append(String.format("  max v%d -> xv%d;\n", i, i));
			expected2.add(prop("xv" + i, TYPES[i]));
		}
		sb2.append("};");

		String actual = sb1 + "\n" + sb2;

		DMDLSimpleParser parser = new DMDLSimpleParser();
		ModelList models = parser.parse(new StringScanner(actual));

		assertProperties(models, expected1, expected2);
	}

	@Test
	public void ref_simple() {
		String actual1 = "simple={\n" + "  v1:INT;\n" + "  v2 : TEXT" + "};";
		String actual2 = "ref1 = simple;";
		String actual = actual1 + "\n" + actual2;

		DMDLSimpleParser parser = new DMDLSimpleParser();
		ModelList models = parser.parse(new StringScanner(actual));

		List<Property> expected1 = Arrays.asList(prop("v1", "INT"),
				prop("v2", "TEXT"));
		assertProperties(models, expected1, expected1);
	}

	@Test
	public void ref_simple2() {
		String actual1 = "simple1={\n" + "  v1 : INT;\n" + "  v2 : TEXT" + "};";
		String actual2 = "simple2={\n" + "  v3 : LONG;\n" + "  v4 : BOOLEAN"
				+ "};";
		String actual3 = "ref1 = simple1 + simple2;";
		String actual = actual1 + "\n" + actual2 + "\n" + actual3;

		DMDLSimpleParser parser = new DMDLSimpleParser();
		ModelList models = parser.parse(new StringScanner(actual));

		List<Property> expected1 = Arrays.asList(prop("v1", "INT"),
				prop("v2", "TEXT"));
		List<Property> expected2 = Arrays.asList(prop("v3", "LONG"),
				prop("v4", "BOOLEAN"));
		List<Property> expected3 = new ArrayList<Property>(expected1);
		expected3.addAll(expected2);
		assertProperties(models, expected1, expected2, expected3);
	}

	@Test
	public void ref_simple3() {
		String actual1 = "simple={\n" + "  v1:INT;\n" + "  v2 : TEXT" + "};";
		String actual2 = "ref1 = simple -> { v1 -> v3; };";
		String actual = actual1 + "\n" + actual2;

		DMDLSimpleParser parser = new DMDLSimpleParser();
		ModelList models = parser.parse(new StringScanner(actual));

		List<Property> expected1 = Arrays.asList(prop("v1", "INT"),
				prop("v2", "TEXT"));
		List<Property> expected2 = Arrays.asList(prop("v3", "INT"));
		assertProperties(models, expected1, expected2);
	}

	@Test
	public void ref_simple4() {
		String actual1 = "simple1={\n" + "  v1 : INT;\n" + "  v2 : TEXT" + "};";
		String actual2 = "simple2={\n" + "  v3 : LONG;\n" + "  v4 : BOOLEAN"
				+ "};";
		String actual3 = "ref1 = simple1 -> { v1->v6; } + simple2;";
		String actual = actual1 + "\n" + actual2 + "\n" + actual3;

		DMDLSimpleParser parser = new DMDLSimpleParser();
		ModelList models = parser.parse(new StringScanner(actual));

		List<Property> expected1 = Arrays.asList(prop("v1", "INT"),
				prop("v2", "TEXT"));
		List<Property> expected2 = Arrays.asList(prop("v3", "LONG"),
				prop("v4", "BOOLEAN"));
		List<Property> expected3 = Arrays.asList(prop("v6", "INT"),
				prop("v3", "LONG"), prop("v4", "BOOLEAN"));
		assertProperties(models, expected1, expected2, expected3);
	}

	@Test
	public void ref_simple5() {
		String actual1 = "simple1={\n" + "  v1 : INT;\n" + "  v2 : TEXT" + "};";
		String actual2 = "simple2={\n" + "  v3 : LONG;\n" + "  v4 : BOOLEAN"
				+ "};";
		String actual3 = "ref1 = simple1 + simple2 -> { v4->v6; };";
		String actual = actual1 + "\n" + actual2 + "\n" + actual3;

		DMDLSimpleParser parser = new DMDLSimpleParser();
		ModelList models = parser.parse(new StringScanner(actual));

		List<Property> expected1 = Arrays.asList(prop("v1", "INT"),
				prop("v2", "TEXT"));
		List<Property> expected2 = Arrays.asList(prop("v3", "LONG"),
				prop("v4", "BOOLEAN"));
		List<Property> expected3 = Arrays.asList(prop("v1", "INT"),
				prop("v2", "TEXT"), prop("v6", "BOOLEAN"));
		assertProperties(models, expected1, expected2, expected3);
	}

	@Test
	public void ref_simple6() {
		String actual1 = "simple1={\n" + "  v1 : INT;\n" + "  v2 : TEXT" + "};";
		String actual2 = "simple2={\n" + "  v3 : LONG;\n" + "  v4 : BOOLEAN"
				+ "};";
		String actual3 = "ref1 = simple1->{v1->v6;} + simple2 -> { v3 -> v7 ; };";
		String actual = actual1 + "\n" + actual2 + "\n" + actual3;

		DMDLSimpleParser parser = new DMDLSimpleParser();
		ModelList models = parser.parse(new StringScanner(actual));

		List<Property> expected1 = Arrays.asList(prop("v1", "INT"),
				prop("v2", "TEXT"));
		List<Property> expected2 = Arrays.asList(prop("v3", "LONG"),
				prop("v4", "BOOLEAN"));
		List<Property> expected3 = Arrays.asList(prop("v6", "INT"),
				prop("v7", "LONG"));
		assertProperties(models, expected1, expected2, expected3);
	}

	@Test
	public void join1() {
		String actual1 = "simple1={\n" + "  v1 : INT;\n" + "  v2 : TEXT;\n"
				+ "  v3 : LONG;\n" + "};";
		String actual2 = "simple2={\n" + "  v1 : INT;\n" + "  a2 : BOOLEAN;\n"
				+ "  a3 : TEXT;\n" + "};";
		String actual3 = "joined join1 = simple1  % v1 + simple2 % v1;";
		String actual = actual1 + "\n" + actual2 + "\n" + actual3;

		DMDLSimpleParser parser = new DMDLSimpleParser();
		ModelList models = parser.parse(new StringScanner(actual));

		List<Property> expected1 = Arrays.asList(prop("v1", "INT"),
				prop("v2", "TEXT"), prop("v3", "LONG"));
		List<Property> expected2 = Arrays.asList(prop("v1", "INT"),
				prop("a2", "BOOLEAN"), prop("a3", "TEXT"));
		List<Property> expected3 = Arrays.asList(prop("v1", "INT"),
				prop("v2", "TEXT"), prop("v3", "LONG"), prop("a2", "BOOLEAN"),
				prop("a3", "TEXT"));
		assertProperties(models, expected1, expected2, expected3);
	}

	@Test
	public void join2() {
		String actual1 = "simple1={\n" + "  v1 : INT;\n" + "  v2 : TEXT;\n"
				+ "  v3 : LONG;\n" + "};";
		String actual2 = "simple2={\n" + "  a1 : INT;\n" + "  a2 : BOOLEAN;\n"
				+ "  a3 : TEXT;\n" + "};";
		String actual3 = "joined join1 = simple1 -> { v1->key; v2->v2; }  % key\n"
				+ "+ simple2 -> { a1->key; a3->a3; }% key;";
		String actual = actual1 + "\n" + actual2 + "\n" + actual3;

		DMDLSimpleParser parser = new DMDLSimpleParser();
		ModelList models = parser.parse(new StringScanner(actual));

		List<Property> expected1 = Arrays.asList(prop("v1", "INT"),
				prop("v2", "TEXT"), prop("v3", "LONG"));
		List<Property> expected2 = Arrays.asList(prop("a1", "INT"),
				prop("a2", "BOOLEAN"), prop("a3", "TEXT"));
		List<Property> expected3 = Arrays.asList(prop("key", "INT"),
				prop("v2", "TEXT"), prop("a3", "TEXT"));
		assertProperties(models, expected1, expected2, expected3);
	}

	// assertion

	protected void assertProperties(ModelList actual,
			List<Property>... expected) {
		assertEquals(expected.length, actual.getBody().size());
		for (int i = 0; i < expected.length; i++) {
			List<Property> list = expected[i];
			ModelToken model = (ModelToken) actual.getBody().get(i);
			List<PropertyToken> props = model.getPropertyList();
			assertEquals("model=" + model, list.size(), props.size());

			for (int j = 0; j < list.size(); j++) {
				Property ex = list.get(j);
				PropertyToken ac = props.get(j);
				assertEquals("prop=" + ac, ex.name, ac.getName());
				assertEquals("prop=" + ac, ex.type, ac.getDataType(null));
			}
		}
	}

	// expected

	protected Property prop(String name, String type) {
		return new Property(name, type);
	}

	protected static class Property {
		public String name;
		public String type;

		public Property(String name, String type) {
			this.name = name;
			this.type = type;
		}
	}
}
