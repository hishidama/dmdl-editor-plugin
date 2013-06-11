package jp.hishidama.eclipse_plugin.dmdl_editor.viewer;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelFile;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelInfo;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelProperty;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelUtil;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.core.resources.IProject;

public abstract class DMDLTreeData {
	private DMDLTreeData parent;
	protected final IProject project;
	private Object otherData = null;
	private boolean filterSelected = true;

	public DMDLTreeData(IProject project, DMDLTreeData parent) {
		this.project = project;
		this.parent = parent;
	}

	public final DMDLTreeData getParent() {
		return parent;
	}

	protected final void setParent(DMDLTreeData parent) {
		this.parent = parent;
	}

	public abstract Object getData();

	public void setOtherData(Object object) {
		this.otherData = object;
	}

	public Object getOtherData() {
		return otherData;
	}

	public boolean setFilter(String filter) {
		filterSelected = isSelect(filter);
		return filterSelected;
	}

	public boolean isFilterSelected() {
		return filterSelected;
	}

	private boolean isSelect(String filter) {
		boolean r = isSelect(getText(), filter) || isSelect(getText2(), filter);
		r |= childSelect(filter);
		return r;
	}

	private boolean childSelect(String filter) {
		List<DMDLTreeData> cs = getChildren();
		if (cs == null) {
			return false;
		}
		boolean r = false;
		for (DMDLTreeData c : cs) {
			r |= c.setFilter(filter);
		}
		return r;
	}

	protected abstract String getText();

	protected abstract String getText2();

	private static boolean isSelect(String s, String filter) {
		if (s == null) {
			return false;
		}
		if (StringUtil.isEmpty(filter)) {
			return true;
		}
		return s.contains(filter);
	}

	public abstract boolean hasChildren();

	public abstract List<DMDLTreeData> getChildren();

	public static class File extends DMDLTreeData {
		private DataModelFile file;
		private List<DMDLTreeData> children;

		public File(DataModelFile file) {
			super(file.getProject(), null);
			this.file = file;
		}

		@Override
		public Object getData() {
			return file;
		}

		@Override
		protected String getText() {
			return file.getFilePath();
		}

		@Override
		protected String getText2() {
			return null;
		}

		@Override
		public boolean hasChildren() {
			return true;
		}

		@Override
		public List<DMDLTreeData> getChildren() {
			if (children == null) {
				List<DataModelInfo> list = DataModelUtil.getModels(file.getFile());
				children = new ArrayList<DMDLTreeData>(list.size());
				for (DataModelInfo info : list) {
					children.add(new Model(project, this, info));
				}
			}
			return children;
		}
	}

	public static class Model extends DMDLTreeData {
		private final DataModelInfo info;
		private List<DMDLTreeData> children;

		public Model(IProject project, DMDLTreeData parent, DataModelInfo info) {
			super(project, parent);
			this.info = info;
		}

		@Override
		public Object getData() {
			return info;
		}

		@Override
		protected String getText() {
			return info.getModelName();
		}

		@Override
		protected String getText2() {
			return DataModelUtil.decodeDescription(info.getModelDescription());
		}

		@Override
		public boolean hasChildren() {
			return true;
		}

		@Override
		public List<DMDLTreeData> getChildren() {
			if (children == null) {
				String modelName = info.getModelName();
				List<DataModelProperty> list = DataModelUtil.getModelProperties(project, modelName);
				if (list == null) {
					return null;
				}
				children = new ArrayList<DMDLTreeData>(list.size());
				for (DataModelProperty prop : list) {
					children.add(new Property(project, this, prop));
				}
			}
			return children;
		}

		public void setChildren(List<DMDLTreeData> list) {
			if (list != null) {
				for (DMDLTreeData c : list) {
					c.setParent(this);
				}
			}
			this.children = list;
		}
	}

	public static class Property extends DMDLTreeData {
		private final DataModelProperty property;

		public Property(IProject project, DMDLTreeData parent, DataModelProperty property) {
			super(project, parent);
			this.property = property;
		}

		@Override
		public Object getData() {
			return property;
		}

		@Override
		protected String getText() {
			return property.getName();
		}

		@Override
		protected String getText2() {
			return DataModelUtil.decodeDescription(property.getDescription());
		}

		@Override
		public boolean hasChildren() {
			return false;
		}

		@Override
		public List<DMDLTreeData> getChildren() {
			return null;
		}
	}
}
