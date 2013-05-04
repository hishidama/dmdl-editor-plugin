package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.update.AttributeAppender;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.update.AttributeUpdater;

import org.eclipse.swt.widgets.Composite;

public class SetAddAttributePage extends SetAttributePage {

	public SetAddAttributePage() {
		super("SetAttributePage");
		setTitle("追加する属性の指定");
	}

	@Override
	public void createControl(Composite parent) {
		setDescription("追加する属性の内容を指定して下さい。");

		super.createControl(parent);
	}

	@Override
	protected String getDefaultModelAttribute(AttributeType type) {
		switch (type) {
		case DIRECTIO_CSV:
			return "@directio.csv(\n" //
					+ "  charset = \"UTF-8\",\n" //
					+ "  allow_linefeed = FALSE,\n" //
					+ "  has_header = FALSE,\n" //
					+ "  true = \"true\",\n" //
					+ "  false = \"false\",\n" //
					+ "  date = \"yyyy-MM-dd\",\n" //
					+ "  datetime = \"yyyy-MM-dd HH:mm:ss\",\n" //
					+ ")";
		case WINDGATE_CSV:
			return "@windgate.csv(\n" //
					+ "  charset = \"UTF-8\",\n" //
					+ "  has_header = FALSE,\n" //
					+ "  true = \"true\",\n" //
					+ "  false = \"false\",\n" //
					+ "  date = \"yyyy-MM-dd\",\n" //
					+ "  datetime = \"yyyy-MM-dd HH:mm:ss\",\n" //
					+ ")";
		case WINDGATE_JDBC:
			return "@windgate.jdbc.table(name =\"${modelName.toUpper}\")";
		default:
			return "";
		}
	}

	@Override
	protected String getDefaultPropertyAttribute(AttributeType type) {
		switch (type) {
		case DIRECTIO_CSV:
			return "@directio.csv.field(name = \"${name}\")";
		case WINDGATE_CSV:
			return "@windgate.csv.field(name = \"${name}\")";
		case WINDGATE_JDBC:
			return "@windgate.jdbc.column";
		default:
			return "";
		}
	}

	@Override
	public AttributeUpdater<?> getUpdater() {
		return new AttributeAppender();
	}
}
