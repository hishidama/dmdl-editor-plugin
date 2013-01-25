package jp.hishidama.eclipse_plugin.dmdl_editor.editors.outline;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.DMDLSimpleParser;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelList;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public class OutlinePage extends ContentOutlinePage {
	protected RootData root = new RootData();
	protected DMDLEditor editor;

	public OutlinePage(DMDLEditor editor) {
		this.editor = editor;
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);

		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new DMDLContentProvider());
		viewer.setLabelProvider(new DMDLLabelProvider());
		viewer.addSelectionChangedListener(new OutlineSelectionChangedListener(
				editor));
		viewer.setInput(root);

		refresh();
	}

	private void refresh() {
		IDocument document = editor.getDocumentProvider().getDocument(
				editor.getEditorInput());
		DMDLSimpleParser parser = new DMDLSimpleParser();
		ModelList models = parser.parse(document);
		refresh(models);
	}

	public void refresh(ModelList models) {
		root.element = models;
		getTreeViewer().refresh();
	}
}
