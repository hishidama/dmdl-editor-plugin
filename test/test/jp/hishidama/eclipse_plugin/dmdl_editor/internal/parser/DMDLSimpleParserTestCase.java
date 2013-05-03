package jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.AnnotationToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ArgumentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ArgumentsToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ArrayToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.BlockToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.CommentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLBodyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLTextToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DescriptionToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken;

public class DMDLSimpleParserTestCase {

	// assertion

	public static void assertEqualsToken(DMDLToken expected, DMDLToken actual) {
		assertEquals("expected=" + expected + "\nactual=" + actual,
				expected.getClass(), actual.getClass());
		if (expected instanceof DMDLTextToken) {
			DMDLTextToken ac = (DMDLTextToken) actual;
			DMDLTextToken ec = (DMDLTextToken) expected;
			String as = ac.getText();
			String es = ec.getText();
			assertEquals("expected=" + ec + "\nactual=" + ac, es, as);
			return;
		}
		if (expected instanceof DMDLBodyToken) {
			DMDLBodyToken ac = (DMDLBodyToken) actual;
			DMDLBodyToken ec = (DMDLBodyToken) expected;
			List<DMDLToken> alist = ac.getBody();
			List<DMDLToken> elist = ec.getBody();
			assertEqualsToken(elist, alist);
			return;
		}
		throw new UnsupportedOperationException("class=" + expected);
	}

	public static void assertEqualsToken(List<DMDLToken> elist,
			List<DMDLToken> alist) {
		assertEquals("list.size expected=" + elist + "\nactual=" + alist,
				elist.size(), alist.size());
		for (int i = 0; i < elist.size(); i++) {
			DMDLToken a = alist.get(i);
			DMDLToken e = elist.get(i);
			assertEqualsToken(e, a);
		}
	}

	public static void assertEnd(String expected, DMDLToken token) {
		assertTrue(token.getEnd() <= expected.length());

		if (token instanceof DMDLBodyToken) {
			List<DMDLToken> list = ((DMDLBodyToken) token).getBody();

			int end = token.getStart();
			for (DMDLToken t : list) {
				assertTrue(end <= t.getStart());
				end = t.getEnd();
			}

			DMDLToken last = list.get(list.size() - 1);
			assertTrue("last=" + last, last.getEnd() <= token.getEnd());

			assertEnd(expected, last);
		}
	}

	// create expected token

	public CommentToken comm(String s) {
		return new CommentToken(0, 0, s, false);
	}

	public ModelToken model(DMDLToken... ts) {
		List<DMDLToken> list = new ArrayList<DMDLToken>(ts.length);
		for (DMDLToken t : ts) {
			list.add(t);
		}
		return new ModelToken(0, 0, list);
	}

	public BlockToken block(DMDLToken... ts) {
		List<DMDLToken> list = new ArrayList<DMDLToken>(ts.length);
		for (DMDLToken t : ts) {
			list.add(t);
		}
		return new BlockToken(0, 0, list);
	}

	public PropertyToken prop(DMDLToken... ts) {
		List<DMDLToken> list = new ArrayList<DMDLToken>(ts.length);
		for (DMDLToken t : ts) {
			list.add(t);
		}
		return new PropertyToken(0, 0, list);
	}

	public DescriptionToken desc(String s) {
		return desc(s, true);
	}

	public DescriptionToken desc(String s, boolean close) {
		if (close) {
			return new DescriptionToken(0, 0, "\"" + s + "\"");
		} else {
			return new DescriptionToken(0, 0, "\"" + s);
		}
	}

	public AnnotationToken ann(String text) {
		return new AnnotationToken(0, 0, text);
	}

	public ArgumentsToken args(ArgumentToken... ts) {
		return args(true, ts);
	}

	public ArgumentsToken args(boolean close, ArgumentToken... ts) {
		List<DMDLToken> list = new ArrayList<DMDLToken>(ts.length * 2 + 2);
		list.add(new WordToken(0, 0, "("));
		boolean first = true;
		for (DMDLToken t : ts) {
			if (!first) {
				list.add(new WordToken(0, 0, ","));
			}
			first = false;

			list.add(t);
		}
		if (close) {
			list.add(new WordToken(0, 0, ")"));
		}
		return new ArgumentsToken(0, 0, list);
	}

	public ArgumentToken arg(String name, DMDLToken value) {
		List<DMDLToken> list = new ArrayList<DMDLToken>();
		list.add(new WordToken(0, 0, name));
		list.add(new WordToken(0, 0, "="));
		list.add(value);
		return new ArgumentToken(0, 0, list);
	}

	public ArgumentToken arg(String name, String value) {
		return arg(name, new WordToken(0, 0, value));
	}

	public ArgumentToken argq(String name, String value) {
		return arg(name, new DescriptionToken(0, 0, "\"" + value + "\""));
	}

	public ArgumentToken arg(DMDLToken... ts) {
		List<DMDLToken> list = new ArrayList<DMDLToken>();
		for (DMDLToken t : ts) {
			list.add(t);
		}
		return new ArgumentToken(0, 0, list);
	}

	public ArrayToken arr(String... value) {
		return arr(true, value);
	}

	public ArrayToken arr(boolean close, String... value) {
		List<DMDLToken> list = new ArrayList<DMDLToken>(value.length * 2 + 2);
		list.add(new WordToken(0, 0, "{"));
		boolean first = true;
		for (String s : value) {
			if (first) {
				first = false;
			} else {
				list.add(new WordToken(0, 0, ","));
			}
			if (s.startsWith("\"")) {
				list.add(new DescriptionToken(0, 0, s));
			} else {
				list.add(new WordToken(0, 0, s));
			}
		}
		if (close) {
			list.add(new WordToken(0, 0, "}"));
		}
		return new ArrayToken(0, 0, list);
	}

	public WordToken word(String s) {
		return new WordToken(0, 0, s);
	}
}
