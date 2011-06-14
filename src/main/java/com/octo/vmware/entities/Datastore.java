package com.octo.vmware.entities;

import vim25.ManagedObjectReference;

public class Datastore {
	
	private String name;
	
	private long capcity;
	
	private long free;
	
	private ManagedObjectReference managedObjectReference;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ManagedObjectReference getManagedObjectReference() {
		return managedObjectReference;
	}

	public void setManagedObjectReference(ManagedObjectReference managedObjectReference) {
		this.managedObjectReference = managedObjectReference;
	}

	public long getCapcity() {
		return capcity;
	}

	public void setCapcity(long capcity) {
		this.capcity = capcity;
	}

	public long getFree() {
		return free;
	}

	public void setFree(long free) {
		this.free = free;
	}

}
