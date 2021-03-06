package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.assist;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.DMDLSimpleParser;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.DocumentScanner;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class AssistTest {

	protected static DMDLDocument createDocument(String s) {
		DMDLDocument document = new DMDLDocument();
		document.set(s);
		DocumentScanner scanner = new DocumentScanner(document);
		DMDLSimpleParser parser = new DMDLSimpleParser();
		ModelList models = parser.parse(scanner);
		document.setModelList(models);

		return document;
	}

	protected static ModelToken getModel(DMDLDocument document) {
		ModelList models = document.getModelList();
		return (ModelToken) models.getBody().get(0);
	}

	protected static void assertEqualsList(List<ICompletionProposal> list, String... expected) {
		try {
			assertEquals(expected.length, list.size());
		} catch (Error e) {
			String[] a = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				a[i] = list.get(i).getDisplayString();
			}
			System.out.println("expected: " + Arrays.toString(expected));
			System.out.println("actual:   " + Arrays.toString(a));
			throw e;
		}
		int i = 0;
		for (String s : expected) {
			ICompletionProposal cp = list.get(i++);
			assertEquals(s, cp.getDisplayString());
		}
	}
}
