package jp.hishidama.eclipse_plugin.dmdl_editor.editors.text.style;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;

public class PartitionDamagerRepairer extends DefaultDamagerRepairer {

	public PartitionDamagerRepairer(ITokenScanner scanner) {
		super(scanner);
	}

	@Override
	public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent event,
			boolean documentPartitioningChanged) {
		if (!documentPartitioningChanged) {
			return partition;
		}

		return super.getDamageRegion(partition, event,
				documentPartitioningChanged);
	}
}
