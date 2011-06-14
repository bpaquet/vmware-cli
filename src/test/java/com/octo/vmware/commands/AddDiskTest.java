package com.octo.vmware.commands;

import junit.framework.Assert;

import org.junit.Test;


public class AddDiskTest {

	@Test
	public void testComputeDiskUrl1() {
		Assert.assertEquals("[datastore1] toto/toto_2.vmdk", AddDisk.computeNewDiskUrl("[datastore1] toto/toto.vmdk", 2));
	}
	
	@Test
	public void testComputeDiskUrl2() {
			Assert.assertEquals("[datastore1] toto/toto_2.vmdk", AddDisk.computeNewDiskUrl("[datastore1] toto/toto_0.vmdk", 2));
	}
}
