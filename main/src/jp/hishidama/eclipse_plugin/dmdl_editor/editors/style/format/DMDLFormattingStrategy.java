package jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.format;

import org.eclipse.jface.text.formatter.IFormattingStrategy;

public class DMDLFormattingStrategy implements IFormattingStrategy {

	@Override
	public void formatterStarts(String initialIndentation) {
	}

	@Override
	public String format(String content, boolean isLineStart,
			String indentation, int[] positions) {
		System.out.println("TODO+++" + indentation);
		return content;
	}

	@Override
	public void formatterStops() {
	}
}
