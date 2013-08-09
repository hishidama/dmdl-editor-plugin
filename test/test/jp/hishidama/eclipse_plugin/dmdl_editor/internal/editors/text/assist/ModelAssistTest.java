package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.assist;

import static org.junit.Assert.assertNull;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.junit.Test;

public class ModelAssistTest extends AssistTest {

	@Test
	public void modelName() {
		ModelAssist assist = new ModelAssist();
		DMDLDocument document = createDocument("a");
		int offset = 1;
		ModelToken token = getModel(document);
		List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
		assertEqualsList(list, "=");
	}

	@Test
	public void summarized() {
		prefix("summarized");
	}

	@Test
	public void joined() {
		prefix("joined");
	}

	@Test
	public void projective() {
		prefix("projective");
	}

	private void prefix(String text) {
		ModelAssist assist = new ModelAssist();
		{
			for (int i = 1; i < text.length(); i++) {
				DMDLDocument document = createDocument(text.substring(0, i));
				int offset = i;
				ModelToken token = getModel(document);
				List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
				assertEqualsList(list, text, "=");
			}
		}
		{
			DMDLDocument document = createDocument(text);
			int offset = text.length();
			ModelToken token = getModel(document);
			List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
			assertNull(list);
		}
		{
			DMDLDocument document = createDocument(text);
			ModelToken token = getModel(document);
			for (int i = 1; i < text.length(); i++) {
				int offset = i;
				List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
				assertEqualsList(list, text, "=");
			}
		}
		{
			DMDLDocument document = createDocument(text + " name");
			int offset = text.length() + 5;
			ModelToken token = getModel(document);
			List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
			assertEqualsList(list, "=");
		}
		{
			DMDLDocument document = createDocument(text + " name ");
			int offset = text.length() + 6;
			ModelToken token = getModel(document);
			List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
			assertEqualsList(list, "=");
		}
	}

	@Test
	public void s_eq() {
		ModelAssist assist = new ModelAssist();
		{
			DMDLDocument document = createDocument("s =");
			int offset = 1;
			ModelToken token = getModel(document);
			List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
			assertEqualsList(list, "summarized", "=");
		}
		{
			DMDLDocument document = createDocument("s =");
			int offset = 2;
			ModelToken token = getModel(document);
			List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
			assertEqualsList(list, "=");
		}
	}

	@Test
	public void s_() {
		ModelAssist assist = new ModelAssist();
		DMDLDocument document = createDocument("s ");
		int offset = 2;
		ModelToken token = getModel(document);
		List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
		assertEqualsList(list, "=");
	}

	@Test
	public void seq() {
		ModelAssist assist = new ModelAssist();
		DMDLDocument document = createDocument("s=");
		int offset = 1;
		ModelToken token = getModel(document);
		List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
		assertEqualsList(list, "summarized", "=");
	}

	@Test
	public void block() {
		ModelAssist assist = new ModelAssist();
		test2(assist, "a=", "{\n};");
	}

	@Test
	public void block_end() {
		ModelAssist assist = new ModelAssist();
		test2(assist, "a={}", ";");
	}

	// summarized

	@Test
	public void summarized_ref() {
		ModelAssist assist = new ModelAssist() {
			@Override
			protected String[] getModelNames(DMDLToken token, String modelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		test2(assist, "summarized s=", "abc", "aaa", "def");
	}

	@Test
	public void summarized_ref1() {
		ModelAssist assist = new ModelAssist() {
			@Override
			protected String[] getModelNames(DMDLToken token, String modelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		test1(assist, "summarized s=a", "abc", "aaa", "=> {\n}");
	}

	@Test
	public void summarized_end() {
		ModelAssist assist = new ModelAssist();
		test2(assist, "summarized s=a => { any abc->abc; }", "%", ";");
	}

	@Test
	public void summarized_key() {
		ModelAssist assist = new ModelAssist();
		test2(assist, "summarized s=a => { any abc->abc; any aaa->aaa; } %", "abc", "aaa");
	}

	@Test
	public void summarized_key_name() {
		ModelAssist assist = new ModelAssist();
		test1(assist, "summarized s=a => { any abc->abc; any aaa->bbb; } % a", "abc");
	}

	@Test
	public void summarized_key_name_match() {
		ModelAssist assist = new ModelAssist();
		test2(assist, "summarized s=a => { any abc->abc; any aaa->bbb; } % abc", ",", ";");
	}

	@Test
	public void summarized_key_name_() {
		ModelAssist assist = new ModelAssist();
		test1(assist, "summarized s=a => { any abc->abc; any aaa->bbb; } % a ", ",", ";");
	}

	@Test
	public void summarized_key_name_notFound() {
		ModelAssist assist = new ModelAssist();
		test1(assist, "summarized s=a => { any abc->abc; any aaa->bbb; } % zzz", ",", ";");
	}

	@Test
	public void summarized_key2() {
		ModelAssist assist = new ModelAssist();
		test2(assist, "summarized s=a => { any abc->abc; any aaa->bbb; } % abc,", "abc", "bbb");
	}

	@Test
	public void summarized_key2_1() {
		ModelAssist assist = new ModelAssist();
		test1(assist, "summarized s=a => { any abc->abc; any aaa->bbb; } % abc,b", "bbb");
	}

	// joined

	@Test
	public void joined_ref() {
		ModelAssist assist = new ModelAssist() {
			@Override
			protected String[] getModelNames(DMDLToken token, String modelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		test2(assist, "joined j=", "abc", "aaa", "def");
	}

	@Test
	public void joined_ref1() {
		ModelAssist assist = new ModelAssist() {
			@Override
			protected String[] getModelNames(DMDLToken token, String modelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		test1(assist, "joined j=a", "abc", "aaa", "-> {\n}", "%");
	}

	@Test
	public void joined_key() {
		ModelAssist assist = new ModelAssist();
		test2(assist, "joined j=a -> { abc->abc; aaa->aaa; } %", "abc", "aaa");
	}

	@Test
	public void joined_key_name() {
		ModelAssist assist = new ModelAssist();
		test1(assist, "joined j=a -> { abc->abc; aaa->aaa; } % a", "abc", "aaa");
	}

	@Test
	public void joined_key_name2() {
		ModelAssist assist = new ModelAssist();
		test1(assist, "joined j=a -> { abc->abc; aaa->aaa; } % ab", "abc");
	}

	@Test
	public void joined_key_match() {
		ModelAssist assist = new ModelAssist();
		test2(assist, "joined j=a -> { abc->abc; aaa->aaa; } % abc", ",", ";", "+");
	}

	@Test
	public void joined_key2() {
		ModelAssist assist = new ModelAssist();
		test2(assist, "joined j=a -> { abc->abc; aaa->bbb; } % abc,", "abc", "bbb");
	}

	@Test
	public void joined_key2_1() {
		ModelAssist assist = new ModelAssist();
		test1(assist, "joined j=a -> { abc->abc; aaa->bbb; } % abc,a", "abc");
	}

	@Test
	public void joined_ref2() {
		ModelAssist assist = new ModelAssist() {
			@Override
			protected String[] getModelNames(DMDLToken token, String modelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		test2(assist, "joined j=zzz+", "abc", "aaa", "def");
	}

	@Test
	public void joined2_name() {
		ModelAssist assist = new ModelAssist();
		test2(assist, "joined j=a % abc+ b", "-> {\n}", "%");
	}

	@Test
	public void joined2_end() {
		ModelAssist assist = new ModelAssist();
		test2(assist, "joined j=a % abc+ b -> {bbb->bbb;ccc->ccc;}", "%", ";");
	}

	@Test
	public void joined2_key() {
		ModelAssist assist = new ModelAssist();
		test2(assist, "joined j=a % abc+ b -> {bbb->bbb;ccc->ccc;}%", "bbb", "ccc");
	}

	@Test
	public void joined2_key1() {
		ModelAssist assist = new ModelAssist();
		test1(assist, "joined j=a % abc+ b -> {bbb->bbb;ccc->ccc;}%b", "bbb");
	}

	@Test
	public void joined2_key_match() {
		ModelAssist assist = new ModelAssist();
		test2(assist, "joined j=a % abc+ b -> {bbb->bbb;ccc->ccc;}%bbb", ",", ";");
	}

	@Test
	public void joined2_key2() {
		ModelAssist assist = new ModelAssist();
		test2(assist, "joined j=a % abc+ b -> {bbb->bbb;ccc->ddd;} % bbb,", "bbb", "ddd");
	}

	@Test
	public void joined2_key2_1() {
		ModelAssist assist = new ModelAssist();
		test1(assist, "joined j=a % abc+ b -> {bbb->bbb;ccc->ddd;} % bbb,d", "ddd");
	}

	@Test
	public void joined2_key2_match() {
		ModelAssist assist = new ModelAssist();
		test2(assist, "joined j=a % abc+ b -> {bbb->bbb;ccc->ddd;} % bbb,ddd", ",", ";");
	}

	// projective

	@Test
	public void projective_ref() {
		ModelAssist assist = new ModelAssist() {
			@Override
			protected String[] getModelNames(DMDLToken token, String modelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		test2(assist, "projective p=", "abc", "aaa", "def");
	}

	@Test
	public void projective_ref1() {
		ModelAssist assist = new ModelAssist() {
			@Override
			protected String[] getModelNames(DMDLToken token, String modelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		test1(assist, "projective p=a", "abc", "aaa", "-> {\n}", "+");
	}

	private static void test1(ModelAssist assist, String text, String... expected) {
		DMDLDocument document = createDocument(text);
		int offset = text.length();
		ModelToken token = getModel(document);
		List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
		assertEqualsList(list, expected);
	}

	private static void test2(ModelAssist assist, String text, String... expected) {
		test1(assist, text, expected);
		test1(assist, text + " ", expected);
	}
}
