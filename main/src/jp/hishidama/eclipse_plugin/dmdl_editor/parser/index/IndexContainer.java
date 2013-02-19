package jp.hishidama.eclipse_plugin.dmdl_editor.parser.index;

import java.util.HashMap;
import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker.DMDLErrorCheckHandler;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

public class IndexContainer {
	static final QualifiedName KEY = new QualifiedName(null, "index");

	private Map<String, ModelIndex> map = new HashMap<String, ModelIndex>();

	public ModelIndex createModel(String modelName, IFile file, ModelToken model) {
		ModelIndex index = new ModelIndex(file, model);
		map.put(modelName, index);
		return index;
	}

	public void clear() {
		map.clear();
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

	public static IndexContainer createContainer(IProject project) {
		IndexContainer ic = new IndexContainer();
		try {
			project.setSessionProperty(KEY, ic);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return ic;
	}

	public static IndexContainer getContainer(IProject project, IFile file) {
		try {
			IndexContainer ic = (IndexContainer) project
					.getSessionProperty(KEY);
			if (ic == null) {
				DMDLErrorCheckHandler handler = new DMDLErrorCheckHandler();
				handler.execute(file, true, false);
				ic = (IndexContainer) project.getSessionProperty(KEY);
			}
			return ic;
		} catch (CoreException e) {
			return null;
		}
	}
}
