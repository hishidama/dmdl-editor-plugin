package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.update;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLBodyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;

public class AttributeAppender4Add extends AttributeUpdater {

	private String modelAttr;
	private String propAttr;

	@Override
	public void setAttribute(String modelAttr, String propAttr) {
		if (!modelAttr.isEmpty()) {
			if (!modelAttr.endsWith("\n")) {
				modelAttr += "\n";
			}
		}
		if (!propAttr.isEmpty()) {
			if (!propAttr.contains("\n") && !propAttr.endsWith("\n")) {
				propAttr = String.format("    %s\n", propAttr);
			} else {
				if (!propAttr.endsWith(" ") && !propAttr.endsWith(")")) {
					propAttr += " ";
				}
			}
		}
		this.modelAttr = modelAttr;
		this.propAttr = propAttr;
	}

	@Override
	protected void execute(IFile file, IDocument doc, ModelToken model) {
		String mname = model.getModelName();
		{
			WordToken token = getFirstWordToken(model);
			if (token != null) {
				int offset = getLineTop(doc, token.getStart());
				addAppendRegion(file, offset, modelAttr, mname, "", "");
			}
		}

		for (PropertyToken prop : model.getOwnPropertyList()) {
			WordToken token = getFirstWordToken(prop);
			if (token != null) {
				int offset = getLineTop(doc, token.getStart());
				addAppendRegion(file, offset, propAttr, mname, prop.getPropertyName(), prop.getPropertyDescription());
			}
		}
	}

	private WordToken getFirstWordToken(DMDLBodyToken token) {
		for (DMDLToken t : token.getBody()) {
			if (t instanceof WordToken) {
				return (WordToken) t;
			}
		}
		return null;
	}

	private void addAppendRegion(IFile file, int offset, String attr, String modelName, String propName, String propDesc) {
		if (attr.isEmpty()) {
			return;
		}

		String text = StringUtil.replace(attr, modelName, propName, propDesc);
		addAppendRegion(file, offset, text);
	}
}
