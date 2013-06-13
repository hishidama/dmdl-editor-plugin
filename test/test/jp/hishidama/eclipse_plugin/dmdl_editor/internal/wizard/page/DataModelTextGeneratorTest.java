package jp.hishidama.eclipse_plugin.dmdl_editor.internal.wizard.page;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DataModelTextGeneratorTest {

	@Test
	public void normal1() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelName("n");
		gen.appendProperty("p1", null, "INT");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("n = {");
		e.add("    p1 : INT;");
		e.add("};");
		assertEqualsList(e, r);
	}

	@Test
	public void normal2() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelName("n");
		gen.appendProperty("p1", null, "INT");
		gen.appendProperty("p2", null, "TEXT");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("n = {");
		e.add("    p1 : INT;");
		e.add("    p2 : TEXT;");
		e.add("};");
		assertEqualsList(e, r);
	}

	@Test
	public void normal_desc() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelName("n");
		gen.setModelDescription("あいう");
		gen.appendProperty("p1", "ぴー1", "INT");
		gen.appendProperty("p2", "ぴー2", "TEXT");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("\"あいう\"");
		e.add("n = {");
		e.add("");
		e.add("    \"ぴー1\"");
		e.add("    p1 : INT;");
		e.add("");
		e.add("    \"ぴー2\"");
		e.add("    p2 : TEXT;");
		e.add("};");
		assertEqualsList(e, r);
	}

	@Test
	public void normal_ref1() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelName("n");
		gen.appendRefProperty("r1");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("n = r1;");
		assertEqualsList(e, r);
	}

	@Test
	public void normal_ref2() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelName("n");
		gen.appendRefProperty("r1");
		gen.appendRefProperty("r2");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("n = r1");
		e.add("+ r2;");
		assertEqualsList(e, r);
	}

	@Test
	public void normal_ref3_1() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelName("n");
		gen.appendProperty("p1", null, "INT");
		gen.appendRefProperty("r1");
		gen.appendRefProperty("r2");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("n = {");
		e.add("    p1 : INT;");
		e.add("}");
		e.add("+ r1");
		e.add("+ r2;");
		assertEqualsList(e, r);
	}

	@Test
	public void normal_ref3_2() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelName("n");
		gen.appendRefProperty("r1");
		gen.appendProperty("p1", null, "INT");
		gen.appendRefProperty("r2");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("n = r1");
		e.add("+ {");
		e.add("    p1 : INT;");
		e.add("}");
		e.add("+ r2;");
		assertEqualsList(e, r);
	}

	@Test
	public void normal_ref3_3() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelName("n");
		gen.appendRefProperty("r1");
		gen.appendRefProperty("r2");
		gen.appendProperty("p1", null, "INT");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("n = r1");
		e.add("+ r2");
		e.add("+ {");
		e.add("    p1 : INT;");
		e.add("};");
		assertEqualsList(e, r);
	}

	@Test
	public void sum1() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("summarized");
		gen.setModelName("s");
		gen.appendSumProperty("p1", null, "any", "f", "s1");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("summarized s = f => {");
		e.add("    any s1 -> p1;");
		e.add("};");
		assertEqualsList(e, r);
	}

	@Test
	public void sum2() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("summarized");
		gen.setModelName("s");
		gen.appendSumProperty("p1", null, "any", "f", "s1");
		gen.appendSumProperty("p2", null, "sum", "f", "s2");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("summarized s = f => {");
		e.add("    any s1 -> p1;");
		e.add("    sum s2 -> p2;");
		e.add("};");
		assertEqualsList(e, r);
	}

	@Test
	public void sum_key1() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("summarized");
		gen.setModelName("s");
		gen.appendSumProperty("p1", null, "any", "f", "s1");
		gen.appendKey("p1", null);
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("summarized s = f => {");
		e.add("    any s1 -> p1;");
		e.add("} % p1;");
		assertEqualsList(e, r);
	}

	@Test
	public void sum_key2() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("summarized");
		gen.setModelName("s");
		gen.appendSumProperty("p1", null, "any", "f", "s1");
		gen.appendKey("p1", null);
		gen.appendSumProperty("p2", null, "sum", "f", "s2");
		gen.appendKey("p2", null);
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("summarized s = f => {");
		e.add("    any s1 -> p1;");
		e.add("    sum s2 -> p2;");
		e.add("} % p1, p2;");
		assertEqualsList(e, r);
	}

	@Test
	public void sum_desc() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("summarized");
		gen.setModelName("s");
		gen.appendSumProperty("p1", "きー", "any", "f", "s1");
		gen.appendKey(null, "p1");
		gen.appendSumProperty("p2", "合計", "sum", "f", "s2");
		gen.appendKey("p2", "s2");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("summarized s = f => {");
		e.add("");
		e.add("    \"きー\"");
		e.add("    any s1 -> p1;");
		e.add("");
		e.add("    \"合計\"");
		e.add("    sum s2 -> p2;");
		e.add("} % p1, p2;");
		assertEqualsList(e, r);
	}

	@Test
	public void join1() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("joined");
		gen.setModelName("j");
		gen.appendRefProperty("p1", null, "f", "s1");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("joined j = f -> {");
		e.add("    s1 -> p1;");
		e.add("};");
		assertEqualsList(e, r);
	}

	@Test
	public void join2() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("joined");
		gen.setModelName("j");
		gen.appendRefProperty("p1", null, "f", "s1");
		gen.appendRefProperty("p2", null, "f", "s2");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("joined j = f -> {");
		e.add("    s1 -> p1;");
		e.add("    s2 -> p2;");
		e.add("};");
		assertEqualsList(e, r);
	}

	@Test
	public void join3() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("joined");
		gen.setModelName("j");
		gen.appendRefProperty("p1", null, "f", "s1");
		gen.appendRefProperty("p2", null, "f", "s2");
		gen.appendRefProperty("p3", null, "f2", "s3");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("joined j = f -> {");
		e.add("    s1 -> p1;");
		e.add("    s2 -> p2;");
		e.add("}");
		e.add("+ f2 -> {");
		e.add("    s3 -> p3;");
		e.add("};");
		assertEqualsList(e, r);
	}

	@Test
	public void join4() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("joined");
		gen.setModelName("j");
		gen.appendRefProperty("p1", null, "f", "s1");
		gen.appendRefProperty("p2", null, "f", "s2");
		gen.appendRefProperty("p3", null, "f2", "s3");
		gen.appendRefProperty("p4", null, "f2", "s4");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("joined j = f -> {");
		e.add("    s1 -> p1;");
		e.add("    s2 -> p2;");
		e.add("}");
		e.add("+ f2 -> {");
		e.add("    s3 -> p3;");
		e.add("    s4 -> p4;");
		e.add("};");
		assertEqualsList(e, r);
	}

	@Test
	public void join_key1() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("joined");
		gen.setModelName("j");
		gen.appendRefProperty("p1", null, "f", "s1");
		gen.appendKey("p1", null);
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("joined j = f -> {");
		e.add("    s1 -> p1;");
		e.add("} % p1;");
		assertEqualsList(e, r);
	}

	@Test
	public void join_key2() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("joined");
		gen.setModelName("j");
		gen.appendRefProperty("p1", null, "f", "s1");
		gen.appendKey("p1", null);
		gen.appendRefProperty("p2", null, "f", "s2");
		gen.appendKey("p2", null);
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("joined j = f -> {");
		e.add("    s1 -> p1;");
		e.add("    s2 -> p2;");
		e.add("} % p1, p2;");
		assertEqualsList(e, r);
	}

	@Test
	public void join_key3() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("joined");
		gen.setModelName("j");
		gen.appendRefProperty("p1", null, "f", "s1");
		gen.appendKey("p1", null);
		gen.appendRefProperty("p2", null, "f", "s2");
		gen.appendRefProperty("p3", null, "f2", "s3");
		gen.appendKey("p3", null);
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("joined j = f -> {");
		e.add("    s1 -> p1;");
		e.add("    s2 -> p2;");
		e.add("} % p1");
		e.add("+ f2 -> {");
		e.add("    s3 -> p3;");
		e.add("} % p3;");
		assertEqualsList(e, r);
	}

	@Test
	public void join_key4() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("joined");
		gen.setModelName("j");
		gen.appendRefProperty("p1", null, "f", "s1");
		gen.appendKey("p1", null);
		gen.appendRefProperty("p2", null, "f", "s2");
		gen.appendKey("p2", null);
		gen.appendRefProperty("p3", null, "f2", "s3");
		gen.appendKey("p3", null);
		gen.appendRefProperty("p4", null, "f2", "s4");
		gen.appendKey("p4", null);
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("joined j = f -> {");
		e.add("    s1 -> p1;");
		e.add("    s2 -> p2;");
		e.add("} % p1, p2");
		e.add("+ f2 -> {");
		e.add("    s3 -> p3;");
		e.add("    s4 -> p4;");
		e.add("} % p3, p4;");
		assertEqualsList(e, r);
	}

	@Test
	public void join_ref1() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("joined");
		gen.setModelName("j");
		gen.appendRefProperty("f1");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("joined j = f1;");
		assertEqualsList(e, r);
	}

	@Test
	public void join_ref2() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("joined");
		gen.setModelName("j");
		gen.appendRefProperty("f1");
		gen.appendRefProperty("f2");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("joined j = f1");
		e.add("+ f2;");
		assertEqualsList(e, r);
	}

	@Test
	public void join_ref_key1() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("joined");
		gen.setModelName("j");
		gen.appendRefProperty("f1");
		gen.appendKey(null, "s1");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("joined j = f1 % s1;");
		assertEqualsList(e, r);
	}

	@Test
	public void join_ref_key1_2() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("joined");
		gen.setModelName("j");
		gen.appendRefProperty("f1");
		gen.appendKey(null, "s1");
		gen.appendRefProperty("f1");
		gen.appendKey(null, "s2");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("joined j = f1 % s1, s2;");
		assertEqualsList(e, r);
	}

	@Test
	public void join_ref_key2() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("joined");
		gen.setModelName("j");
		gen.appendRefProperty("f1");
		gen.appendKey(null, "s1");
		gen.appendRefProperty("f2");
		gen.appendKey(null, "s2");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("joined j = f1 % s1");
		e.add("+ f2 % s2;");
		assertEqualsList(e, r);
	}

	@Test
	public void join_ref_key2_2() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("joined");
		gen.setModelName("j");
		gen.appendRefProperty("f1");
		gen.appendKey(null, "s1");
		gen.appendRefProperty("f1");
		gen.appendKey(null, "s2");
		gen.appendRefProperty("f2");
		gen.appendKey(null, "s3");
		gen.appendRefProperty("f2");
		gen.appendKey(null, "s4");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("joined j = f1 % s1, s2");
		e.add("+ f2 % s3, s4;");
		assertEqualsList(e, r);
	}

	@Test
	public void projective() {
		DataModelTextGenerator gen = new DataModelTextGenerator();
		gen.setModelType("projective");
		gen.setModelName("n");
		gen.appendProperty("p1", null, "INT");
		String r = gen.getText();
		List<String> e = new ArrayList<String>();
		e.add("projective n = {");
		e.add("    p1 : INT;");
		e.add("};");
		assertEqualsList(e, r);
	}

	private void assertEqualsList(List<String> expected, String actual) {
		StringBuilder sb = new StringBuilder(1024);
		for (String s : expected) {
			sb.append(s);
			sb.append("\n");
		}
		assertEquals(sb.toString(), actual);
	}
}
