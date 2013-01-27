package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken.WordType;

public class BlockToken extends DMDLBodyToken {

	public BlockToken(int start, int end, List<DMDLToken> bodyList) {
		super(start, end, bodyList);
	}

	@Override
	public String toString() {
		return toString("BlockToken", "    ");
	}

	public void parseSummarized() {
		for (DMDLToken token : bodyList) {
			if (token instanceof PropertyToken) {
				PropertyToken prop = (PropertyToken) token;
				for (DMDLToken t : prop.getBody()) {
					if (t instanceof WordToken) {
						WordToken word = (WordToken) t;
						if (word.getWordType() == WordType.UNKNOWN) {
							word.setWordType(WordType.SUMMARIZED_TYPE);
						}
						break;
					}
				}
			}
		}
	}
}
