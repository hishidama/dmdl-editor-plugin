package jp.hishidama.eclipse_plugin.dmdl_editor.util;

import java.io.Serializable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

public abstract class DataModelPosition implements Serializable {
	private static final long serialVersionUID = -7040125071027361358L;

	private DataModelPosition parent;
	private int start, end;

	public IProject getProject() {
		if (parent != null) {
			return parent.getProject();
		}
		return null;
	}

	public IFile getFile() {
		if (parent != null) {
			return parent.getFile();
		}
		return null;
	}

	public final void setParent(DataModelPosition parent) {
		this.parent = parent;
	}

	public DataModelPosition getParent() {
		return parent;
	}

	public final void setOffset(int offset) {
		this.start = offset;
	}

	public final int getOffset() {
		return start;
	}

	public final void setEnd(int end) {
		this.end = end;
	}

	public final int getEnd() {
		return end;
	}

	public final int getLength() {
		return getEnd() - getOffset();
	}
}
