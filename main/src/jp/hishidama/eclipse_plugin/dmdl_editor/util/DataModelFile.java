package jp.hishidama.eclipse_plugin.dmdl_editor.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

public class DataModelFile extends DataModelPosition {
	private static final long serialVersionUID = 7785572714927367718L;

	private transient IProject project;
	private String projectName;

	private transient IFile file;
	private String filePath;

	private List<DataModelInfo> models;

	private transient Map<String, DataModelInfo> snakeMap;
	private transient Map<String, DataModelInfo> camelMap;

	public DataModelFile(IFile file) {
		setFile(file);
	}

	public DataModelFile(IProject project, String filePath) {
		setProject(project);
		this.filePath = filePath;
	}

	/**
	 * for unit test.
	 *
	 * @param filePath
	 *            file path
	 */
	protected DataModelFile(String filePath) {
		this.filePath = filePath;
	}

	private void setProject(IProject project) {
		this.project = project;
		this.projectName = project.getName();
	}

	private void setFile(IFile file) {
		this.file = file;
		this.filePath = file.getProjectRelativePath().toPortableString();
		setProject(file.getProject());
	}

	@Override
	public IProject getProject() {
		if (project == null) {
			if (projectName == null) {
				return null;
			}
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			project = root.getProject(projectName);
		}
		return project;
	}

	@Override
	public IFile getFile() {
		if (file == null) {
			if (filePath == null) {
				return null;
			}
			IProject project = getProject();
			file = project.getFile(filePath);
		}
		return file;
	}

	public String getFilePath() {
		return filePath;
	}

	public boolean isModelsNull() {
		return models == null;
	}

	public List<DataModelInfo> getModels() {
		if (models == null) {
			models = DataModelUtil.getModels(getFile());
			for (DataModelInfo info : models) {
				info.setParent(this);
			}
		}
		return models;
	}

	public void clearModels() {
		models = null;
	}

	public void addModel(DataModelInfo info) {
		info.setParent(this);

		if (models == null) {
			models = new ArrayList<DataModelInfo>();
		}
		models.add(info);
	}

	public DataModelInfo getProperty(String name) {
		if (snakeMap == null) {
			snakeMap = new HashMap<String, DataModelInfo>();
			for (DataModelInfo i : getModels()) {
				snakeMap.put(i.getModelName(), i);
			}
		}
		DataModelInfo info = snakeMap.get(name);
		if (info != null) {
			return info;
		}

		if (camelMap == null) {
			camelMap = new HashMap<String, DataModelInfo>();
			for (DataModelInfo i : getModels()) {
				String cname = StringUtil.toCamelCase(i.getModelName());
				camelMap.put(cname, i);
			}
		}
		return camelMap.get(name);
	}
}
