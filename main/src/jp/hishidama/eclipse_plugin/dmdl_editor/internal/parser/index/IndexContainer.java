package jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.marker.DMDLErrorCheckHandler;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.util.StringUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;

public class IndexContainer {
	static final QualifiedName KEY = new QualifiedName(Activator.PLUGIN_ID, "IndexContainer.index");

	private Map<String, ModelIndex> map = new HashMap<String, ModelIndex>();
	private Map<String, ModelIndex> snakeMap = null;

	public ModelIndex createModel(String modelName, IFile file, ModelToken model) {
		ModelIndex index = new ModelIndex(file, model);
		map.put(modelName, index);
		return index;
	}

	public void clear() {
		map.clear();
	}

	public Collection<ModelIndex> getModels() {
		return map.values();
	}

	public ModelIndex findModel(String modelName) {
		return map.get(modelName);
	}

	public PropertyIndex findProperty(String modelName, String propertyName) {
		ModelIndex model = map.get(modelName);
		if (model == null) {
			return null;
		}
		return model.getProperty(propertyName);
	}

	public ModelIndex findModelSnake(String modelSnakeName) {
		if (snakeMap == null) {
			snakeMap = new HashMap<String, ModelIndex>(map.size());
			for (Entry<String, ModelIndex> entry : map.entrySet()) {
				String name = StringUtil.toCamelCase(entry.getKey());
				snakeMap.put(name, entry.getValue());
			}
		}
		return snakeMap.get(modelSnakeName);
	}

	public static IndexContainer createContainer(IProject project) {
		IndexContainer ic = new IndexContainer();
		try {
			project.setSessionProperty(KEY, ic);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return ic;
	}

	public static IndexContainer getContainer(IProject project) {
		try {
			IndexContainer ic = (IndexContainer) project.getSessionProperty(KEY);
			if (ic == null) {
				DMDLErrorCheckHandler handler = new DMDLErrorCheckHandler();
				handler.execute(project, true, false); // create IndexContainer
				ic = (IndexContainer) project.getSessionProperty(KEY);
			}
			return ic;
		} catch (Exception e) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, MessageFormat.format(
					"getting IndexContainer error. project={0}", project), e));
			return null;
		}
	}

	public static void remove(IProject project) {
		try {
			project.setSessionProperty(KEY, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
