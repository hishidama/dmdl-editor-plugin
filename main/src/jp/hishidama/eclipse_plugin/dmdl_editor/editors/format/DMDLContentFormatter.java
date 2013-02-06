package jp.hishidama.eclipse_plugin.dmdl_editor.editors.format;

import jp.hishidama.eclipse_plugin.dmdl_editor.Activator;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.AnnotationToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ArgumentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ArgumentsToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.BlockToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLBodyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLTextToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DescriptionToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
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
	protected String LF = "\r\n";

	@Override
	public void format(IDocument document, IRegion region) {
		DMDLDocument doc = (DMDLDocument) document;
		this.document = doc;
		ModelList models = doc.getModelList();

		this.start = region.getOffset();
		int length = region.getLength();
		this.end = start + length;

		changeStart = -1;
		sb = new StringBuilder(length);
		defaultFormatter.format(models);

		String text = sb.toString();
		try {
			document.replace(start, length, text); // TODO start,length
		} catch (BadLocationException e) {
			ILog log = Activator.getDefault().getLog();
			log.log(new Status(Status.WARNING, Activator.PLUGIN_ID,
					"DMDLContentFormatter bad location.", e));
		}
	}

	protected TokenFormatter<DMDLBodyToken> defaultFormatter = new TokenFormatter<DMDLBodyToken>();

	protected class TokenFormatter<T extends DMDLBodyToken> {
		public void format(T body) {
			prepareFormat(body);
			boolean firstWord = true;
			for (DMDLToken token : body.getBody()) {
				if (token.getEnd() <= start) {
					if (token instanceof WordToken) {
						firstWord = false;
					}
					continue;
				}
				if (end <= token.getStart()) {
					break;
				}
				if (token instanceof WordToken) {
					WordToken word = (WordToken) token;
					formatWord(word, word.getBody(), firstWord, body);
					firstWord = false;
				} else if (token instanceof AnnotationToken) {
					formatAnnotation((AnnotationToken) token);
				} else if (token instanceof DescriptionToken) {
					formatDescription((DescriptionToken) token);
				} else if (token instanceof DMDLTextToken) {
					formatText((DMDLTextToken) token);
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
					} else {
						defaultFormatter.format(t);
					}
				}
			}
		}

		protected void prepareFormat(T parent) {
		}

		protected void formatWord(WordToken token, String word,
				boolean firstWord, T parent) {
			appendIfNotLf(token.getStart(), " ");
			append(token.getStart(), token.getBody());
		}

		protected void formatAnnotation(AnnotationToken token) {
			appendIfNotLf(token.getStart(), LF);
			append(token.getStart(), token.getBody());
		}

		protected void formatDescription(DescriptionToken token) {
			appendIfNotLf(token.getStart(), LF);
			append(token.getStart(), token.getBody());
		}

		protected void formatText(DMDLTextToken token) {
			appendIfNotLf(token.getStart(), " ");
			append(token.getStart(), token.getBody());
		}
	}

	protected TokenFormatter<ModelToken> modelFormatter = new TokenFormatter<ModelToken>() {
		@Override
		protected void prepareFormat(ModelToken model) {
			appendIfNotLf(model.getStart(), LF);
			append(model.getStart(), LF);
		}

		@Override
		protected void formatWord(WordToken token, String word,
				boolean firstWord, ModelToken parent) {
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
	};

	protected TokenFormatter<ArgumentsToken> argumentsFormatter = new TokenFormatter<ArgumentsToken>() {
		@Override
		protected void formatWord(WordToken token, String word,
				boolean firstWord, ArgumentsToken parent) {
			if ("(".equals(word) || ",".equals(word)) {
				// 何も入れない
			} else if (")".equals(word)) {
				if (parent.getBody().size() > 3) {
					appendIfNotLf(token.getEnd(), LF);
				}
			} else {
				appendIfNotLf(token.getStart(), " ");
			}
			append(token.getStart(), word);

			if ("(".equals(word)) {
				if (parent.getBody().size() > 3) {
					appendIfNotLf(token.getEnd(), LF);
				}
			} else if (")".equals(word) || ",".equals(word)) {
				append(token.getEnd(), LF);
			}
		}
	};

	protected TokenFormatter<ArgumentToken> argumentFormatter = new TokenFormatter<ArgumentToken>() {
		@Override
		protected void formatWord(WordToken token, String word,
				boolean firstWord, ArgumentToken parent) {
			if (firstWord) {
				ArgumentsToken args = (ArgumentsToken) parent.getParent();
				if (args.getBody().size() > 3) {
					appendIfNotLf(token.getStart(), LF);
					append(token.getStart(), "  ");
				}
			} else {
				appendIfNotLf(token.getStart(), " ");
			}
			append(token.getStart(), word);
		}

		@Override
		protected void formatDescription(DescriptionToken token) {
			appendIfNotLf(token.getStart(), " ");
			append(token.getStart(), token.getBody());
		}
	};

	protected TokenFormatter<BlockToken> blockFormatter = new TokenFormatter<BlockToken>() {
		@Override
		protected void formatWord(WordToken token, String word,
				boolean firstWord, BlockToken parent) {
			if ("}".equals(word)) {
				// 何も入れない
			} else {
				appendIfNotLf(token.getStart(), " ");
			}
			append(token.getStart(), word);

			if ("{".equals(word)) {
				append(token.getEnd(), LF);
			}
		}
	};
	protected TokenFormatter<PropertyToken> propertyFormatter = new TokenFormatter<PropertyToken>() {
		@Override
		protected void formatWord(WordToken token, String word,
				boolean firstWord, PropertyToken parent) {
			if (firstWord) {
				appendIfNotLf(token.getStart(), LF);
				append(token.getStart(), "    ");
			} else if (";".equals(word)) {
				// 何も入れない
			} else {
				append(token.getStart(), " ");
			}
			append(token.getStart(), word);

			if (";".equals(word)) {
				append(token.getEnd(), LF);
			}
		}

		@Override
		protected void formatAnnotation(AnnotationToken token) {
			appendIfNotLf(token.getStart(), LF);
			append(token.getStart(), "    ");
			append(token.getStart(), token.getBody());
		}

		@Override
		protected void formatDescription(DescriptionToken token) {
			appendIfNotLf(token.getStart(), LF);
			append(token.getStart(), LF);
			append(token.getStart(), "    ");
			append(token.getStart(), token.getBody());
		}
	};

	protected void appendIfNotLf(int offset, String s) {
		if (sb.length() > 0) {
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
