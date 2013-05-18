package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.form;

import java.util.List;

import static jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken.*;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DescriptionToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class ModelTableEditor extends TableEditor {
	private static final int COL_DESC = 0;
	private static final int COL_NAME = 1;
	private static final int COL_TYPE = 2;

	private DataModelPage page;
	private Table table;

	public ModelTableEditor(DataModelPage page, Table table) {
		super(table);
		this.page = page;
		this.table = table;

		horizontalAlignment = SWT.LEFT;
		grabHorizontal = true;
	}

	public boolean isEditable(int column) {
		switch (column) {
		case COL_DESC:
		case COL_NAME:
			return true;
		case COL_TYPE:
			String type = page.getModelType();
			if (type.equals(SUMMARIZED) || type.equals(JOINED)) {
				return false;
			}
			return true;
		default:
			return false;
		}
	}

	public void setEditor(int row, int column, int direction) {
		int columnCount = table.getColumnCount();
		while (!isEditable(column)) {
			column += direction;
			if (column < 0) {
				row--;
				column = columnCount - 1;
			}
			if (column >= columnCount) {
				row++;
				column = 0;
			}
		}
		if (row < 0) {
			row = table.getItemCount() - 1;
		} else if (row >= table.getItemCount()) {
			row = 0;
		}
		table.setSelection(row);
		// table.deselectAll();
		switch (column) {
		case COL_DESC:
			DescriptionEditor desc = new DescriptionEditor();
			desc.start(row, column);
			break;
		case COL_NAME:
			NameEditor name = new NameEditor();
			name.start(row, column);
			break;
		case COL_TYPE:
			TypeEditor type = new TypeEditor();
			type.start(row, column);
			break;
		default:
			throw new UnsupportedOperationException("column=" + column);
		}
	}

	protected abstract class Editor {
		protected int row;
		protected int column;
		protected TableItem item;
		protected String oldValue;

		public void start(final int row, final int column) {
			this.row = row;
			this.column = column;
			item = table.getItem(row);
			oldValue = item.getText(column);

			Control editor = createControl(oldValue);
			editor.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					commit();
				}
			});
			editor.addTraverseListener(new TraverseListener() {
				@Override
				public void keyTraversed(TraverseEvent e) {
					switch (e.detail) {
					case SWT.TRAVERSE_RETURN:
						e.doit = commit();
						break;
					case SWT.TRAVERSE_TAB_NEXT:
						e.doit = commit();
						if (e.doit) {
							setEditor(row, column + 1, +1);
						}
						break;
					case SWT.TRAVERSE_TAB_PREVIOUS:
						e.doit = commit();
						if (e.doit) {
							setEditor(row, column - 1, -1);
						}
						break;
					case SWT.TRAVERSE_ESCAPE:
						disposeControl();
						e.doit = false;
						break;
					}
				}
			});

			ModelTableEditor.super.setEditor(editor, item, column);

			editor.setFocus();
		}

		protected abstract Control createControl(String value);

		protected abstract void disposeControl();

		protected boolean commit() {
			String value = getText();
			if (!prepareCommit(value)) {
				return false;
			}

			item.setText(column, value);
			disposeControl();

			if (!value.equals(oldValue)) {
				ModelToken model = page.getModel();
				List<PropertyToken> list = model.getPropertyList();
				PropertyToken prop = list.get(row);
				replace(model, prop, value);
			}
			return true;
		}

		protected abstract String getText();

		protected abstract boolean prepareCommit(String value);

		protected abstract void replace(ModelToken model, PropertyToken prop, String value);
	}

	protected abstract class TextEditor extends Editor {
		protected Text text;

		@Override
		protected Control createControl(String value) {
			text = new Text(table, SWT.NONE);
			text.setText(value);
			text.selectAll();
			return text;
		}

		@Override
		protected void disposeControl() {
			text.dispose();
		}

		@Override
		protected String getText() {
			return text.getText();
		}
	}

	class DescriptionEditor extends TextEditor {
		@Override
		protected boolean prepareCommit(String value) {
			return true;
		}

		@Override
		protected void replace(ModelToken model, PropertyToken prop, String value) {
			if (!value.isEmpty()) {
				value = DataModelPage.encodeDescription(value);
			}

			DescriptionToken desc = prop.getPropertyDescriptionToken();
			if (desc != null) {
				page.replaceDocument(desc.getStart(), desc.getLength(), value);
				return;
			}
			page.replaceDocument(prop.getStart(), 0, "\n" + value);
		}
	}

	class NameEditor extends TextEditor {
		@Override
		protected boolean prepareCommit(String value) {
			if (value.isEmpty()) {
				return false;
			}
			return true;
		}

		@Override
		protected void replace(ModelToken model, PropertyToken prop, String value) {
			WordToken name = prop.getNameToken();
			if (name != null) {
				page.replaceDocument(name.getStart(), name.getLength(), value);
				return;
			}
			// TODO summarizeとかjoinとか
			DescriptionToken desc = prop.getPropertyDescriptionToken();
			if (desc != null) {
				page.replaceDocument(desc.getEnd(), 0, " " + value + " ");
				return;
			}
			page.replaceDocument(prop.getStart(), 0, "\n" + value);
		}
	}

	protected class TypeEditor extends Editor {
		protected Combo list;

		@Override
		protected Control createControl(String value) {
			list = new Combo(table, SWT.NONE);
			for (String type : WordToken.PROPERTY_TYPE) {
				list.add(type);
			}
			list.setText(value);
			return list;
		}

		@Override
		protected void disposeControl() {
			list.dispose();
		}

		@Override
		protected String getText() {
			return list.getText();
		}

		@Override
		protected boolean prepareCommit(String value) {
			if (value.isEmpty()) {
				return false;
			}
			return true;
		}

		@Override
		protected void replace(ModelToken model, PropertyToken prop, String value) {
			WordToken type = prop.getDataTypeToken();
			if (type != null) {
				page.replaceDocument(type.getStart(), type.getLength(), value);
				return;
			}
			WordToken colon = prop.findWord(":");
			if (colon != null) {
				page.replaceDocument(colon.getEnd(), 0, " " + value);
				return;
			}
		}
	}
}
