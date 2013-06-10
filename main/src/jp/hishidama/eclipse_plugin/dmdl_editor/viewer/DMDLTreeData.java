package jp.hishidama.eclipse_plugin.dmdl_editor.viewer;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelFile;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelInfo;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelProperty;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelUtil;

import org.eclipse.core.resources.IProject;

public abstract class DMDLTreeData {
	private DMDLTreeData parent;
	protected final IProject project;
	private Object otherData = null;

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
		public boolean hasChildren() {
			return false;
		}

		@Override
		public List<DMDLTreeData> getChildren() {
			return null;
		}
	}
}
