package jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.DMDLSimpleParser;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.DocumentScanner;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.DMDLFileUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.DMDLFileUtil.DocumentManager;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelFile;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelInfo;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelProperty;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.text.IDocument;

public class IndexContainer implements Serializable {
	private static final long serialVersionUID = -2379041961753691214L;

	private static final Map<IProject, IndexContainer> indexMap = new ConcurrentHashMap<IProject, IndexContainer>();

	private transient IProject project;

	private Map<String, DataModelFile> fileMap = new TreeMap<String, DataModelFile>();
	private transient Map<String, DataModelInfo> modelMap;
	private transient Map<String, DataModelInfo> camelMap;

	public IndexContainer(IProject project) {
		setProject(project);
	}

	private void setProject(IProject project) {
		this.project = project;
	}

	public void initialize(IProgressMonitor monitor) throws InterruptedException {
		monitor.beginTask("IndexContainer#initialize", 10);
		try {
			List<IFile> files = DMDLFileUtil.getDmdlFiles(project);
			monitor.worked(1);

			initialize(files, new SubProgressMonitor(monitor, 8));

			save();
			monitor.worked(1);
		} finally {
			monitor.done();
		}
	}

	private void initialize(List<IFile> files, IProgressMonitor monitor) throws InterruptedException {
		monitor.beginTask("IndexContainer#initialize", files.size());
		try {
			DMDLSimpleParser parser = new DMDLSimpleParser();

			for (IFile file : files) {
				cancelCheck(monitor);
				initialize(file, parser);
				monitor.worked(1);
			}
		} finally {
			monitor.done();
		}
	}

	private void initialize(IFile file, DMDLSimpleParser parser) {
		DataModelFile f = new DataModelFile(file);
		initialize(f, parser);
	}

	private void initialize(DataModelFile f, DMDLSimpleParser parser) {
		ModelList models = getModels(f.getFile(), parser);
		for (ModelToken model : models.getNamedModelList()) {
			initialize(f, model);
		}
		addFile(f, true);
	}

	private ModelList getModels(IFile file, DMDLSimpleParser parser) {
		DocumentManager dm = DMDLFileUtil.getDocument(file);
		try {
			IDocument document = dm.getDocument();
			DocumentScanner scanner = new DocumentScanner(document);
			ModelList models = parser.parse(scanner);
			return models;
		} finally {
			try {
				dm.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void addFile(DataModelFile file, boolean put) {
		fileMap.put(file.getFilePath(), file);
		if (modelMap != null && put) {
			putMap(file);
		}
	}

	public void initialize(DataModelFile f, ModelToken model) {
		DataModelInfo info = new DataModelInfo(model.getModelName(), model.getDescription(), model.getModelType());
		{
			WordToken nameToken = model.getModelNameToken();
			info.setOffset(nameToken.getStart());
			info.setEnd(nameToken.getEnd());
			f.addModel(info);
		}

		for (PropertyToken token : model.getPropertyList()) {
			String pname = token.getName();
			String pdesc = token.getPropertyDescription();

			DataModelProperty p;
			if (token.getRefNameToken() == null) {
				p = new DataModelProperty(pname, pdesc, token.getDataType());
			} else {
				String refName = token.getRefName();
				String refModelName = token.findRefModelName();
				p = new DataModelProperty(pname, pdesc, refModelName, refName);
				p.setSumType(token.getSumType());
			}
			WordToken nameToken = token.getNameToken();
			p.setOffset(nameToken.getStart());
			p.setEnd(nameToken.getEnd());
			info.addProperty(p);
		}
	}

	protected final void cancelCheck(IProgressMonitor monitor) throws InterruptedException {
		if (monitor.isCanceled()) {
			throw new InterruptedException();
		}
	}

	public void refresh(IFile file, ModelList models) {
		if (models == null) {
			DMDLSimpleParser parser = new DMDLSimpleParser();
			models = getModels(file, parser);
		}

		DataModelFile f = getFileFromMap(file);
		if (f == null) {
			f = new DataModelFile(file);
		}
		refresh(f, models);
	}

	private void refresh(DataModelFile f, ModelList models) {
		if (!f.isModelsNull()) {
			for (DataModelInfo info : f.getModels()) {
				String modelName = info.getModelName();
				if (modelMap != null) {
					modelMap.remove(modelName);
				}
				if (camelMap != null) {
					camelMap.remove(StringUtil.toCamelCase(modelName));
				}
			}
			f.clearModels();
		}
		for (ModelToken model : models.getNamedModelList()) {
			initialize(f, model);
		}
		addFile(f, true);
		save();
	}

	public List<DataModelFile> getFiles() {
		return new ArrayList<DataModelFile>(fileMap.values());
	}

	public DataModelFile getFile(IFile file) {
		DataModelFile f = getFileFromMap(file);
		if (f == null) {
			f = new DataModelFile(file);
			addFile(f, false);
		}
		return f;
	}

	private DataModelFile getFileFromMap(IFile file) {
		return fileMap.get(file.getProjectRelativePath().toPortableString());
	}

	public List<DataModelInfo> getModels(IFile file) {
		DataModelFile f = getFileFromMap(file);
		if (f == null) {
			f = new DataModelFile(file);
		}
		if (f.isModelsNull()) {
			DMDLSimpleParser parser = new DMDLSimpleParser();
			initialize(f, parser);
		}
		return f.getModels();
	}

	public DataModelInfo getModel(String modelName) {
		if (modelMap == null) {
			modelMap = new HashMap<String, DataModelInfo>();
			camelMap = new HashMap<String, DataModelInfo>();
			for (DataModelFile file : fileMap.values()) {
				putMap(file);
			}
		}

		DataModelInfo info = modelMap.get(modelName);
		if (info != null) {
			return info;
		}
		return camelMap.get(modelName);
	}

	private void putMap(DataModelFile file) {
		for (DataModelInfo info : file.getModels()) {
			String mname = info.getModelName();
			modelMap.put(mname, info);
			String cname = StringUtil.toCamelCase(mname);
			camelMap.put(cname, info);
		}
	}

	public DataModelProperty getProperty(String modelName, String propertyName) {
		DataModelInfo model = getModel(modelName);
		if (model != null) {
			return model.getProperty(propertyName);
		}
		return null;
	}

	public String getResolvedDataType(String modelName, String propertyName) {
		return getResolvedDataType(modelName, propertyName, new HashSet<String>());
	}

	private String getResolvedDataType(String modelName, String propertyName, Set<String> set) {
		if (modelName == null || propertyName == null) {
			return null;
		}

		String key = modelName + "#" + propertyName;
		if (set.contains(key)) {
			return null;
		}
		set.add(key);

		DataModelProperty prop = getProperty(modelName, propertyName);
		if (prop == null) {
			return null;
		}
		String dataType = getDataType(prop, set);
		String sumType = prop.getSumType();
		if (sumType == null) {
			return dataType;
		}

		if ("count".equals(sumType)) {
			return "LONG";
		}
		if ("sum".equals(sumType)) {
			if ("BYTE".equals(dataType) || "SHORT".equals(dataType) || "INT".equals(dataType)
					|| "LONG".equals(dataType)) {
				return "LONG";
			} else if ("FLOAT".equals(dataType) || "DOUBLE".equals(dataType)) {
				return "DOUBLE";
			} else if ("DECIMAL".equals(dataType)) {
				return "DECIMAL";
			}
			return null;
		}
		if ("any".equals(sumType) || "min".equals(sumType) || "max".equals(sumType)) {
			return dataType;
		}
		return null;
	}

	private String getDataType(DataModelProperty prop, Set<String> set) {
		String type = prop.getDataType();
		if (type != null) {
			return type;
		}

		String refModelName = prop.getRefModelName();
		String refName = prop.getRefPropertyName();
		return getResolvedDataType(refModelName, refName, set);
	}

	public static IndexContainer getContainer(IProject project) {
		try {
			return getContainer(project, null);
		} catch (InterruptedException e) {
			throw new IllegalStateException("InterruptedExceptionは発生しないはず");
		}
	}

	public static IndexContainer getContainer(IProject project, IProgressMonitor monitor) throws InterruptedException {
		try {
			IndexContainer ic = indexMap.get(project);
			if (ic == null) {
				ic = load(project);
				if (ic != null) {
					indexMap.put(project, ic);
				}
			}
			if (ic == null) {
				ic = new IndexContainer(project);
				if (monitor != null) {
					ic.initialize(monitor);
				}
				indexMap.put(project, ic);
			}
			return ic;
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception e) {
			IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, MessageFormat.format(
					"getting IndexContainer error. project={0}", project), e);
			Activator.getDefault().getLog().log(status);

			IndexContainer ic = new IndexContainer(project);
			indexMap.put(project, ic);
			return ic;
		}
	}

	private static IndexContainer load(IProject project) {
		File file = getIndexFileName(project);
		if (!file.exists()) {
			return null;
		}
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			try {
				IndexContainer ic = (IndexContainer) ois.readObject();
				ic.setProject(project);
				return ic;
			} finally {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, MessageFormat.format(
					"load IndexContainer error. project={0}", project), e);
			Activator.getDefault().getLog().log(status);
			return null;
		}
	}

	private void save() {
		File file = getIndexFileName(project);
		try {
			file.getParentFile().mkdirs();
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			try {
				oos.writeObject(this);
			} finally {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, MessageFormat.format(
					"save IndexContainer error. project={0}", project), e);
			Activator.getDefault().getLog().log(status);
		}
	}

	public static void remove(IProject project) {
		indexMap.remove(project);
		File file = getIndexFileName(project);
		file.delete();
	}

	private static File getIndexFileName(IProject project) {
		String name = project.getName();
		IPath tempDir = Activator.getDefault().getStateLocation();
		File dir = new File(tempDir.toFile(), "index");
		File file = new File(dir, name + ".dat");
		return file;
	}
}
