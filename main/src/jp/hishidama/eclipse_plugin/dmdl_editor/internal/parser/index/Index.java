package jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index;

import org.eclipse.core.resources.IFile;

public interface Index {
	public IFile getFile();

	public int getOffset();

	public int getEnd();
}
