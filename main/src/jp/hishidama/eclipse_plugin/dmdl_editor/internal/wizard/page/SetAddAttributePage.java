package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.update.AttributeAppender;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.update.AttributeUpdater;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class SetAddAttributePage extends SetAttributePage {

	public SetAddAttributePage() {
		super("SetAttributePage");
		setTitle("追加する属性の指定");
		setDescription("追加する属性の内容を指定して下さい。");
	}

	@Override
	protected void createNoteArea(Group group) {
		group.setLayout(new GridLayout(2, false));

		Label label = new Label(group, SWT.NONE);
		label.setText("追加する属性には右記の変数を指定することが出来ます。\n行を選択してCtrl+Cを押すと変数名をコピーできます。");

		Table table = new Table(group, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setHeaderVisible(false);
		table.setLinesVisible(true);
		GridData grid = new GridData(GridData.GRAB_HORIZONTAL);
		grid.heightHint = 18 * 3;
		table.setLayoutData(grid);
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setWidth(128 + 64);
		}
		{
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setWidth(128 + 64);
		}
		createItem(table, "モデル名", "$(modelName)");
		createItem(table, "モデル名（大文字）", "$(modelName.toUpper)");
		createItem(table, "プロパティー名", "$(name)");
		createItem(table, "プロパティー名（大文字）", "$(name.toUpper)");
		createItem(table, "プロパティー説明", "$(description)");
		table.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == 0x03) { // Ctrl+C
					Table table = (Table) e.getSource();
					TableItem[] items = table.getSelection();
					if (items.length > 0) {
						TableItem item = items[0];
						Clipboard clipboard = new Clipboard(e.display);
						clipboard.setContents(new Object[] { item.getData() },
								new Transfer[] { TextTransfer.getInstance() });
					}
				}
			}
		});
	}

	private void createItem(Table table, String desc, String value) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(0, desc);
		item.setText(1, value);
		item.setData(value);
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
			return "@windgate.jdbc.table(name = \"$(modelName.toUpper)\")";
		default:
			return "";
		}
	}

	@Override
	protected String getDefaultPropertyAttribute(AttributeType type) {
		switch (type) {
		case DIRECTIO_CSV:
			return "@directio.csv.field(name = \"$(name)\")";
		case WINDGATE_CSV:
			return "@windgate.csv.field(name = \"$(name)\")";
		case WINDGATE_JDBC:
			return "@windgate.jdbc.column(name = \"$(name.toUpper)\")";
		default:
			return "";
		}
	}

	@Override
	public AttributeUpdater<?> getUpdater() {
		return new AttributeAppender();
	}
}
