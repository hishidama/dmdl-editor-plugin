package jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.hyperlink;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLTextEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class DMDLHyperlink implements IHyperlink {
	private DMDLTextEditor editor;
	private DMDLToken token;

	private IProject project;
	private IRegion region;

	public DMDLHyperlink(DMDLTextEditor editor, DMDLToken token) {
		this.editor = editor;
		this.token = token;
	}

	public DMDLHyperlink(IProject project, DMDLToken token, IRegion region) {
		this.project = project;
		this.token = token;
		this.region = region;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		if (region != null) {
			return region;
		}
		int offset = token.getStart();
		int length = token.getLength();
		return new Region(offset, length);
	}

	@Override
	public String getTypeLabel() {
		return "Open DMDL";
	}

	@Override
	public String getHyperlinkText() {
		return "Open DMDL";
	}

	@Override
	public void open() {
		if (project != null) {
			HyperlinkUtil.gotoPosition(project, token);
			return;
		}
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
						HyperlinkUtil.gotoPosition(editor.getProject(),
								editor.getFile(), modelName, propName);
					}
					return;
				} else if (t instanceof ModelToken) {
					if (token instanceof WordToken) {
						String modelName = ((WordToken) token).getBody();
						HyperlinkUtil.gotoPosition(editor.getProject(),
								editor.getFile(), modelName, null);
					}
					return;
				}
			}
		}
	}
}
