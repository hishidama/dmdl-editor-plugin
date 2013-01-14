package jp.hishidama.eclipse_plugin.dmdl_editor.editors;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ICharacterPairMatcher;

public class DMBraceMatcher implements ICharacterPairMatcher {

	@Override
	public void dispose() {
	}

	@Override
	public void clear() {
	}

	@Override
	public IRegion match(IDocument document, int offset) {
		int len = document.getLength();
		if (offset >= len) {
			return null;
		}
		IRegion r = match(document, offset, '{', '}');
		if (r != null) {
			return r;
		}
		r = match(document, offset, '(', ')');
		return r;
	}

	protected IRegion match(IDocument document, int offset, char c, char d) {
		try {
			if (offset > 0 && document.getChar(offset - 1) == c) {
				// ‘Î‰ž‚·‚é•Â‚¶Š‡ŒÊ‚ÌˆÊ’u‚ð•Ô‚·
				int end = document.get().indexOf(d, offset);
				if (end >= 0) {
					return new Region(end, 1);
				}
			} else if (document.getChar(offset) == d) {
				// ‘Î‰ž‚·‚éŠJ‚«Š‡ŒÊ‚ÌˆÊ’u‚ð•Ô‚·
				int start = document.get().lastIndexOf(c, offset);
				if (start >= 0) {
					return new Region(start, 1);
				}
			}
		} catch (BadLocationException e) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID, 0,
					"DMBraceMatcher#match() location error.", e));
		}
		return null;
	}

	@Override
	public int getAnchor() {
		return LEFT;
	}
}
