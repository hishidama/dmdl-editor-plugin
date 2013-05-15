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
		DMDLDocument document = createDocument("s = { a");
		int offset = 7;
		PropertyToken token = getProperty(document);
		List<ICompletionProposal> list = assist.getPropertyAssist(document, offset, token);
		assertEqualsList(list, ":");
	}

	@Test
	public void normal_() {
		PropertyAssist assist = new PropertyAssist();
		DMDLDocument document = createDocument("s = { a ");
		int offset = 8;
		PropertyToken token = getProperty(document);
		List<ICompletionProposal> list = assist.getPropertyAssist(document, offset, token);
		assertEqualsList(list, ":");
	}

	@Test
	public void normal_colon() {
		PropertyAssist assist = new PropertyAssist();
		DMDLDocument document = createDocument("s = { a :");
		int offset = 9;
		PropertyToken token = getProperty(document);
		List<ICompletionProposal> list = assist.getPropertyAssist(document, offset, token);
		assertEqualsList(list, WordToken.PROPERTY_TYPE);
	}

	@Test
	public void normal_colon_() {
		PropertyAssist assist = new PropertyAssist();
		DMDLDocument document = createDocument("s = { a : ");
		int offset = 10;
		PropertyToken token = getProperty(document);
		List<ICompletionProposal> list = assist.getPropertyAssist(document, offset, token);
		assertEqualsList(list, WordToken.PROPERTY_TYPE);
	}

	@Test
	public void normal_type() {
		PropertyAssist assist = new PropertyAssist();
		DMDLDocument document = createDocument("s = { a : i");
		int offset = 11;
		PropertyToken token = getProperty(document);
		List<ICompletionProposal> list = assist.getPropertyAssist(document, offset, token);
		assertEqualsList(list, "INT");
	}

	@Test
	public void normal_end() {
		PropertyAssist assist = new PropertyAssist();
		DMDLDocument document = createDocument("s = { a : INT");
		int offset = 13;
		PropertyToken token = getProperty(document);
		List<ICompletionProposal> list = assist.getPropertyAssist(document, offset, token);
		assertEqualsList(list, ";");
	}

	@Test
	public void normal_end_() {
		PropertyAssist assist = new PropertyAssist();
		DMDLDocument document = createDocument("s = { a : IN ");
		int offset = 13;
		PropertyToken token = getProperty(document);
		List<ICompletionProposal> list = assist.getPropertyAssist(document, offset, token);
		assertEqualsList(list, ";");
	}

	@Test
	public void summarized() {
		PropertyAssist assist = new PropertyAssist();
		DMDLDocument document = createDocument("summarized s = a=>{ a");
		int offset = 21;
		PropertyToken token = getProperty(document);
		List<ICompletionProposal> list = assist.getPropertyAssist(document, offset, token);
		assertEqualsList(list, "any");
	}

	@Test
	public void summarized_from() {
		PropertyAssist assist = new PropertyAssist();
		DMDLDocument document = createDocument("summarized s = a=>{ any c");
		int offset = 25;
		PropertyToken token = getProperty(document);
		List<ICompletionProposal> list = assist.getPropertyAssist(document, offset, token);
		assertEqualsList(list, "->");
	}

	@Test
	public void summarized_from_() {
		PropertyAssist assist = new PropertyAssist();
		DMDLDocument document = createDocument("summarized s = a=>{ any c ");
		int offset = 26;
		PropertyToken token = getProperty(document);
		List<ICompletionProposal> list = assist.getPropertyAssist(document, offset, token);
		assertEqualsList(list, "->");
	}

	@Test
	public void summarized_arrow() {
		PropertyAssist assist = new PropertyAssist();
		DMDLDocument document = createDocument("summarized s = a=>{ any c ->");
		int offset = 28;
		PropertyToken token = getProperty(document);
		List<ICompletionProposal> list = assist.getPropertyAssist(document, offset, token);
		assertEqualsList(list, "c");
	}

	@Test
	public void summarized_arrow_() {
		PropertyAssist assist = new PropertyAssist();
		DMDLDocument document = createDocument("summarized s = a=>{ any c -> ");
		int offset = 29;
		PropertyToken token = getProperty(document);
		List<ICompletionProposal> list = assist.getPropertyAssist(document, offset, token);
		assertEqualsList(list, "c");
	}

	@Test
	public void summarized_end() {
		PropertyAssist assist = new PropertyAssist();
		DMDLDocument document = createDocument("summarized s = a=>{ any c -> n");
		int offset = 30;
		PropertyToken token = getProperty(document);
		List<ICompletionProposal> list = assist.getPropertyAssist(document, offset, token);
		assertEqualsList(list, ";");
	}

	@Test
	public void joined() {
		PropertyAssist assist = new PropertyAssist() {
			@Override
			protected String[] getRefProperties(WordToken refModelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		DMDLDocument document = createDocument("joined s = a->{ a");
		int offset = 17;
		PropertyToken token = getProperty(document);
		List<ICompletionProposal> list = assist.getPropertyAssist(document, offset, token);
		assertEqualsList(list, "abc", "aaa");
	}

	@Test
	public void joined_() {
		PropertyAssist assist = new PropertyAssist();
		DMDLDocument document = createDocument("joined s = a->{ b ");
		int offset = 18;
		PropertyToken token = getProperty(document);
		List<ICompletionProposal> list = assist.getPropertyAssist(document, offset, token);
		assertEqualsList(list, "->");
	}

	@Test
	public void joined_match() {
		PropertyAssist assist = new PropertyAssist() {
			@Override
			protected String[] getRefProperties(WordToken refModelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		DMDLDocument document = createDocument("joined s = a->{ aaa");
		int offset = 19;
		PropertyToken token = getProperty(document);
		List<ICompletionProposal> list = assist.getPropertyAssist(document, offset, token);
		assertEqualsList(list, "->");
	}

	@Test
	public void joined_notFound() {
		PropertyAssist assist = new PropertyAssist() {
			@Override
			protected String[] getRefProperties(WordToken refModelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		DMDLDocument document = createDocument("joined s = a->{ aaaa");
		int offset = 20;
		PropertyToken token = getProperty(document);
		List<ICompletionProposal> list = assist.getPropertyAssist(document, offset, token);
		assertEqualsList(list, "->");
	}

	protected static PropertyToken getProperty(DMDLDocument document) {
		ModelToken model = getModel(document);
		List<PropertyToken> list = model.getOwnPropertyList();
		return list.get(0);
	}
}
