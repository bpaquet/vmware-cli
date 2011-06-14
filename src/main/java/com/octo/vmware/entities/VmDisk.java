package com.octo.vmware.entities;

public class VmDisk {
	
	private String fileName;
	
	private long sizeKb;
	
	private int controllerKey;
	
	private int unitNumber;
	
	private int key;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getControllerKey() {
		return controllerKey;
	}

	public void setControllerKey(int controllerKey) {
		this.controllerKey = controllerKey;
	}

	public int getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(int unitNumber) {
		this.unitNumber = unitNumber;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public long getSizeKb() {
		return sizeKb;
	}

	public void setSizeKb(long sizeKb) {
		this.sizeKb = sizeKb;
	}

}
