package jp.hishidama.eclipse_plugin.dmdl_editor.internal.jdt.hyperlink;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.hyperlink.DMDLHyperlink;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index.IndexContainer;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index.ModelIndex;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.index.PropertyIndex;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

// @see org.eclipse.jdt.internal.ui.javaeditor.JavaElementHyperlinkDetector
public class OpenDeclaredDmdlHyperlinkDetector extends
		AbstractHyperlinkDetector {

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		ITextEditor editor = (ITextEditor) getAdapter(ITextEditor.class);
		if (editor == null) {
			return null;
		}
		int offset = region.getOffset();
		return detectHyperlinks(editor, offset);
	}

	public IHyperlink[] detectHyperlinks(ITextEditor editor, int offset) {
		IEditorInput input = editor.getEditorInput();
		IJavaElement element = (IJavaElement) input
				.getAdapter(IJavaElement.class);

		IDocument document = editor.getDocumentProvider().getDocument(input);
		IRegion word = findWord(document, offset);
		if (word == null || word.getLength() == 0) {
			return null;
		}
		IJavaElement[] codes;
		try {
			ITypeRoot root = (ITypeRoot) element.getAdapter(ITypeRoot.class);
			codes = root.codeSelect(word.getOffset(), word.getLength());
		} catch (JavaModelException e) {
			return null;
		}
		for (IJavaElement code : codes) {
			int elementType = code.getElementType();
			switch (elementType) {
			case IJavaElement.TYPE:
				IHyperlink[] tr = detectModelHyperlinks((IType) code, word);
				if (tr != null) {
					return tr;
				}
				break;
			case IJavaElement.FIELD:
				String fieldName = code.getElementName();
				String fieldSnakeName = fieldName.substring(0, 1).toUpperCase()
						+ fieldName.substring(1);
				IHyperlink[] fr = detectPropertyHyperlinks(code,
						fieldSnakeName, word);
				if (fr != null) {
					return fr;
				}
				break;
			case IJavaElement.METHOD:
				String methodSnakeName = cutMethodName(code.getElementName());
				IHyperlink[] mr = detectPropertyHyperlinks(code,
						methodSnakeName, word);
				if (mr != null) {
					return mr;
				}
				break;
			}
		}
		return null;
	}

	private static IRegion findWord(IDocument document, int offset) {
		int start = -2;
		int end = -1;
		try {
			int pos;
			for (pos = offset; pos >= 0; pos--) {
				char c = document.getChar(pos);
				if (!Character.isJavaIdentifierPart(c)) {
					break;
				}
			}

			start = pos;
			pos = offset;
			for (int length = document.getLength(); pos < length; pos++) {
				char c = document.getChar(pos);
				if (!Character.isJavaIdentifierPart(c)) {
					break;
				}
			}

			end = pos;
		} catch (BadLocationException e) {
		}
		if (start >= -1 && end > -1) {
			if (start == offset && end == offset) {
				return new Region(offset, 0);
			}
			if (start == offset) {
				return new Region(start, end - start);
			} else {
				return new Region(start + 1, end - start - 1);
			}
		} else {
			return null;
		}
	}

	private static String cutMethodName(String name) {
		if (name.startsWith("set")) {
			name = name.substring(3);
		} else if (name.startsWith("get")) {
			name = name.substring(3);
		}
		if (name.endsWith("Option")) {
			name = name.substring(0, name.length() - 6);
		} else if (name.endsWith("AsString")) {
			name = name.substring(0, name.length() - 8);
		}
		return name;
	}

	private IHyperlink[] detectModelHyperlinks(IType type, IRegion word) {
		ModelIndex model = findModel(type);
		if (model == null) {
			return null;
		}
		IProject project = type.getJavaProject().getProject();
		return new IHyperlink[] { new DMDLHyperlink(project, model.getToken(),
				word) };
	}

	private IHyperlink[] detectPropertyHyperlinks(IJavaElement code,
			String name, IRegion word) {
		IType type = (IType) code.getParent();
		ModelIndex model = findModel(type);
		if (model == null) {
			return null;
		}
		PropertyIndex property = model.getPropertySnake(name);
		if (property == null) {
			return null;
		}
		IProject project = code.getJavaProject().getProject();
		return new IHyperlink[] { new DMDLHyperlink(project,
				property.getToken(), word) };
	}

	private static ModelIndex findModel(IType type) {
		IProject project = type.getJavaProject().getProject();
		IndexContainer ic = IndexContainer.getContainer(project);
		if (ic == null) {
			return null;
		}
		return ic.findModelSnake(type.getElementName());
	}
}
