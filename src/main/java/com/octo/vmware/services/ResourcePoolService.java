package com.octo.vmware.services;

import vim2.ManagedObjectReference;

import com.octo.vmware.entities.ResourcePool;
import com.octo.vmware.utils.VimServiceUtil;

public class ResourcePoolService {

	public static ResourcePool searchPoolName(VimServiceUtil vimServiceUtil, ManagedObjectReference managedObjectReference) throws Exception {
		String name = PropertiesService.getProperties(vimServiceUtil, "name", managedObjectReference);
		ResourcePool resourcePool = new ResourcePool();
		resourcePool.setManagedObjectReference(managedObjectReference);
		resourcePool.setName(name);
		return resourcePool;
	}
}
