package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelInfo;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelProperty;
import jp.hishidama.eclipse_plugin.dmdl_editor.viewer.DMDLTreeData;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class DataModelJoinKey extends DataModelRow {
	private Table table;
	private Map<String, String[]> comboMap;
	private Map<String, Map<String, Integer>> indexMap;

	public DataModelJoinKey(Table table, Map<String, String[]> comboMap, Map<String, Map<String, Integer>> indexMap) {
		this.table = table;
		this.comboMap = comboMap;
		this.indexMap = indexMap;
	}

	private Map<String, String> keys = new HashMap<String, String>();

	public void set(String property, String value) {
		keys.put(property, value);
	}

	public String get(String property) {
		return keys.get(property);
	}

	@Override
	public String getText(int columnIndex) {
		TableColumn c = table.getColumn(columnIndex);
		String name = c.getText();
		return keys.get(name);
	}

	@Override
	public Object getValue(String property) {
		Map<String, Integer> map = indexMap.get(property);
		String key = keys.get(property);
		Integer n = map.get(key);
		if (n == null) {
			return -1;
		}
		return n;
	}

	@Override
	public boolean modify(String property, Object value) {
		String[] combo = comboMap.get(property);
		int n = (Integer) value;
		if (0 <= n && n < combo.length) {
			keys.put(property, combo[n]);
		}
		return true;
	}

	@Override
	public String validate() {
		for (TableColumn c : table.getColumns()) {
			String name = c.getText();
			String key = keys.get(name);
			if (StringUtil.isEmpty(key)) {
				return "キーは必須です。";
			}
		}
		return null;
	}
}

public class CreateDataModelJoinKeyPage extends CreateDataModelPage<DataModelJoinKey> {

	private List<DMDLTreeData> input;
	private Map<String, String[]> comboMap = new HashMap<String, String[]>();
	private Map<String, Map<String, Integer>> indexMap = new HashMap<String, Map<String, Integer>>();

	public CreateDataModelJoinKeyPage() {
		super("CreateDataModelJoinKeyPage", "結合キーの指定",
				"結合データモデルのキーを定義して下さい。\n（結合元データモデルのそれぞれのプロパティーを同時に選択してcopyボタンを押すと定義が作れます）");
	}

	@Override
	protected String getSourceLabelText() {
		return "結合元データモデル";
	}

	@Override
	protected String getTargetLabelText() {
		return "結合キー定義";
	}

	@Override
	protected void defineColumns(Table table) {
		if (input == null) {
			addColumn("key1", 256, "0", new TextCellEditor(table));
			addColumn("key2", 256, "1", new TextCellEditor(table));
		} else {
			for (DMDLTreeData data : input) {
				DataModelInfo info = (DataModelInfo) data.getData();
				String modelName = info.getModelName();

				List<DMDLTreeData> children = data.getChildren();
				if (children == null) {
					children = Collections.emptyList();
				}
				String[] combo = new String[children.size()];
				int i = 0;
				for (DMDLTreeData c : children) {
					DataModelProperty prop = (DataModelProperty) c.getData();
					combo[i++] = prop.getName();
				}

				addColumn(modelName, 128, modelName, new ComboBoxCellEditor(table, combo));

				comboMap.put(modelName, combo);
				Map<String, Integer> index = getComboIndexMap(null, combo);
				indexMap.put(modelName, index);
			}
		}
	}

	public void setSourceList(List<DMDLTreeData> input) {
		this.input = input;
		if (sourceViewer != null) {
			setInput();
		}
	}

	@Override
	protected void setInput() {
		Table table = tableViewer.getTable();
		for (TableColumn c : table.getColumns()) {
			c.dispose();
		}
		editors.clear();
		cprops.clear();
		defineColumns(table);
		tableViewer.setCellEditors(editors.toArray(new CellEditor[editors.size()]));
		tableViewer.setColumnProperties(cprops.toArray(new String[cprops.size()]));

		sourceViewer.setInputList(input);
		sourceViewer.expandAll();

		tableViewer.refresh();
		validate(false);
	}

	@Override
	protected DataModelJoinKey newAddRow() {
		return new DataModelJoinKey(tableViewer.getTable(), comboMap, indexMap);
	}

	@Override
	protected String getCopyToolTipText() {
		return "データモデルのプロパティーを結合キーにします。\n" + "例えばp1とp2をコピーすると、\n" + "joined model = src -> { ～ } % p1, p2\n" + "となります。";
	}

	@Override
	protected boolean enableCopy(DataModelInfo info, DataModelProperty prop) {
		return prop != null;
	}

	protected void doCopy(int index, Iterator<DMDLTreeData> iterator) {
		DataModelJoinKey row = null;

		for (Iterator<DMDLTreeData> i = iterator; i.hasNext();) {
			DMDLTreeData data = i.next();
			Object obj = data.getData();
			if (obj instanceof DataModelProperty) {
				DataModelInfo info = (DataModelInfo) data.getParent().getData();
				DataModelProperty p = (DataModelProperty) obj;
				String modelName = info.getModelName();
				if (row == null) {
					row = newCopyRow(null, null);
				}
				row.set(modelName, p.getName());
			}
		}

		if (row != null) {
			addToList(index, row);
		}
	}

	@Override
	protected DataModelJoinKey newCopyRow(DataModelInfo info, DataModelProperty prop) {
		return new DataModelJoinKey(tableViewer.getTable(), comboMap, indexMap);
	}

	@Override
	protected boolean visibleReference() {
		return false;
	}

	@Override
	protected String getDefineEmptyMessage() {
		return null;
	}

	@Override
	protected String getModelType() {
		return "joined";
	}

	@Override
	protected void setGenerator(DataModelTextGenerator gen, TableItem[] items) {
		for (DMDLTreeData data : input) {
			DataModelInfo info = (DataModelInfo) data.getData();
			String modelName = info.getModelName();
			if ((Boolean) data.getOtherData()) {
				List<DMDLTreeData> children = data.getChildren();
				for (DMDLTreeData c : children) {
					DataModelJoinRow row = (DataModelJoinRow) c.getOtherData();
					gen.appendRefProperty(row.name, row.description, row.refModelName, row.refProperty);
				}
			} else {
				gen.appendRefProperty(modelName);
			}
			for (TableItem item : items) {
				DataModelJoinKey row = (DataModelJoinKey) item.getData();
				String value = row.get(modelName);
				gen.appendKey(value, null);
			}
		}
	}

	@Override
	protected void setGeneratorProperty(DataModelTextGenerator gen, DataModelJoinKey row) {
		throw new UnsupportedOperationException();
	}
}
