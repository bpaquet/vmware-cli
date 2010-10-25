package com.octo.vmware;

import org.junit.Assert;
import org.junit.Test;


public class CommandSplitterTest {

	@Test
	public void testSimple() {
		String [] result = {"a", "b", "c"};
		Assert.assertArrayEquals(result, CommandSplitter.split("a b c"));
	}
	
	@Test
	public void testComplex() {
		String [] result = {"a", "b c", "d", "e", "f"};
		Assert.assertArrayEquals(result, CommandSplitter.split("a \"b c\" d e f"));
	}
}
