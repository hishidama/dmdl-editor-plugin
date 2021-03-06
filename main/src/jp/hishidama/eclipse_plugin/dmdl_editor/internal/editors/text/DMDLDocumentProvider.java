package jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.editors.text.style.partition.DMDLPartitionScanner;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

public class DMDLDocumentProvider extends FileDocumentProvider {

	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner = new FastPartitioner(
					new DMDLPartitionScanner(),
					new String[] { DMDLPartitionScanner.DMDL_BLOCK });
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}

	@Override
	protected IDocument createEmptyDocument() {
		return new DMDLDocument();
	}
}
