package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.format;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.preference.PreferenceConst;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.AnnotationToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ArgumentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ArgumentsToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ArrayToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.BlockToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.CommentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLBodyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLTextToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.DescriptionToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.WordToken;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.IFormattingStrategy;

public class DMDLContentFormatter implements IContentFormatter {
	protected DMDLDocument document;
	protected int start;
	protected int end;
	protected int changeStart;
	protected StringBuilder sb;
	protected DMDLTextToken prevText;
	protected String LF = "\r\n";
	protected String INDENT_ARGUMENT = "  ";
	protected String INDENT_PROPERTY = "    ";

	@Override
	public void format(IDocument document, IRegion region) {
		init();

		DMDLDocument doc = (DMDLDocument) document;
		this.document = doc;
		ModelList models = doc.getModelList();

		this.start = region.getOffset();
		int length = region.getLength();
		this.end = start + length;

		changeStart = -1;
		sb = new StringBuilder(length);
		prevText = null;
		defaultFormatter.format(models);

		if (prevText != null && ";".equals(prevText.getBody())) {
			append(prevText.getStart(), LF);
		}

		String text = sb.toString();
		try {
			document.replace(start, length, text); // TODO start,length
		} catch (BadLocationException e) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
					"DMDLContentFormatter bad location.", e));
		}
	}

	protected Map<String, Object> simulate = null;

	public void setSimulate(String key, Object value) {
		if (simulate == null) {
			simulate = new HashMap<String, Object>();
		}
		simulate.put(key, value);
	}

	private void init() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		INDENT_ARGUMENT = space(getInt(store,
				PreferenceConst.FORMAT_INDENT_ARGUMENT));
		INDENT_PROPERTY = space(getInt(store,
				PreferenceConst.FORMAT_INDENT_PROPERTY));
	}

	private int getInt(IPreferenceStore store, String key) {
		if (simulate != null) {
			Integer value = (Integer) simulate.get(key);
			if (value != null) {
				return value;
			}
		}
		return store.getInt(key);
	}

	private String space(int n) {
		char[] buf = new char[n];
		Arrays.fill(buf, ' ');
		return new String(buf);
	}

	protected TokenFormatter<DMDLBodyToken> defaultFormatter = new TokenFormatter<DMDLBodyToken>();

	protected class TokenFormatter<T extends DMDLBodyToken> {
		public void format(T body) {
			boolean firstToken = true;
			boolean firstWord = true;
			for (DMDLToken token : body.getBody()) {
				if (token.getEnd() <= start) {
					if (token instanceof WordToken) {
						firstWord = false;
					}
					if (token instanceof DMDLTextToken) {
						prevText = (DMDLTextToken) token;
					}
					firstToken = false;
					continue;
				}
				if (end <= token.getStart()) {
					break;
				}
				if (token instanceof WordToken) {
					WordToken word = (WordToken) token;
					formatWord(word, firstToken, word.getBody(), firstWord);
					firstWord = false;
					prevText = word;
				} else if (token instanceof DescriptionToken) {
					DescriptionToken desc = (DescriptionToken) token;
					formatDescription(desc, firstToken);
					prevText = desc;
				} else if (token instanceof AnnotationToken) {
					AnnotationToken anno = (AnnotationToken) token;
					formatAnnotation(anno, firstToken);
					prevText = anno;
				} else if (token instanceof DMDLTextToken) {
					DMDLTextToken text = (DMDLTextToken) token;
					formatText(text, firstToken);
					prevText = text;
				} else {
					DMDLBodyToken t = (DMDLBodyToken) token;
					if (t instanceof ModelToken) {
						modelFormatter.format((ModelToken) t);
					} else if (t instanceof ArgumentsToken) {
						argumentsFormatter.format((ArgumentsToken) t);
					} else if (t instanceof ArgumentToken) {
						argumentFormatter.format((ArgumentToken) t);
					} else if (t instanceof BlockToken) {
						blockFormatter.format((BlockToken) t);
					} else if (t instanceof PropertyToken) {
						propertyFormatter.format((PropertyToken) t);
					} else if (t instanceof ArrayToken) {
						arrayFormatter.format((ArrayToken) t);
					} else {
						defaultFormatter.format(t);
					}
				}
				firstToken = false;
			}
		}

		protected void formatWord(WordToken token, boolean firstToken,
				String word, boolean firstWord) {
			appendIfNotLf(token.getStart(), " ");
			append(token.getStart(), token.getBody());
		}

		protected void formatDescription(DescriptionToken token,
				boolean firstToken) {
			appendIfNotLf(token.getStart(), LF);
			append(token.getStart(), token.getBody());
		}

		protected void formatAnnotation(AnnotationToken token,
				boolean firstToken) {
			appendIfNotLf(token.getStart(), LF);
			append(token.getStart(), token.getBody());
		}

		protected void formatText(DMDLTextToken token, boolean firstToken) {
			if (firstToken) {
				DMDLToken parent = token.getParent();
				shrinkLf(parent.getStart(), token.getStart(), token);
			} else if (prevText != null) {
				shrinkLf(prevText.getEnd(), token.getStart(), token);
			}
			appendIfNotLf(token.getStart(), " ");
			append(token.getStart(), token.getBody());
		}
	}

	protected TokenFormatter<ModelToken> modelFormatter = new TokenFormatter<ModelToken>() {
		@Override
		protected void formatWord(WordToken token, boolean firstToken,
				String word, boolean firstWord) {
			prepare(token, firstToken);
			if (firstWord) {
				appendIfNotLf(token.getStart(), LF);
			} else {
				if ("+".equals(word)) {
					appendIfNotLf(token.getStart(), LF);
				} else if (",".equals(word) || ";".equals(word)) {
					// 何も入れない
				} else {
					appendIfNotLf(token.getStart(), " ");
				}
			}
			append(token.getStart(), word);
		}

		@Override
		protected void formatDescription(DescriptionToken token,
				boolean firstToken) {
			prepare(token, firstToken);
			super.formatDescription(token, firstToken);
		}

		@Override
		protected void formatAnnotation(AnnotationToken token,
				boolean firstToken) {
			prepare(token, firstToken);
			super.formatAnnotation(token, firstToken);
		}

		private void prepare(DMDLToken token, boolean firstToken) {
			if (firstToken) {
				ModelToken model = (ModelToken) token.getParent();
				int n = countLf(model.getStart(), token.getStart());
				switch (n) {
				case 0:
					if (prevText != null) {
						appendIfNotLf(token.getStart(), LF);
					}
					break;
				default:
					appendIfNotLf(token.getStart(), LF);
					append(token.getStart(), LF);
					break;
				}
			} else if (prevText instanceof CommentToken) {
				shrinkLf(prevText.getEnd(), token.getStart(), token);
			}
		}
	};

	protected int shrinkLf(int start, int end, DMDLToken token) {
		int n = countLf(start, end);
		switch (n) {
		case 0:
			break;
		case 1:
			append(token.getStart(), LF);
			break;
		default:
			append(token.getStart(), LF + LF);
			break;
		}
		return n;
	}

	protected int countLf(int start, int end) {
		String prepare;
		try {
			prepare = document.get(start, end - start);
		} catch (BadLocationException e) {
			return 0;
		}
		int n = 0;
		for (int i = 0; i < prepare.length(); i++) {
			char c = prepare.charAt(i);
			if (c == '\n') {
				n++;
			}
		}
		return n;
	}

	protected TokenFormatter<ArgumentsToken> argumentsFormatter = new TokenFormatter<ArgumentsToken>() {
		@Override
		protected void formatWord(WordToken token, boolean firstToken,
				String word, boolean firstWord) {
			if ("(".equals(word) || ",".equals(word)) {
				// 何も入れない
			} else if (")".equals(word)) {
				ArgumentsToken parent = (ArgumentsToken) token.getParent();
				if (parent.getBody().size() > 3) {
					appendIfNotLf(token.getEnd(), LF);
					if (inBlock(parent.getParent())) {
						append(token.getStart(), INDENT_PROPERTY);
					}
				}
			} else {
				appendIfNotLf(token.getStart(), " ");
			}
			append(token.getStart(), word);
		}
	};

	protected TokenFormatter<ArgumentToken> argumentFormatter = new TokenFormatter<ArgumentToken>() {
		@Override
		protected void formatWord(WordToken token, boolean firstToken,
				String word, boolean firstWord) {
			if (firstWord) {
				ArgumentToken parent = (ArgumentToken) token.getParent();
				ArgumentsToken args = (ArgumentsToken) parent.getParent();
				if (args.getBody().size() > 3) {
					appendIfNotLf(token.getStart(), LF);
					String indent = inBlock(args.getParent()) ? INDENT_PROPERTY
							+ INDENT_ARGUMENT : INDENT_ARGUMENT;
					append(token.getStart(), indent);
				}
			} else {
				appendIfNotLf(token.getStart(), " ");
			}
			append(token.getStart(), word);
		}

		@Override
		protected void formatDescription(DescriptionToken token,
				boolean firstToken) {
			appendIfNotLf(token.getStart(), " ");
			append(token.getStart(), token.getBody());
		}
	};

	private boolean inBlock(DMDLToken token) {
		while (token != null) {
			if (token instanceof BlockToken) {
				return true;
			}
			token = token.getParent();
		}
		return false;
	}

	protected TokenFormatter<BlockToken> blockFormatter = new TokenFormatter<BlockToken>() {
		@Override
		protected void formatWord(WordToken token, boolean firstToken,
				String word, boolean firstWord) {
			if ("}".equals(word)) {
				appendIfNotLf(token.getStart(), LF);
			} else {
				appendIfNotLf(token.getStart(), " ");
			}
			append(token.getStart(), word);
		}
	};
	protected TokenFormatter<PropertyToken> propertyFormatter = new TokenFormatter<PropertyToken>() {
		@Override
		protected void formatWord(WordToken token, boolean firstToken,
				String word, boolean firstWord) {
			if (firstWord) {
				appendIfNotLf(token.getStart(), LF);
				append(token.getStart(), INDENT_PROPERTY);
			} else if (";".equals(word)) {
				// 何も入れない
			} else {
				append(token.getStart(), " ");
			}
			append(token.getStart(), word);
		}

		@Override
		protected void formatAnnotation(AnnotationToken token,
				boolean firstToken) {
			appendIfNotLf(token.getStart(), LF);
			if (firstToken) {
				append(token.getStart(), LF);
			}
			append(token.getStart(), INDENT_PROPERTY);
			append(token.getStart(), token.getBody());
		}

		@Override
		protected void formatDescription(DescriptionToken token,
				boolean firstToken) {
			appendIfNotLf(token.getStart(), LF);
			if (firstToken) {
				append(token.getStart(), LF);
			}
			append(token.getStart(), INDENT_PROPERTY);
			append(token.getStart(), token.getBody());
		}
	};

	protected TokenFormatter<ArrayToken> arrayFormatter = new TokenFormatter<ArrayToken>() {
		@Override
		protected void formatWord(WordToken token, boolean firstToken,
				String word, boolean firstWord) {
			if (",".equals(word)) {
				// 何も入れない
			} else {
				append(token.getStart(), " ");
			}
			append(token.getStart(), word);
		}
	};

	protected void appendIfNotLf(int offset, String s) {
		if (sb.length() == 0) {
			return;
		} else {
			char c = sb.charAt(sb.length() - 1);
			if (c == '\n' || c == '\r') {
				return;
			}
		}
		append(offset, s);
	}

	protected void append(int offset, String s) {
		if (offset < start || end <= offset) {
			return;
		}
		if (changeStart < 0) {
			changeStart = offset;
		}
		sb.append(s);
	}

	@Override
	public IFormattingStrategy getFormattingStrategy(String contentType) {
		return null; // 使用しない
	}
}
