package jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token;

import java.util.ArrayDeque;
import java.util.List;

public class DMDLTextTokenIterator {

	protected int start;
	protected ArrayDeque<Pos> stack = new ArrayDeque<Pos>();

	public DMDLTextTokenIterator(DMDLToken token, int offset) {
		this.start = offset;
		init(token);
	}

	protected void init(DMDLToken token) {
		if (token instanceof DMDLTextToken) {
			stack.push(new Pos(token, null, 0));
		} else if (token instanceof DMDLBodyToken) {
			List<DMDLToken> list = ((DMDLBodyToken) token).getBody();
			stack.push(new Pos(token, list, 0));
		}
	}

	public DMDLTextToken next() {
		for (;;) {
			DMDLTextToken token = next0();
			if (token == null) {
				return null;
			}
			if (token.getEnd() <= start) {
				continue;
			}
			return token;
		}
	}

	protected DMDLTextToken next0() {
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
					if (t.getEnd() <= start) {
						pos.i++;
						continue;
					}
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
