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
		DMDLDocument document = createDocument("a=");
		int offset = 2;
		ModelToken token = getModel(document);
		List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
		assertEqualsList(list, "{\n};");
	}

	@Test
	public void block_end() {
		ModelAssist assist = new ModelAssist();
		DMDLDocument document = createDocument("a={}");
		int offset = 4;
		ModelToken token = getModel(document);
		List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
		assertEqualsList(list, ";");
	}

	@Test
	public void block_end2() {
		ModelAssist assist = new ModelAssist();
		DMDLDocument document = createDocument("a={} ");
		int offset = 5;
		ModelToken token = getModel(document);
		List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
		assertEqualsList(list, ";");
	}

	@Test
	public void summarized_ref() {
		ModelAssist assist = new ModelAssist() {
			@Override
			protected String[] getModelNames(DMDLToken token, String modelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		DMDLDocument document = createDocument("summarized s=");
		int offset = 13;
		ModelToken token = getModel(document);
		List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
		assertEqualsList(list, "abc", "aaa", "def");
	}

	@Test
	public void summarized_ref1() {
		ModelAssist assist = new ModelAssist() {
			@Override
			protected String[] getModelNames(DMDLToken token, String modelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		DMDLDocument document = createDocument("summarized s=a");
		int offset = 14;
		ModelToken token = getModel(document);
		List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
		assertEqualsList(list, "abc", "aaa", "=> {\n}");
	}

	@Test
	public void summarized_key() {
		ModelAssist assist = new ModelAssist();
		DMDLDocument document = createDocument("summarized s=a => { any abc->abc; any aaa->aaa; } %");
		int offset = 51;
		ModelToken token = getModel(document);
		List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
		assertEqualsList(list, "abc", "aaa");
	}

	@Test
	public void summarized_key_name() {
		ModelAssist assist = new ModelAssist();
		DMDLDocument document = createDocument("summarized s=a => { any abc->abc; any aaa->bbb; } % a");
		int offset = 53;
		ModelToken token = getModel(document);
		List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
		assertEqualsList(list, "abc");
	}

	@Test
	public void summarized_key_name_match() {
		ModelAssist assist = new ModelAssist();
		DMDLDocument document = createDocument("summarized s=a => { any abc->abc; any aaa->bbb; } % abc");
		int offset = 55;
		ModelToken token = getModel(document);
		List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
		assertEqualsList(list, ",", ";");
	}

	@Test
	public void summarized_key_name_() {
		ModelAssist assist = new ModelAssist();
		DMDLDocument document = createDocument("summarized s=a => { any abc->abc; any aaa->bbb; } % a ");
		int offset = 54;
		ModelToken token = getModel(document);
		List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
		assertEqualsList(list, ",", ";");
	}

	@Test
	public void summarized_key_name_notFound() {
		ModelAssist assist = new ModelAssist();
		DMDLDocument document = createDocument("summarized s=a => { any abc->abc; any aaa->bbb; } % zzz");
		int offset = 55;
		ModelToken token = getModel(document);
		List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
		assertEqualsList(list, ",", ";");
	}

	@Test
	public void joined_ref() {
		ModelAssist assist = new ModelAssist() {
			@Override
			protected String[] getModelNames(DMDLToken token, String modelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		DMDLDocument document = createDocument("joined j=");
		int offset = 9;
		ModelToken token = getModel(document);
		List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
		assertEqualsList(list, "abc", "aaa", "def");
	}

	@Test
	public void joined_ref1() {
		ModelAssist assist = new ModelAssist() {
			@Override
			protected String[] getModelNames(DMDLToken token, String modelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		DMDLDocument document = createDocument("joined j=a");
		int offset = 10;
		ModelToken token = getModel(document);
		List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
		assertEqualsList(list, "abc", "aaa", "-> {\n}", "%");
	}

	@Test
	public void projective_ref() {
		ModelAssist assist = new ModelAssist() {
			@Override
			protected String[] getModelNames(DMDLToken token, String modelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		DMDLDocument document = createDocument("projective p=");
		int offset = 13;
		ModelToken token = getModel(document);
		List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
		assertEqualsList(list, "abc", "aaa", "def");
	}

	@Test
	public void projective_ref1() {
		ModelAssist assist = new ModelAssist() {
			@Override
			protected String[] getModelNames(DMDLToken token, String modelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		DMDLDocument document = createDocument("projective p=a");
		int offset = 14;
		ModelToken token = getModel(document);
		List<ICompletionProposal> list = assist.getModelAssist(document, offset, token);
		assertEqualsList(list, "abc", "aaa", "-> {\n}", "+");
	}
}
