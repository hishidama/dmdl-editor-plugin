package jp.hishidama.eclipse_plugin.dmdl_editor.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class DataModelUtilTest {

	@Test
	public void testDecodeDescription() {
		assertNull(DataModelUtil.decodeDescription(null));
		assertEquals("", DataModelUtil.decodeDescription(""));
	}

	@Test
	public void testEncodeDescription() {
		assertNull(DataModelUtil.encodeDescription(null));
		assertEquals("\"\"", DataModelUtil.encodeDescription(""));
	}

	@Test
	public void testDescription() {
		assertEqualsDescriptoin("\"\"", "");
		assertEqualsDescriptoin("\"あいうえお\"", "あいうえお");
		assertEqualsDescriptoin("\"あい\\\"う\\\"えお\"", "あい\"う\"えお");
	}

	private void assertEqualsDescriptoin(String ex, String s) {
		assertEquals(ex, DataModelUtil.encodeDescription(s));
		assertEquals(s, DataModelUtil.decodeDescription(ex));
	}
}
