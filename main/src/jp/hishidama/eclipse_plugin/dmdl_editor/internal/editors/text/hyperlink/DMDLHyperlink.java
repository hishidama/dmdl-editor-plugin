package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.hyperlink;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLTextEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index.PositionUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index.PositionUtil.NamePair;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DMDLHyperlinkUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class DMDLHyperlink implements IHyperlink {
	private DMDLTextEditor editor;
	private DMDLToken token;

	public DMDLHyperlink(DMDLTextEditor editor, DMDLToken token) {
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
		return "Open DMDL";
	}

	@Override
	public String getHyperlinkText() {
		return "Open DMDL";
	}

	@Override
	public void open() {
		WordToken target = token.getReferenceWord();
		if (target != null) {
			int offset = target.getStart();
			int length = target.getLength();
			editor.selectAndReveal(offset, length);
		} else {
			IProject project = editor.getProject();
			NamePair name = PositionUtil.getName(project, token);
			if (name != null) {
				DMDLHyperlinkUtil.gotoPosition(project, name.getModelName(), name.getPropertyName());
			}
		}
	}
}
