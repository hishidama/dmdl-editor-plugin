package jp.hishidama.eclipse_plugin.dmdl_editor.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.AnnotationToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ArgumentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ArgumentsToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.BlockToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.CommentToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DescriptionToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.WordToken;

public class DMDLSimpleParser {

	protected static Set<Character> SYMBOL = new HashSet<Character>();
	static {
		String s = " \t\r\n@,+&%:;-=>{}()";
		for (int i = 0; i < s.length(); i++) {
			SYMBOL.add(s.charAt(i));
		}
	}

	public ModelList parse(DMDLSimpleScanner scanner) {
		List<DMDLToken> list = new ArrayList<DMDLToken>();
		parse(list, scanner);
		return new ModelList(0, scanner.getLength(), list);
	}

	protected void parse(List<DMDLToken> topList, DMDLSimpleScanner scanner) {
		int modelStart = scanner.getOffset();
		List<DMDLToken> list = new ArrayList<DMDLToken>();
		for (;;) {
			int start = scanner.getOffset();
			char c = scanner.read();
			switch (c) {
			case ' ':
			case '\t':
			case '\r':
			case '\n':
				break;
			case DMDLSimpleScanner.EOF:
				if (!list.isEmpty()) {
					acceptDataModel(topList, scanner, modelStart,
							scanner.getOffset(), list);
				}
				return;
			case ';':
				acceptWord(list, scanner, start, start + 1);
				acceptDataModel(topList, scanner, modelStart,
						scanner.getOffset(), list);
				modelStart = scanner.getOffset();
				list = new ArrayList<DMDLToken>();
				break;
			case '{':
				parseBlock(list, scanner, start);
				break;
			case '@':
				parseAnnotation(list, scanner, start);
				break;
			case '(':
				parseArgs(list, scanner, start);
				break;
			case '/': {
				char d = scanner.read();
				scanner.unread();
				switch (d) {
				case '/':
					parseLineComment(list, scanner, start, true);
					break;
				case '*':
					parseBlockComment(list, scanner, start, true);
					break;
				default:
					parseWord(list, scanner, start);
					break;
				}
				break;
			}
			case '-': {
				char d = scanner.read();
				scanner.unread();
				switch (d) {
				case '-':
					parseLineComment(list, scanner, start, true);
					break;
				default:
					parseWord(list, scanner, start);
					break;
				}
				break;
			}
			case '\"':
				parseDescription(list, scanner, start);
				break;
			default:
				parseWord(list, scanner, start);
				break;
			}
		}
	}

	protected void acceptDataModel(List<DMDLToken> list,
			DMDLSimpleScanner scanner, int start, int end,
			List<DMDLToken> partList) {
		list.add(new ModelToken(start, end, partList));
	}

	protected void parseBlock(List<DMDLToken> topList,
			DMDLSimpleScanner scanner, int blockStart) {
		List<DMDLToken> bodyList = new ArrayList<DMDLToken>();
		acceptWord(bodyList, scanner, blockStart, blockStart + 1);

		List<DMDLToken> list = new ArrayList<DMDLToken>();
		int propertyStart = scanner.getOffset();
		for (;;) {
			int start = scanner.getOffset();
			char c = scanner.read();
			switch (c) {
			case ' ':
			case '\t':
			case '\r':
			case '\n':
				break;
			case ';':
				acceptWord(list, scanner, start, start + 1);
				acceptProperty(bodyList, scanner, propertyStart,
						scanner.getOffset(), list);
				list = new ArrayList<DMDLToken>();
				propertyStart = scanner.getOffset();
				break;
			case DMDLSimpleScanner.EOF:
				if (!list.isEmpty()) {
					acceptProperty(bodyList, scanner, propertyStart,
							scanner.getOffset(), list);
				}
				acceptModelBlock(topList, scanner, blockStart,
						scanner.getOffset(), bodyList);
				return;
			case '}': {
				if (!list.isEmpty()) {
					acceptProperty(bodyList, scanner, propertyStart,
							scanner.getOffset(), list);
				}
				acceptWord(bodyList, scanner, start, start + 1);
				acceptModelBlock(topList, scanner, blockStart,
						scanner.getOffset(), bodyList);
				return;
			}
			case '@':
				parseAnnotation(list, scanner, start);
				break;
			case '(':
				parseArgs(list, scanner, start);
				break;
			case '/': {
				char d = scanner.read();
				scanner.unread();
				switch (d) {
				case '/':
					parseLineComment(list, scanner, start, false);
					break;
				case '*':
					parseBlockComment(list, scanner, start, false);
					break;
				default:
					parseWord(list, scanner, start);
					break;
				}
				break;
			}
			case '-': {
				char d = scanner.read();
				scanner.unread();
				switch (d) {
				case '-':
					parseLineComment(list, scanner, start, true);
					break;
				default:
					parseWord(list, scanner, start);
					break;
				}
				break;
			}
			case '\"':
				parseDescription(list, scanner, start);
				break;
			default:
				parseWord(list, scanner, start);
				break;
			}
		}
	}

	protected void acceptProperty(List<DMDLToken> list,
			DMDLSimpleScanner scanner, int start, int end,
			List<DMDLToken> bodyList) {
		list.add(new PropertyToken(start, end, bodyList));
	}

	protected void acceptModelBlock(List<DMDLToken> list,
			DMDLSimpleScanner scanner, int start, int end,
			List<DMDLToken> bodyList) {
		list.add(new BlockToken(start, end, bodyList));
	}

	protected void parseBlockComment(List<DMDLToken> list,
			DMDLSimpleScanner scanner, int blockStart, boolean top) {
		for (;;) {
			char c = scanner.read();
			switch (c) {
			case DMDLSimpleScanner.EOF:
				acceptComment(list, scanner, blockStart, scanner.getOffset(),
						true, top);
				return;
			case '*':
				char d = scanner.read();
				if (d == '/') {
					acceptComment(list, scanner, blockStart,
							scanner.getOffset(), true, top);
					return;
				} else {
					scanner.unread();
				}
				break;
			default:
				break;
			}
		}
	}

	protected void parseLineComment(List<DMDLToken> list,
			DMDLSimpleScanner scanner, int commentStart, boolean top) {
		for (;;) {
			char c = scanner.read();
			switch (c) {
			case '\r':
				char d = scanner.read();
				if (d != '\n') {
					scanner.unread();
				}
				acceptComment(list, scanner, commentStart, scanner.getOffset(),
						false, top);
				return;
			case '\n':
			case DMDLSimpleScanner.EOF:
				acceptComment(list, scanner, commentStart, scanner.getOffset(),
						false, top);
				return;
			default:
				break;
			}
		}
	}

	protected void acceptComment(List<DMDLToken> list,
			DMDLSimpleScanner scanner, int start, int end, boolean block,
			boolean top) {
		String s = scanner.getString(start, end);
		list.add(new CommentToken(start, end, s, block));
	}

	protected void parseDescription(List<DMDLToken> list,
			DMDLSimpleScanner scanner, int descStart) {
		for (;;) {
			char c = scanner.read();
			switch (c) {
			case '\r':
				char d = scanner.read();
				if (d != '\n') {
					scanner.unread();
				}
				acceptDescription(list, scanner, descStart, scanner.getOffset());
				return;
			case '\n':
			case DMDLSimpleScanner.EOF:
			case '\"':
				acceptDescription(list, scanner, descStart, scanner.getOffset());
				return;
			default:
				break;
			}
		}
	}

	protected void acceptDescription(List<DMDLToken> list,
			DMDLSimpleScanner scanner, int start, int end) {
		String s = scanner.getString(start, end);
		list.add(new DescriptionToken(start, end, s));
	}

	protected void parseAnnotation(List<DMDLToken> list,
			DMDLSimpleScanner scanner, int start) {
		for (;;) {
			char c = scanner.read();
			switch (c) {
			case DMDLSimpleScanner.EOF:
				acceptAnnotation(list, scanner, start, scanner.getOffset());
				return;
			case '.':
			case '_':
				break;
			default:
				if (SYMBOL.contains(c)) {
					scanner.unread();
					acceptAnnotation(list, scanner, start, scanner.getOffset());
					return;
				}
				break;
			}
		}
	}

	protected void acceptAnnotation(List<DMDLToken> list,
			DMDLSimpleScanner scanner, int start, int end) {
		String s = scanner.getString(start, end);
		list.add(new AnnotationToken(start, end, s));
	}

	protected void parseArgs(List<DMDLToken> list, DMDLSimpleScanner scanner,
			int blockStart) {
		List<DMDLToken> bodyList = new ArrayList<DMDLToken>();
		acceptWord(bodyList, scanner, blockStart, blockStart + 1);

		List<DMDLToken> argList = new ArrayList<DMDLToken>(3);
		int argStart = scanner.getOffset();
		for (;;) {
			int start = scanner.getOffset();
			char c = scanner.read();
			switch (c) {
			case ' ':
			case '\t':
			case '\r':
			case '\n':
				break;
			case ',':
				acceptArg(bodyList, scanner, argStart, scanner.getOffset(),
						argList);
				acceptWord(bodyList, scanner, start, start + 1);
				argList = new ArrayList<DMDLToken>(3);
				argStart = scanner.getOffset();
				break;
			case DMDLSimpleScanner.EOF:
				if (!argList.isEmpty()) {
					acceptArg(bodyList, scanner, argStart, scanner.getOffset(),
							argList);
				}
				acceptArgs(list, scanner, blockStart, scanner.getOffset(),
						bodyList);
				return;
			case ')':
				if (!argList.isEmpty()) {
					acceptArg(bodyList, scanner, argStart, scanner.getOffset(),
							argList);
				}
				acceptWord(bodyList, scanner, start, start + 1);
				acceptArgs(list, scanner, blockStart, scanner.getOffset(),
						bodyList);
				return;
			case '/': {
				char d = scanner.read();
				scanner.unread();
				switch (d) {
				case '/':
					parseLineComment(argList, scanner, start, false);
					break;
				case '*':
					parseBlockComment(argList, scanner, start, false);
					break;
				default:
					parseWord(argList, scanner, start);
					break;
				}
				break;
			}
			case '-': {
				char d = scanner.read();
				scanner.unread();
				switch (d) {
				case '-':
					parseLineComment(argList, scanner, start, true);
					break;
				default:
					parseWord(argList, scanner, start);
					break;
				}
				break;
			}
			case '\"':
				parseDescription(argList, scanner, start);
				break;
			default:
				parseWord(argList, scanner, start);
				break;
			}
		}
	}

	protected void acceptArgs(List<DMDLToken> list, DMDLSimpleScanner scanner,
			int start, int end, List<DMDLToken> bodyList) {
		list.add(new ArgumentsToken(start, end, bodyList));
	}

	protected void acceptArg(List<DMDLToken> list, DMDLSimpleScanner scanner,
			int start, int end, List<DMDLToken> bodyList) {
		list.add(new ArgumentToken(start, end, bodyList));
	}

	protected void parseWord(List<DMDLToken> list, DMDLSimpleScanner scanner,
			int start) {
		String s = scanner.getString(start, start + 1);
		char c = s.charAt(0);
		if (SYMBOL.contains(c)) {
			parseSymbol(list, scanner, start, c);
		} else {
			parseWord0(list, scanner, start);
		}
	}

	protected void parseWord0(List<DMDLToken> list, DMDLSimpleScanner scanner,
			int start) {
		for (;;) {
			char c = scanner.read();
			switch (c) {
			case DMDLSimpleScanner.EOF:
				acceptWord(list, scanner, start, scanner.getOffset());
				return;
			default:
				if (SYMBOL.contains(c)) {
					scanner.unread();
					acceptWord(list, scanner, start, scanner.getOffset());
					return;
				}
				break;
			}
		}
	}

	protected void parseSymbol(List<DMDLToken> list, DMDLSimpleScanner scanner,
			int start, char c) {
		switch (c) {
		case DMDLSimpleScanner.EOF:
			return;
		case '-':
		case '=':
			char d = scanner.read();
			if (d == '>') {
				acceptWord(list, scanner, start, scanner.getOffset());
				return;
			}
			scanner.unread();
			break;
		default:
			break;
		}

		acceptWord(list, scanner, start, scanner.getOffset());
	}

	protected void acceptWord(List<DMDLToken> list, DMDLSimpleScanner scanner,
			int start, int end) {
		String s = scanner.getString(start, end);
		list.add(new WordToken(start, end, s));
	}
}
