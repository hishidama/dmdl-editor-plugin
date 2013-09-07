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
		test2(assist, "summarized s = a=>{", "any", "count", "sum", "min", "max");
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
		test2(assist, "joined s = a->{", "abc", "aaa", "def");
	}

	private static void test1(BlockAssist assist, String text, String... expected) {
		DMDLDocument document = createDocument(text);
		int offset = text.length();
		BlockToken token = getBlock(document);
		List<ICompletionProposal> list = assist.getBlockAssist(document, offset, token);
		assertEqualsList(list, expected);
	}

	private static void test2(BlockAssist assist, String text, String... expected) {
		test1(assist, text, expected);
		test1(assist, text + "\n", expected);
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
