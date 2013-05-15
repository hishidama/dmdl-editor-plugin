package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.assist;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.BlockToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.junit.Test;

public class BlockAssistTest extends AssistTest {

	@Test
	public void summarized() {
		BlockAssist assist = new BlockAssist();
		DMDLDocument document = createDocument("summarized s = a=>{");
		int offset = 19;
		BlockToken token = getBlock(document);
		List<ICompletionProposal> list = assist.getBlockAssist(document, offset, token);
		assertEqualsList(list, "any", "count", "sum", "min", "max");
	}

	@Test
	public void summarized_() {
		BlockAssist assist = new BlockAssist();
		DMDLDocument document = createDocument("summarized s = a=>{\n");
		int offset = 20;
		BlockToken token = getBlock(document);
		List<ICompletionProposal> list = assist.getBlockAssist(document, offset, token);
		assertEqualsList(list, "any", "count", "sum", "min", "max");
	}

	@Test
	public void joined() {
		BlockAssist assist = new BlockAssist() {
			@Override
			protected ModelToken findModel(DMDLToken token, String modelName) {
				ModelToken model = new ModelToken(0, 0, new ArrayList<DMDLToken>());
				return model;
			}

			@Override
			protected String[] getRefProperties(WordToken refModelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		DMDLDocument document = createDocument("joined s = a->{");
		int offset = 15;
		BlockToken token = getBlock(document);
		List<ICompletionProposal> list = assist.getBlockAssist(document, offset, token);
		assertEqualsList(list, "abc", "aaa", "def");
	}

	@Test
	public void joined_() {
		BlockAssist assist = new BlockAssist() {
			@Override
			protected ModelToken findModel(DMDLToken token, String modelName) {
				ModelToken model = new ModelToken(0, 0, new ArrayList<DMDLToken>());
				return model;
			}

			@Override
			protected String[] getRefProperties(WordToken refModelName) {
				return new String[] { "abc", "aaa", "def" };
			}
		};
		DMDLDocument document = createDocument("joined s = a->{\n");
		int offset = 16;
		BlockToken token = getBlock(document);
		List<ICompletionProposal> list = assist.getBlockAssist(document, offset, token);
		assertEqualsList(list, "abc", "aaa", "def");
	}

	protected static BlockToken getBlock(DMDLDocument document) {
		ModelToken model = getModel(document);
		for (DMDLToken token : model.getBody()) {
			if (token instanceof BlockToken) {
				return (BlockToken) token;
			}
		}
		throw new IllegalStateException(model.toString());
	}
}
