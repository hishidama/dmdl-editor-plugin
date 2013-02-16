package jp.hishidama.eclipse_plugin.dmdl_editor.editors.marker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.asakusafw.dmdl.source.DmdlSourceRepository;

public class DmdlSourceUri implements DmdlSourceRepository {

	public static class Info {
		URI file;
		String encoding;

		public Info(URI file, String encoding) {
			this.file = file;
			this.encoding = encoding;
		}
	}

	private final List<Info> resources;

	public DmdlSourceUri(List<Info> files) {
		this.resources = files;
	}

	@Override
	public Cursor createCursor() throws IOException {
		return new UrlListCursor(resources.iterator());
	}

	protected static class UrlListCursor implements Cursor {

		private final Iterator<Info> rest;

		private Info current;

		public UrlListCursor(Iterator<Info> iterator) {
			this.current = null;
			this.rest = iterator;
		}

		@Override
		public boolean next() throws IOException {
			if (rest.hasNext()) {
				Info arr = rest.next();
				current = arr;
				return true;
			} else {
				current = null;
				return false;
			}
		}

		@Override
		public URI getIdentifier() throws IOException {
			if (current == null) {
				throw new NoSuchElementException();
			}
			return current.file;
		}

		@Override
		public Reader openResource() throws IOException {
			if (current == null) {
				throw new NoSuchElementException();
			}
			InputStream in = current.file.toURL().openStream();
			return new InputStreamReader(in, current.encoding);
		}

		@Override
		public void close() {
			current = null;
			while (rest.hasNext()) {
				rest.next();
			}
		}
	}
}
