package jp.hishidama.eclipse_plugin.dmdl_editor.viewer;

import org.eclipse.gef.dnd.SimpleObjectTransfer;
import org.eclipse.swt.dnd.Transfer;

public class DMDLTreeDataTransfer extends SimpleObjectTransfer {

	private static final DMDLTreeDataTransfer INSTANCE = new DMDLTreeDataTransfer();
	private static final String TYPE_NAME = "DMDLTreeData transfer"//$NON-NLS-1$
			+ System.currentTimeMillis() + ":" + INSTANCE.hashCode();//$NON-NLS-1$
	private static final int TYPEID = registerType(TYPE_NAME);

	public static Transfer getInstance() {
		return INSTANCE;
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}
}
