package com.octo.vmware.entities;

public class VmLocation {

	private String esxName;
	
	private String vmName;

	public VmLocation(String s) {
		String [] splitted = s.split(":");
		if (splitted.length != 2) {
			throw new RuntimeException("Wrong virtual machine description : " + s);
		}
		esxName = splitted[0];
		vmName = splitted[1];
	}
	
	public String getEsxName() {
		return esxName;
	}

	public String getVmName() {
		return vmName;
	}
	
}
