package jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;

import com.asakusafw.dmdl.source.DmdlSourceRepository;

public class DmdlSourceString implements DmdlSourceRepository {

	private URI name;
	private String text;

	public DmdlSourceString(URI name, String text) {
		this.name = name;
		this.text = text;
	}

	@Override
	public Cursor createCursor() throws IOException {
		return new StringCursor();
	}

	protected class StringCursor implements Cursor {
		private StringReader reader;

		@Override
		public URI getIdentifier() throws IOException {
			return name;
		}

		@Override
		public boolean next() throws IOException {
			if (text != null) {
				reader = new StringReader(text);
				text = null;
				return true;
			} else {
				return false;
			}
		}

		@Override
		public Reader openResource() throws IOException {
			return reader;
		}

		@Override
		public void close() throws IOException {
			if (reader != null) {
				reader.close();
				reader = null;
			}
		}
	}
}
