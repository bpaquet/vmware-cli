package com.octo.vmware.entities;

import vim2.ManagedObjectReference;

public class ResourcePool {

	private ManagedObjectReference managedObjectReference;
	
	private String name;

	public ManagedObjectReference getManagedObjectReference() {
		return managedObjectReference;
	}

	public void setManagedObjectReference(ManagedObjectReference managedObjectReference) {
		this.managedObjectReference = managedObjectReference;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
