package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import java.text.MessageFormat;
import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelInfo;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelProperty;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelUtil;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Table;

class DataModelNormalRow extends DataModelRow {
	public static final String TP_NAME = "name";
	public static final String TP_DESC = "description";
	public static final String TP_DATATYPE = "dataType";
	public static final String TP_REF_MODEL = "refModel";
	public static final String[] DATA_TYPE = WordToken.PROPERTY_TYPE;

	public String name;
	public String description;
	public String dataType;
	public String refModelName;

	@Override
	public String getText(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return name;
		case 1:
			return description;
		case 2:
			return dataType;
		case 3:
			return refModelName;
		default:
			throw new UnsupportedOperationException(MessageFormat.format("index={0}", columnIndex));
		}
	}

	private static Map<String, Integer> dataTypeComboMap;

	@Override
	public Object getValue(String property) {
		if (property.equals(TP_NAME)) {
			return nonNull(name);
		}
		if (property.equals(TP_DESC)) {
			return nonNull(description);
		}
		if (property.equals(TP_DATATYPE)) {
			dataTypeComboMap = CreateDataModelPage.getComboIndexMap(dataTypeComboMap, DATA_TYPE);
			Integer n = dataTypeComboMap.get(dataType);
			if (n == null) {
				return -1;
			}
			return n;
		}
		if (property.equals(TP_REF_MODEL)) {
			return nonNull(refModelName);
		}
		throw new UnsupportedOperationException(MessageFormat.format("property={0}", property));
	}

	@Override
	public boolean modify(String property, Object value) {
		if (property.equals(TP_NAME)) {
			String text = ((String) value).trim();
			this.name = text;
			return true;
		}
		if (property.equals(TP_DESC)) {
			String text = ((String) value).trim();
			this.description = text;
			return false;
		}
		if (property.equals(TP_DATATYPE)) {
			int n = (Integer) value;
			if (0 <= n && n < DATA_TYPE.length) {
				this.dataType = DATA_TYPE[n];
			}
			return true;
		}
		if (property.equals(TP_REF_MODEL)) {
			String text = ((String) value).trim();
			this.refModelName = text;
			return true;
		}
		throw new UnsupportedOperationException(MessageFormat.format("property={0}", property));
	}

	@Override
	public String validate() {
		if (StringUtil.nonEmpty(name)) {
			String message = DataModelUtil.validateName("プロパティー名", name);
			if (message != null) {
				return message;
			}
		}
		if (StringUtil.isEmpty(refModelName)) {
			if (StringUtil.isEmpty(name)) {
				return "プロパティー名は必須です。";
			}
			if (StringUtil.isEmpty(dataType)) {
				return "プロパティーの型は必須です。";
			}
		}
		return null;
	}
}

public class CreateDataModelNormalPage extends CreateDataModelPage<DataModelNormalRow> {

	public CreateDataModelNormalPage() {
		this("CreateDataModelNormalPage", "データモデルの作成", "データモデルを定義して下さい。");
	}

	protected CreateDataModelNormalPage(String pageName, String pageTitle, String pageDescription) {
		super(pageName, pageTitle, pageDescription);
	}

	@Override
	protected void defineColumns(Table table) {
		addColumn("name", 128, DataModelNormalRow.TP_NAME, new TextCellEditor(table));
		addColumn("description", 128, DataModelNormalRow.TP_DESC, new TextCellEditor(table));
		addColumn("type", 96, DataModelNormalRow.TP_DATATYPE, new ComboBoxCellEditor(table,
				DataModelNormalRow.DATA_TYPE));
		addColumn("ref model", 128, DataModelNormalRow.TP_REF_MODEL, new TextCellEditor(table));
	}

	@Override
	protected DataModelNormalRow newAddRow() {
		return new DataModelNormalRow();
	}

	@Override
	protected String getCopyToolTipText() {
		return "データモデルのプロパティーをコピーします。\n" + "例えばp1とp2をコピーすると、\n" + "model = { p1:TYPE; p2:TYPE; };\n" + "となります。";
	}

	@Override
	protected DataModelNormalRow newCopyRow(DataModelInfo info, DataModelProperty prop) {
		DataModelNormalRow row = new DataModelNormalRow();
		row.name = prop.getName();
		row.description = prop.getDescription();
		row.dataType = prop.getResolvedDataType();
		return row;
	}

	@Override
	protected String getReferenceToolTipText() {
		return "データモデルの参照を作成します。\n" + "例えばm1とm2を参照すると、\n" + "model = m1 + m2;\n" + "となります。";
	}

	@Override
	protected boolean enableReference(DataModelInfo info, DataModelProperty prop) {
		return info != null;
	}

	@Override
	protected DataModelNormalRow newReferenceRow(DataModelInfo info, DataModelProperty prop) {
		DataModelNormalRow row = new DataModelNormalRow();
		row.refModelName = info.getModelName();
		return row;
	}

	@Override
	protected String getDefineEmptyMessage() {
		return null;
	}

	@Override
	protected String getModelType() {
		return null;
	}

	@Override
	protected void setGeneratorProperty(DataModelTextGenerator gen, DataModelNormalRow row) {
		if (StringUtil.nonEmpty(row.dataType)) {
			gen.appendProperty(row.name, row.description, row.dataType);
		} else if (StringUtil.nonEmpty(row.refModelName)) {
			gen.appendRefProperty(row.refModelName);
		} else {
			gen.appendProperty(row.name, row.description, row.dataType);
		}
	}
}
