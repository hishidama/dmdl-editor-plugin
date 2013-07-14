package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.update;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.DMDLSimpleParser;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.DMDLSimpleScanner;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.StringScanner;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.AnnotationToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ArgumentsToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.CommentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLBodyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;

public class AttributeAppender4Replace extends AttributeUpdater {

	private String modelAttr;
	private String propAttr;
	private Set<String> modelAttrSet;
	private Set<String> propAttrSet;

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

		modelAttrSet = parseAttribute(modelAttr);
		propAttrSet = parseAttribute(propAttr);
	}

	private Set<String> parseAttribute(String text) {
		DMDLSimpleScanner scanner = new StringScanner(text);
		DMDLSimpleParser parser = new DMDLSimpleParser();
		ModelList models = parser.parse(scanner);

		Set<String> set = new HashSet<String>();
		for (DMDLToken token : models.getBody()) {
			ModelToken model = (ModelToken) token;
			for (DMDLToken t : model.getBody()) {
				if (t instanceof AnnotationToken) {
					String name = ((AnnotationToken) t).getText();
					set.add(name);
				}
			}
		}
		return set;
	}

	@Override
	protected void execute(IFile file, IDocument doc, ModelToken model) {
		String mname = model.getModelName();
		{
			DMDLToken token = getPosition(model, modelAttrSet);
			if (token != null) {
				if (token instanceof WordToken) {
					int offset = getLineTop(doc, token.getStart());
					addAppendRegion(file, offset, modelAttr, mname, "", "");
				} else if (token instanceof AnnotationToken) {
					addReplaceRegion(file, (AnnotationToken) token, modelAttr, mname, "", "");
				}
			}
		}

		for (PropertyToken prop : model.getOwnPropertyList()) {
			DMDLToken token = getPosition(prop, propAttrSet);
			if (token != null) {
				if (token instanceof WordToken) {
					int offset = getLineTop(doc, token.getStart());
					addAppendRegion(file, offset, propAttr, mname, prop.getPropertyName(),
							prop.getPropertyDescription());
				} else if (token instanceof AnnotationToken) {
					addReplaceRegion(file, (AnnotationToken) token, propAttr, mname, prop.getPropertyName(),
							prop.getPropertyDescription());
				}
			}
		}
	}

	private DMDLToken getPosition(DMDLBodyToken token, Set<String> set) {
		for (DMDLToken t : token.getBody()) {
			if (t instanceof AnnotationToken) {
				String name = ((AnnotationToken) t).getText();
				if (set.contains(name)) {
					return t;
				}
			}
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

	private void addReplaceRegion(IFile file, AnnotationToken token, String attr, String modelName, String propName,
			String propDesc) {
		if (attr.isEmpty()) {
			return;
		}
		String text = StringUtil.replace(attr, modelName, propName, propDesc);
		text = text.trim();

		int start = -1;
		int end = -1;
		DMDLBodyToken parentToken = (DMDLBodyToken) token.getParent();
		List<DMDLToken> list = parentToken.getBody();
		for (int i = 0; i < list.size(); i++) {
			DMDLToken t = list.get(i);
			if (t == token) {
				start = t.getStart();
				end = t.getEnd();
				for (i++; i < list.size(); i++) {
					t = list.get(i);
					if (t instanceof CommentToken) {
						continue;
					} else if (t instanceof ArgumentsToken) {
						end = t.getEnd();
					}
					break;
				}
				break;
			}
		}

		addReplaceRegion(file, start, end, text);
	}
}
