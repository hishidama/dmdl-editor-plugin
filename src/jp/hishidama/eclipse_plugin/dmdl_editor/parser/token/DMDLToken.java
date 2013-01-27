package jp.hishidama.eclipse_plugin.dmdl_editor.parser.token;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.style.DMScanner.AttrType;

public abstract class DMDLToken {

	protected int start;
	protected int end;
	protected DMDLToken parentToken;
	protected WordToken refModelName;

	public DMDLToken(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int getLength() {
		return end - start;
	}

	/**
	 * 親Token設定.
	 *
	 * @param token
	 *            親Token
	 */
	public void setParent(DMDLToken token) {
		this.parentToken = token;
	}

	/**
	 * 親Token取得.
	 *
	 * @return 親Token（無い場合はnull）
	 */
	public DMDLToken getParent() {
		return parentToken;
	}

	/**
	 * 参照先WordToken取得.
	 *
	 * @return 参照先Word（無い場合はnull）
	 */
	public WordToken getReferenceWord() {
		return null;
	}

	public abstract DMDLToken getTokenByOffset(int offset);

	public AttrType getStyleAttribute() {
		return AttrType.DEFAULT;
	}

	public ModelList getTop() {
		for (DMDLToken token = this; token != null; token = token.getParent()) {
			if (token instanceof ModelList) {
				return (ModelList) token;
			}
		}
		return null;
	}

	public ModelToken findModel(String name) {
		ModelList models = getTop();
		if (models == null) {
			return null;
		}
		for (DMDLToken token : models.getBody()) {
			if (token instanceof ModelToken) {
				ModelToken model = (ModelToken) token;
				if (name.equals(model.getModelName())) {
					return model;
				}
			}
		}
		return null;
	}

	public void setRefModelName(WordToken token) {
		refModelName = token;
	}

	public WordToken getRefModelNameToken() {
		return refModelName;
	}

	public WordToken findRefModelNameToken() {
		for (DMDLToken token = this; token != null; token = token.getParent()) {
			WordToken t = token.getRefModelNameToken();
			if (t != null) {
				return t;
			}
		}
		return null;
	}
}
