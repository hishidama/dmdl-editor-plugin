package jp.hishidama.eclipse_plugin.util.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class CheckedTableUtil {

	/**
	 * チェックを最大1個だけに限定させる。
	 * 
	 * @param table
	 *            Table(SWT.CHECK)
	 */
	public static void setSingleCheckedTable(final Table table) {
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// org.eclipse.jface.viewers.CheckboxTableViewer#handleSelect(SelectionEvent)
				if (event.detail == SWT.CHECK) {
					TableItem item = (TableItem) event.item;
					if (item.getChecked()) {
						resetChecked(table, item);
					}
				}
			}
		});

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				Point point = new Point(e.x, e.y);
				TableItem item = table.getItem(point);
				if (item == null) {
					return;
				}
				int columns = table.getColumnCount();
				for (int i = 0; i < columns; i++) {
					if (item.getBounds(i).contains(point)) {
						boolean checked = !item.getChecked();
						item.setChecked(checked);
						if (checked) {
							resetChecked(table, item);
						}
						break;
					}
				}
			}
		});
	}

	private static void resetChecked(Table table, TableItem checked) {
		for (TableItem item : table.getItems()) {
			if (item != checked) {
				item.setChecked(false);
			}
		}
	}
}
