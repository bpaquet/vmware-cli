package com.octo.vmware;

import junit.framework.Assert;

import org.junit.Test;


public class SizeFormatterTest {
	
	@Test
	public void testB() {
		Assert.assertEquals("12 B", SizeFormatter.formatSize(12));
	}
	
	@Test
	public void testkB() {
		Assert.assertEquals("2,000 kB", SizeFormatter.formatSize(2048));
		Assert.assertEquals("2,012 kB", SizeFormatter.formatSize(2048 + 12));
	}
	
	@Test
	public void testGB() {
		Assert.assertEquals("12,000 MB", SizeFormatter.formatSize(2048*6*1024));
	}

}
