package jp.hishidama.eclipse_plugin.dmdl_editor.internal.jdt.hyperlink;

import jp.hishidama.eclipse_plugin.dmdl_editor.util.DMDLHyperlinkUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelPosition;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class DeclaredDmdlHyperlink implements IHyperlink {
	private DataModelPosition token;
	private IRegion region;

	public DeclaredDmdlHyperlink(DataModelPosition token, IRegion region) {
		this.token = token;
		this.region = region;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return region;
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
		DMDLHyperlinkUtil.gotoPosition(token);
	}
}
