package jp.hishidama.eclipse_plugin.dmdl_editor.editors.style;

import java.util.ArrayDeque;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLBodyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLTextToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;

public class DMDLTextTokenIterator {

	ArrayDeque<Pos> stack = new ArrayDeque<Pos>();

	public DMDLTextTokenIterator(DMDLToken token, int offset) {
		init(token, offset);
	}

	protected void init(DMDLToken token, int offset) {
		if (token instanceof DMDLTextToken) {
			stack.push(new Pos(token, null, 0));
		} else if (token instanceof DMDLBodyToken) {
			List<DMDLToken> list = ((DMDLBodyToken) token).getBody();
			stack.push(new Pos(token, list, 0));
		}
	}

	public DMDLTextToken next() {
		for (;;) {
			Pos pos = stack.peek();
			if (pos == null) {
				return null;
			}
			if (pos.list == null) {
				DMDLTextToken t = (DMDLTextToken) pos.token;
				stack.poll();
				pos = stack.peek();
				if (pos != null) {
					pos.i++;
				}
				return t;
			} else {
				if (pos.i < pos.list.size()) {
					DMDLToken t = pos.list.get(pos.i);
					if (t instanceof DMDLTextToken) {
						pos.i++;
						return (DMDLTextToken) t;
					}
					pos = new Pos(t, ((DMDLBodyToken) t).getBody(), 0);
					stack.push(pos);
					continue;
				}
				stack.poll();
				pos = stack.peek();
				if (pos != null) {
					pos.i++;
				}
			}
		}
	}

	protected static class Pos {
		public DMDLToken token;
		public List<DMDLToken> list;
		public int i;

		public Pos(DMDLToken token, List<DMDLToken> list, int i) {
			this.token = token;
			this.list = list;
			this.i = i;
		}
	}
}
