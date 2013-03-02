package jp.hishidama.eclipse_plugin.dmdl_editor.editors.form;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DescriptionToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Text;

public class ModelNameListener implements FocusListener {
	protected DataModelPage page;
	private String oldValue;

	public ModelNameListener(DataModelPage page) {
		this.page = page;
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
		if (value.isEmpty()) {
			text.setText(oldValue); // 元に戻す
		} else {
			if (!value.equals(oldValue)) {
				replace(value);
				oldValue = value;
			}
		}
	}

	private void replace(String value) {
		ModelToken model = page.getModel();
		WordToken name = model.getModelNameToken();
		if (name != null) {
			page.replaceDocument(name.getStart(), name.getLength(), value);
			return;
		}
		DescriptionToken desc = model.getDescriptionToken();
		if (desc != null) {
			page.replaceDocument(desc.getEnd(), 0, "\n" + value + "\n");
			return;
		}
		page.replaceDocument(model.getStart(), 0, value + "\n");
	}
}
