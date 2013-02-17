package jp.hishidama.eclipse_plugin.dmdl_editor.editors.hyperlink;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class DMDLHyperlink implements IHyperlink {
	private DMDLEditor editor;
	private DMDLToken token;

	public DMDLHyperlink(DMDLEditor editor, DMDLToken token) {
		this.editor = editor;
		this.token = token;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		int offset = token.getStart();
		int length = token.getLength();
		return new Region(offset, length);
	}

	@Override
	public String getTypeLabel() {
		return null;
	}

	@Override
	public String getHyperlinkText() {
		return null;
	}

	@Override
	public void open() {
		WordToken target = token.getReferenceWord();
		if (target != null) {
			int offset = target.getStart();
			int length = target.getLength();
			editor.selectAndReveal(offset, length);
		} else {
			for (DMDLToken t = token; t != null; t = t.getParent()) {
				if (t instanceof PropertyToken) {
					PropertyToken prop = (PropertyToken) t;
					DMDLToken ref = prop.findRefModelToken();
					if (ref instanceof WordToken) {
						String modelName = ((WordToken) ref).getBody();
						String propName = prop.getRefNameToken().getBody();
						editor.gotoPosition(modelName, propName);
					}
					return;
				} else if (t instanceof ModelToken) {
					if (token instanceof WordToken) {
						String modelName = ((WordToken) token).getBody();
						editor.gotoPosition(modelName, null);
					}
					return;
				}
			}
		}
	}
}
