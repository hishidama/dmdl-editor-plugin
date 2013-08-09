package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.assist;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.assist.Assist.Word;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class AssistMatcher {
	protected static final String ANY = new String();

	protected List<Word> list;
	protected int offset;
	protected int matched;

	public AssistMatcher(List<Word> list, int offset) {
		this.list = list;
		this.offset = offset;
	}

	public int matchFirst(String... expected) {
		matched = 0;
		for (int i = 0; i < expected.length; i++) {
			if (list.size() <= i) {
				break;
			}
			Word t = list.get(i);
			if (expected[i] == ANY) {
				matched++;
				continue;
			}
			String word = t.getText();
			if (word != null) {
				if (expected[i].equals(word)) {
					matched++;
					continue;
				}
			}
			matched = 0;
			return matched;
		}

		return matched;
	}

	public int matchLast(String... expected) {
		int n = list.size() - 1;
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
					Word t = list.get(n - i + j);
					String word = t.getText();
					if (word != null) {
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
	public Word getToken(int n) {
		if (n < 0) {
			n += list.size();
		}
		return (0 <= n && n < list.size()) ? list.get(n) : null;
	}

	public String getWord(int n) {
		Word token = getToken(n);
		if (token != null) {
			return token.getText();
		}
		return null;
	}

	public String getLastWord() {
		int n = list.size() - 1;
		if (n >= 0) {
			return list.get(n).getText();
		}
		return null;
	}

	public int lastIndexOf(String find) {
		int n = list.size() - 1;
		for (; n >= 0; n--) {
			if (find.equals(getWord(n))) {
				return n;
			}
		}
		return -1; // not found
	}

	public List<ICompletionProposal> createAssist(IDocument document, String... candidate) {
		int start = list.get(list.size() - 1).getStart();
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
			if (s.length() >= text.length() && s.substring(0, text.length()).equalsIgnoreCase(text)) {
				list.add(new CompletionProposal(s, start, len, s.length()));
			}
		}
		return list;
	}

	public List<ICompletionProposal> createAssist(String... candidate) {
		List<ICompletionProposal> list = new ArrayList<ICompletionProposal>();
		addAssist(list, candidate);
		return list;
	}

	public void addAssist(List<ICompletionProposal> list, String... candidate) {
		for (String s : candidate) {
			if (s == null) {
				continue;
			}
			list.add(new CompletionProposal(s, offset, 0, s.length()));
		}
	}
}
