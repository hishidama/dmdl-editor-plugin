package jp.hishidama.eclipse_plugin.dmdl_editor.editors.outline;

import java.util.List;

import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLDocument;
import jp.hishidama.eclipse_plugin.dmdl_editor.editors.DMDLEditor;
import jp.hishidama.eclipse_plugin.dmdl_editor.parser.token.ModelList;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
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
}