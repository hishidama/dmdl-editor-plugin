package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.assist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLTextToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken;

public class AssistMatcher {
	protected static final String ANY = new String();

	protected List<DMDLToken> list;
	protected int offset;
	protected int matched;
	protected DMDLToken cursorToken;

	public AssistMatcher(List<DMDLToken> list, int offset) {
		this.list = list;
		this.offset = offset;
	}

	public int matchFirst(String... expected) {
		matched = 0;
		cursorToken = null;
		for (int i = 0; i < expected.length; i++) {
			if (list.size() <= i) {
				break;
			}
			if (expected[i] == ANY) {
				matched++;
				continue;
			}
			DMDLToken t = list.get(i);
			if (t instanceof WordToken) {
				String word = ((WordToken) t).getBody();
				if (expected[i].equals(word)) {
					matched++;
					continue;
				}
			}
			break;
		}

		if (matched > 0) {
			DMDLToken last = list.get(matched - 1);
			if (last.getStart() <= offset && offset < last.getEnd()) {
				cursorToken = last;
				matched--;
			}
		}

		return matched;
	}

	public int matchLast(String... expected) {
		cursorToken = null;
		int n = list.size() - 1;
		if (n >= 0) {
			DMDLToken last = list.get(n);
			if (last.getStart() <= offset && offset < last.getEnd()) {
				cursorToken = last;
				n--;
			}
		}
		for (int i = expected.length - 1; i >= 0; i--) {
			if (n < i) {
				continue;
			}
			matched = 0;
			for (int j = 0; j <= i; j++) {
				if (n <= j) {
					break;
				}
				if (expected[j] == ANY) {
					matched++;
				} else {
					DMDLToken t = list.get(n - i + j);
					if (t instanceof WordToken) {
						String word = ((WordToken) t).getBody();
						if (expected[j].equals(word)) {
							matched++;
						}
					}
				}
			}
			if (matched == i + 1) {
				return matched;
			}
		}

		matched = 0;
		return matched;
	}

	/**
	 * トークン取得.
	 *
	 * @param n
	 *            インデックス（マイナスの場合、リストの末尾からのインデックスを意味する（-1が一番末尾））
	 * @return トークン（範囲外の場合はnull）
	 */
	public DMDLToken getToken(int n) {
		if (n < 0) {
			if (cursorToken != null) {
				n--;
			}
			n += list.size();
		}
		return (0 <= n && n < list.size()) ? list.get(n) : null;
	}

	public String getWord(int n) {
		DMDLToken token = getToken(n);
		if (token instanceof DMDLTextToken) {
			return ((DMDLTextToken) token).getBody();
		}
		return null;
	}

	public int lastIndexOf(String find) {
		int n = list.size() - 1;
		if (cursorToken != null) {
			n--;
		}
		for (; n >= 0; n--) {
			if (find.equals(getWord(n))) {
				return n;
			}
		}
		return -1; // not found
	}

	public List<ICompletionProposal> createAssist(IDocument document,
			String... candidate) {
		if (cursorToken != null) {
			int start = cursorToken.getStart();
			int len = offset - start;
			String text;
			try {
				text = document.get(start, len);
			} catch (BadLocationException e) {
				return null;
			}
			List<ICompletionProposal> list = new ArrayList<ICompletionProposal>();
			for (String s : candidate) {
				if (s == null) {
					continue;
				}
				if (s.length() >= text.length()
						&& s.substring(0, text.length()).equalsIgnoreCase(text)) {
					list.add(new CompletionProposal(s, start, len, s.length()));
				}
			}
			return list;
		} else {
			List<ICompletionProposal> list = new ArrayList<ICompletionProposal>();
			for (String s : candidate) {
				if (s == null) {
					continue;
				}
				list.add(new CompletionProposal(s, offset, 0, s.length()));
			}
			return list;
		}
	}
}
