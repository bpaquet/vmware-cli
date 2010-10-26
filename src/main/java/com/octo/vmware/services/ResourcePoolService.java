package com.octo.vmware.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vim2.DynamicProperty;
import vim2.ManagedObjectReference;
import vim2.ObjectContent;
import vim2.ObjectSpec;
import vim2.PropertyFilterSpec;
import vim2.PropertySpec;
import vim2.TraversalSpec;

import com.octo.vmware.entities.ResourcePool;
import com.octo.vmware.utils.TraversalSpecHelper;
import com.octo.vmware.utils.VimServiceUtil;

public class ResourcePoolService {

	public static ResourcePool searchPoolName(VimServiceUtil vimServiceUtil, ManagedObjectReference managedObjectReference) throws Exception {
		String name = PropertiesService.getProperties(vimServiceUtil, "name", managedObjectReference);
		ResourcePool resourcePool = new ResourcePool();
		resourcePool.setManagedObjectReference(managedObjectReference);
		resourcePool.setName(name);
		return resourcePool;
	}
	
	public static List<ResourcePool> getResourcePoolList(VimServiceUtil vimServiceUtil) throws Exception {
		TraversalSpec rpToRp = TraversalSpecHelper.makeTraversalSpec("ResourcePool", "resourcePool", "rpToRp", false, new String[]{"rpToRp", "rpToVm"}, new TraversalSpec[]{});
		TraversalSpec crToH = TraversalSpecHelper.makeTraversalSpec("ComputeResource", "host", "crToH", false, new String[]{}, new TraversalSpec[]{});
		TraversalSpec rpToVm = TraversalSpecHelper.makeTraversalSpec("ResourcePool", "vm", "rpToVm", false, new String[]{}, new TraversalSpec[]{});
		TraversalSpec crToRp = TraversalSpecHelper.makeTraversalSpec("ComputeResource", "resourcePool", "crToRp", false, new String[]{"rpToVm", "rpToRp"}, new TraversalSpec[]{});
		TraversalSpec dataCenterToHostFolder = TraversalSpecHelper.makeTraversalSpec("Datacenter", "hostFolder", "dcToHf", false, new String[]{"VisitFolders"}, new TraversalSpec[]{});
		TraversalSpec traversalSpec = TraversalSpecHelper.makeTraversalSpec("Folder", "childEntity", "VisitFolders", false, new String[]{}, new TraversalSpec[]{dataCenterToHostFolder, crToH, rpToVm, crToRp, rpToRp});
	
		ObjectSpec objectSpec = new ObjectSpec();
		objectSpec.setObj(vimServiceUtil.getServiceContent().getRootFolder());
		objectSpec.setSkip(true);
		objectSpec.getSelectSet().add(traversalSpec);
		
		PropertySpec propertySpec = new PropertySpec();
		propertySpec.getPathSet().add("name");
		propertySpec.setAll(false);
		propertySpec.setType("ResourcePool");
		
		// Finally retrieve VirtualMachines in datacenter
		PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
		propertyFilterSpec.getObjectSet().add(objectSpec);
		propertyFilterSpec.getPropSet().add(propertySpec);
		List<PropertyFilterSpec> propertyFilterSpecsList = Arrays.asList(propertyFilterSpec);
		List<ObjectContent> resourcePools = vimServiceUtil.getService().retrieveProperties(
				vimServiceUtil.getServiceContent().getPropertyCollector(), propertyFilterSpecsList);
		
		List<ResourcePool> list = new ArrayList<ResourcePool>();
		for(ObjectContent objectContent : resourcePools) {
			ResourcePool resourcePool = new ResourcePool();
			resourcePool.setManagedObjectReference(objectContent.getObj());
			for(DynamicProperty prop : objectContent.getPropSet()) {
				if ("name".equals(prop.getName())) {
					resourcePool.setName((String) prop.getVal());
				}
			}
			list.add(resourcePool);
		}
		return list;
	}
}
