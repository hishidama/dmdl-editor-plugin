package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.assist;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.junit.Test;

public class PropertyAssistTest extends AssistTest {

	@Test
	public void normal() {
		PropertyAssist assist = new PropertyAssist();
		test2(assist, "s = { a", ":");
	}

	@Test
	public void normal_colon() {
		PropertyAssist assist = new PropertyAssist();
		test2(assist, "s = { a :", WordToken.PROPERTY_TYPE);
	}

	@Test
	public void normal_type() {
		PropertyAssist assist = new PropertyAssist();
		test1(assist, "s = { a : i", "INT");
	}

	@Test
	public void normal_type_match() {
		PropertyAssist assist = new PropertyAssist();
		test2(assist, "s = { a : INT", ";");
	}

	@Test
	public void normal_end_() {
		PropertyAssist assist = new PropertyAssist();
		test1(assist, "s = { a : IN ", ";");
	}

	@Test
	public void summarized() {
		PropertyAssist assist = new PropertyAssist();
		test1(assist, "summarized s = a=>{ a", "any");
	}

	@Test
	public void summarized_from() {
		PropertyAssist assist = new PropertyAssist();
		test1(assist, "summarized s = a=>{ any c", "->");
	}

	@Test
	public void summarized_from_() {
		PropertyAssist assist = new PropertyAssist();
		test1(assist, "summarized s = a=>{ any c ", "->");
	}

	@Test
	public void summarized_arrow() {
		PropertyAssist assist = new PropertyAssist();
		test2(assist, "summarized s = a=>{ any c ->", "c");
	}

	@Test
	public void summarized_end() {
		PropertyAssist assist = new PropertyAssist();
		test2(assist, "summarized s = a=>{ any c -> n", ";");
	}

	@Test
	public void joined() {
		PropertyAssist assist = new PropertyAssist() {
			@Override
			protected String[] getRefProperties(WordToken refModelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		test1(assist, "joined s = a->{ a", "abc", "aaa");
	}

	@Test
	public void joined_() {
		PropertyAssist assist = new PropertyAssist();
		test1(assist, "joined s = a->{ b ", "->");
	}

	@Test
	public void joined_match() {
		PropertyAssist assist = new PropertyAssist() {
			@Override
			protected String[] getRefProperties(WordToken refModelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		test2(assist, "joined s = a->{ aaa", "->");
	}

	@Test
	public void joined_notFound() {
		PropertyAssist assist = new PropertyAssist() {
			@Override
			protected String[] getRefProperties(WordToken refModelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		test2(assist, "joined s = a->{ aaaa", "->");
	}

	private static void test1(PropertyAssist assist, String text, String... expected) {
		DMDLDocument document = createDocument(text);
		int offset = text.length();
		PropertyToken token = getProperty(document);
		List<ICompletionProposal> list = assist.getPropertyAssist(document, offset, token);
		assertEqualsList(list, expected);
	}

	private static void test2(PropertyAssist assist, String text, String... expected) {
		test1(assist, text, expected);
		test1(assist, text + " ", expected);
	}

	protected static PropertyToken getProperty(DMDLDocument document) {
		ModelToken model = getModel(document);
		List<PropertyToken> list = model.getOwnPropertyList();
		return list.get(0);
	}
}
