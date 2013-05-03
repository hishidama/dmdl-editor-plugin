package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.update;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.hishidama.eclipse_plugin.dmdl_editor.internal.parser.token.ModelToken;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.DMDLFileUtil;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.util.DMDLFileUtil.DocumentManager;
import jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page.SelectDataModelPage.ModelFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

public abstract class AttributeUpdater<R extends UpdateRegion<R>> {

	public abstract void setAttribute(String modelAttr, String propAttr);

	public boolean execute(List<ModelFile> list) throws IOException {
		for (ModelFile mf : list) {
			IFile file = mf.file;
			ModelToken model = mf.model;

			DocumentManager dm = DMDLFileUtil.getDocument(file);
			try {
				IDocument doc = dm.getDocument();
				execute(file, doc, model);
			} finally {
				dm.close();
			}
		}
		return executeFinish();
	}

	protected abstract void execute(IFile file, IDocument doc, ModelToken model);

	protected static int getLineTop(IDocument doc, int start) {
		try {
			for (int i = start - 1; i >= 0; i--) {
				char c = doc.getChar(i);
				switch (c) {
				case ' ':
				case '\t':
					continue;
				default:
					return i + 1;
				}
			}
			return 0;
		} catch (BadLocationException e) {
			return start;
		}
	}

	private Map<IPath, List<R>> regionMap = new LinkedHashMap<IPath, List<R>>();

	protected final void addRegion(IFile file, R region) {
		IPath path = file.getFullPath();
		List<R> list = regionMap.get(path);
		if (list == null) {
			list = new ArrayList<R>();
			regionMap.put(path, list);
		}
		list.add(region);
	}

	private boolean executeFinish() throws IOException {
		for (Entry<IPath, List<R>> entry : regionMap.entrySet()) {
			IPath path = entry.getKey();
			DocumentManager dm = DMDLFileUtil.getDocument(path);
			try {
				IDocument doc = dm.getDocument();
				List<R> list = entry.getValue();
				Collections.sort(list);
				for (R region : list) {
					executeFinish(doc, region);
				}
				dm.commit();
			} finally {
				dm.close();
			}
		}
		return true;
	}

	protected abstract void executeFinish(IDocument doc, R region);
}
