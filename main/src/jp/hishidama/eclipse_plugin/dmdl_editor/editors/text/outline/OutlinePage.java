package jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.outline;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.DMDLTextEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.DMDLToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelList;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.PropertyToken;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public class OutlinePage extends ContentOutlinePage {
	protected RootData root = new RootData();
	protected DMDLTextEditor editor;

	public OutlinePage(DMDLTextEditor editor) {
		this.editor = editor;
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);

		TreeViewer viewer = getTreeViewer();
		viewer.setContentProvider(new DMDLContentProvider());
		viewer.setLabelProvider(new DMDLLabelProvider(editor));
		viewer.addSelectionChangedListener(new OutlineSelectionChangedListener(
				editor));
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				if (selection.isEmpty()) {
					return;
				}
				List<?> list = selection.toList();
				TreeViewer viewer = (TreeViewer) event.getSource();
				for (Object element : list) {
					boolean old = viewer.getExpandedState(element);
					viewer.setExpandedState(element, !old);
				}
			}
		});
		viewer.setInput(root);

		refresh();
	}

	private void refresh() {
		DMDLDocument document = editor.getDocument();
		ModelList models = document.getModelList();
		refresh(models);
	}

	public void refresh(ModelList models) {
		root.element = models;
		getTreeViewer().refresh();
	}

	public void selectToken(DMDLToken token) {
		TreeViewer viewer = getTreeViewer();
		if (token instanceof PropertyToken) {
			for (DMDLToken t = token.getParent(); t != null; t = t.getParent()) {
				if (t instanceof ModelToken) {
					viewer.expandToLevel(t, TreeViewer.ALL_LEVELS);
					break;
				}
			}
		}
		StructuredSelection selection = new StructuredSelection(token);
		viewer.setSelection(selection, true);
	}
}
