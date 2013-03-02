package jp.hishidama.eclipse_plugin.dmdl_editor.editors.form;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DescriptionToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Text;

public class ModelDescriptionListener implements FocusListener {
	protected DataModelPage page;
	private String oldValue;

	public ModelDescriptionListener(DataModelPage page) {
		this.page = page;
	}

	public void setOldValue(String value) {
		oldValue = value;
	}

	@Override
	public void focusGained(FocusEvent e) {
		Text text = (Text) e.getSource();
		oldValue = text.getText();
	}

	@Override
	public void focusLost(FocusEvent e) {
		Text text = (Text) e.getSource();
		String value = text.getText();
		if (!value.equals(oldValue)) {
			if (value.isEmpty()) {
				replace("");
			} else {
				replace(DataModelPage.encodeDescription(value));
			}
			oldValue = value;
		}
	}

	private void replace(String value) {
		ModelToken model = page.getModel();
		DescriptionToken desc = model.getDescriptionToken();
		if (desc != null) {
			page.replaceDocument(desc.getStart(), desc.getLength(), value);
			return;
		}
		if (value.isEmpty()) {
			return;
		}
		WordToken type = model.getModelTypeToken();
		if (type != null) {
			page.replaceDocument(type.getStart(), 0, value + "\n");
			return;
		}
		WordToken name = model.getModelNameToken();
		if (name != null) {
			page.replaceDocument(name.getStart(), 0, value + "\n");
			return;
		}
		page.replaceDocument(model.getStart(), 0, value + "\n");
	}
}
