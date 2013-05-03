package jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.CommentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken;

import org.eclipse.core.resources.IProject;

public class PositionUtil {

	public static String getQualifiedName(IProject project, DMDLToken token) {
		while (token != null) {
			if (token instanceof WordToken) {
				WordToken word = (WordToken) token;
				switch (word.getWordType()) {
				case REF_MODEL_NAME: {
					String name = word.getBody();
					WordToken ref = word.getReferenceWord();
					if (ref != null) {
						ModelToken model = ref.getModelToken();
						if (model != null) {
							return model.getQualifiedName();
						}
						return name;
					} else {
						IndexContainer ic = IndexContainer
								.getContainer(project);
						if (ic != null) {
							ModelIndex index = ic.findModel(name);
							if (index != null) {
								ModelToken model = index.getToken();
								String file = index.getFile().getFullPath()
										.lastSegment();
								return model.getQualifiedName() + " (" + file
										+ ")";
							}
						}
						return null;
					}
				}
				case REF_PROPERTY_NAME: {
					WordToken ref = word.getReferenceWord();
					if (ref != null) {
						IndexContainer ic = IndexContainer
								.getContainer(project);
						PropertyToken prop = (PropertyToken) ref.getParent();
						return prop.getQualifiedName(ic);
					} else {
						DMDLToken model = word.findRefModelToken();
						if (model instanceof WordToken) {
							IndexContainer ic = IndexContainer
									.getContainer(project);
							if (ic != null) {
								String modelName = ((WordToken) model)
										.getBody();
								PropertyIndex index = ic.findProperty(
										modelName, word.getBody());
								if (index != null) {
									PropertyToken p = index.getToken();
									String file = index.getFile().getFullPath()
											.lastSegment();
									return p.getQualifiedName(ic) + " (" + file
											+ ")";
								}
							}
						}
					}
				}
					return null;
				}
			} else if (token instanceof PropertyToken) {
				IndexContainer ic = IndexContainer.getContainer(project);
				PropertyToken prop = (PropertyToken) token;
				return prop.getQualifiedName(ic);
			} else if (token instanceof ModelToken) {
				ModelToken model = (ModelToken) token;
				return model.getQualifiedName();
			} else if (token instanceof CommentToken) {
				return null;
			}
			token = token.getParent();
		}
		return null;
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
					String name = word.getBody();
					WordToken ref = word.getReferenceWord();
					if (ref != null) {
						ModelToken model = ref.getModelToken();
						if (model != null) {
							return new NamePair(model.getModelName(), null);
						}
						return new NamePair(name, null);
					} else {
						IndexContainer ic = IndexContainer
								.getContainer(project);
						if (ic != null) {
							ModelIndex index = ic.findModel(name);
							if (index != null) {
								ModelToken model = index.getToken();
								return new NamePair(model.getModelName(), null);
							}
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
							IndexContainer ic = IndexContainer
									.getContainer(project);
							if (ic != null) {
								String modelName = ((WordToken) model)
										.getBody();
								PropertyIndex index = ic.findProperty(
										modelName, word.getBody());
								if (index != null) {
									PropertyToken prop = index.getToken();
									return new NamePair(modelName,
											prop.getPropertyName());
								}
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
