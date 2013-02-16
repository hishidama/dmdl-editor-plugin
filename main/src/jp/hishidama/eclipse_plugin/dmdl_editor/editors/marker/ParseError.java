package jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker;

import java.net.URI;

public class ParseError {

	public URI file;
	public int level;
	public String message;
	public int beginLine;
	public int beginColumn;
	public int endLine;
	public int endColumn;
}
