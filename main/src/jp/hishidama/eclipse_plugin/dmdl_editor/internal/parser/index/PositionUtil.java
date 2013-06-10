package jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.CommentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelInfo;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelProperty;
import jp.hishidama.eclipse_plugin.dmdl_editor.util.DataModelUtil;

import org.eclipse.core.resources.IProject;

public class PositionUtil {

	public static String getQualifiedName(IProject project, DMDLToken token) {
		while (token != null) {
			if (token instanceof WordToken) {
				WordToken word = (WordToken) token;
				switch (word.getWordType()) {
				case REF_MODEL_NAME: {
					String name = word.getText();
					WordToken ref = word.getReferenceWord();
					if (ref != null) {
						ModelToken model = ref.getModelToken();
						if (model != null) {
							String qname = DataModelUtil.getQualifiedModelName(model.getModelName(),
									model.getDescription(), model.getModelType());
							return qname;
						}
						return name;
					} else {
						IndexContainer ic = IndexContainer.getContainer(project);
						DataModelInfo model = ic.getModel(name);
						if (model != null) {
							String file = model.getFile().getFullPath().lastSegment();
							String qname = DataModelUtil.getQualifiedModelName(model.getModelName(),
									model.getModelDescription(), model.getModelType());
							return qname + " (" + file + ")";
						}
						return null;
					}
				}
				case REF_PROPERTY_NAME: {
					WordToken ref = word.getReferenceWord();
					if (ref != null) {
						PropertyToken prop = (PropertyToken) ref.getParent();
						return getQualifiedName(project, prop);
					} else {
						DMDLToken model = word.findRefModelToken();
						if (model instanceof WordToken) {
							IndexContainer ic = IndexContainer.getContainer(project);
							String modelName = ((WordToken) model).getText();
							DataModelProperty index = ic.getProperty(modelName, word.getText());
							if (index != null) {
								String file = index.getFile().getFullPath().lastSegment();
								String pname = index.getName();
								String type = DataModelUtil.getResolvedDataType(project, modelName, pname);
								String qname = DataModelUtil.getQualifiedPropertyName(pname, index.getDescription(),
										type);
								return qname + " (" + file + ")";
							}
						}
					}
				}
					return null;
				}
			} else if (token instanceof PropertyToken) {
				PropertyToken prop = (PropertyToken) token;
				return getQualifiedName(project, prop);
			} else if (token instanceof ModelToken) {
				ModelToken model = (ModelToken) token;
				String qname = DataModelUtil.getQualifiedModelName(model.getModelName(), model.getDescription(),
						model.getModelType());
				return qname;
			} else if (token instanceof CommentToken) {
				return null;
			}
			token = token.getParent();
		}
		return null;
	}

	private static String getQualifiedName(IProject project, PropertyToken prop) {
		String pname = prop.getName();
		String modelName = prop.getModelToken().getModelName();
		String type = DataModelUtil.getResolvedDataType(project, modelName, pname);
		return DataModelUtil.getQualifiedPropertyName(pname, prop.getPropertyDescription(), type);
	}

	public static class NamePair {
		private String modelName;
		private String propName;

		public NamePair(String modelName, String propName) {
			this.modelName = modelName;
			this.propName = propName;
		}

		public String getModelName() {
			return modelName;
		}

		public String getPropertyName() {
			return propName;
		}

		@Override
		public String toString() {
			return String.format("NamePair(%s, %s)", modelName, propName);
		}
	}

	public static NamePair getName(IProject project, DMDLToken token) {
		while (token != null) {
			if (token instanceof WordToken) {
				WordToken word = (WordToken) token;
				switch (word.getWordType()) {
				case REF_MODEL_NAME: {
					String name = word.getText();
					WordToken ref = word.getReferenceWord();
					if (ref != null) {
						ModelToken model = ref.getModelToken();
						if (model != null) {
							return new NamePair(model.getModelName(), null);
						}
						return new NamePair(name, null);
					} else {
						IndexContainer ic = IndexContainer.getContainer(project);
						DataModelInfo info = ic.getModel(name);
						if (info != null) {
							return new NamePair(info.getModelName(), null);
						}
						return null;
					}
				}
				case REF_PROPERTY_NAME: {
					WordToken ref = word.getReferenceWord();
					if (ref != null) {
						PropertyToken prop = (PropertyToken) ref.getParent();
						return createNamePair(prop);
					} else {
						DMDLToken model = word.findRefModelToken();
						if (model instanceof WordToken) {
							IndexContainer ic = IndexContainer.getContainer(project);
							String modelName = ((WordToken) model).getText();
							DataModelProperty prop = ic.getProperty(modelName, word.getText());
							if (prop != null) {
								return new NamePair(modelName, prop.getName());
							}
						}
					}
				}
					return null;
				}
			} else if (token instanceof PropertyToken) {
				PropertyToken prop = (PropertyToken) token;
				return createNamePair(prop);
			} else if (token instanceof ModelToken) {
				ModelToken model = (ModelToken) token;
				return new NamePair(model.getModelName(), null);
			} else if (token instanceof CommentToken) {
				return null;
			}
			token = token.getParent();
		}
		return null;
	}

	private static NamePair createNamePair(PropertyToken prop) {
		ModelToken model = prop.getModelToken();
		if (model != null) {
			return new NamePair(model.getModelName(), prop.getPropertyName());
		}
		return null;
	}
}
