package com.octo.vmware.services;

import java.util.ArrayList;
import java.util.List;

import vim25.ArrayOfManagedObjectReference;
import vim25.DatastoreSummary;
import vim25.ManagedObjectReference;

import com.octo.vmware.entities.Datastore;
import com.octo.vmware.utils.VimServiceUtil;

public class DatastoreService {
	
	public static List<Datastore> getDatastoreList(VimServiceUtil vimServiceUtil) throws Exception {
		List<Datastore> list = new ArrayList<Datastore>();
		ArrayOfManagedObjectReference array = HostConfigService.getHostConfig(vimServiceUtil, "datastore");
		for (ManagedObjectReference managedObjectReference : array.getManagedObjectReference()) {
			String name = PropertiesService.getProperties(vimServiceUtil, "name", managedObjectReference);
			vimServiceUtil.getService().refreshDatastore(managedObjectReference);
			DatastoreSummary summary = PropertiesService.getProperties(vimServiceUtil, "summary", managedObjectReference);
			Datastore datastore = new Datastore();
			datastore.setManagedObjectReference(managedObjectReference);
			datastore.setName(name);
			datastore.setCapcity(summary.getCapacity());
			datastore.setFree(summary.getFreeSpace());
			list.add(datastore);
		}
		return list;
	}
	
}
