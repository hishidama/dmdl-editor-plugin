package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.update.AttributeRemover;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.update.AttributeUpdater;

import org.eclipse.swt.widgets.Composite;

public class SetRemoveAttributePage extends SetAttributePage {

	public SetRemoveAttributePage() {
		super("SetRemoveAttributePage");
		setTitle("削除する属性の指定");
	}

	@Override
	public void createControl(Composite parent) {
		setDescription("削除する属性の名前を指定して下さい。");

		super.createControl(parent);
	}

	@Override
	protected String getDefaultModelAttribute(AttributeType type) {
		switch (type) {
		case DIRECTIO_CSV:
			return "@directio.csv";
		case WINDGATE_CSV:
			return "@windgate.csv";
		case WINDGATE_JDBC:
			return "@windgate.jdbc.table";
		default:
			return "";
		}
	}

	@Override
	protected String getDefaultPropertyAttribute(AttributeType type) {
		switch (type) {
		case DIRECTIO_CSV:
			return "@directio.csv.field\n" //
					+ "@directio.csv.file_name\n" //
					+ "@directio.csv.line_number\n" //
					+ "@directio.csv.record_number";
		case WINDGATE_CSV:
			return "@windgate.csv.field\n" //
					+ "@windgate.csv.file_name\n" //
					+ "@windgate.csv.line_number\n" //
					+ "@windgate.csv.record_number";
		case WINDGATE_JDBC:
			return "@windgate.jdbc.column";
		default:
			return "";
		}
	}

	@Override
	public AttributeUpdater<?> getUpdater() {
		return new AttributeRemover();
	}
}
