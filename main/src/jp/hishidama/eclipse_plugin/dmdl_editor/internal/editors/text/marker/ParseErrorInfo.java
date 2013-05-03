package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.marker;

import java.net.URI;

public class ParseErrorInfo {

	public URI file;
	public int level;
	public String message;
	public int beginLine;
	public int beginColumn;
	public int endLine;
	public int endColumn;
}
