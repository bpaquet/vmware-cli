package com.octo.vmware.services;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vim25.DynamicProperty;
import vim25.ManagedObjectReference;
import vim25.ObjectContent;
import vim25.ObjectSpec;
import vim25.PropertyFilterSpec;
import vim25.PropertySpec;
import vim25.TraversalSpec;

import com.octo.vmware.entities.ResourcePool;
import com.octo.vmware.utils.TraversalSpecHelper;
import com.octo.vmware.utils.VimServiceUtil;

public class ResourcePoolService {

	public static ResourcePool searchPoolName(VimServiceUtil vimServiceUtil, ManagedObjectReference managedObjectReference) throws Exception {
		String name = PropertiesService.getProperties(vimServiceUtil, "name", managedObjectReference);
		ResourcePool resourcePool = new ResourcePool();
		resourcePool.setManagedObjectReference(managedObjectReference);
		resourcePool.setName(URLDecoder.decode(name, "UTF-8"));
		return resourcePool;
	}
	
	public static ResourcePool findResourcePoolByName(VimServiceUtil vimServiceUtil, String name) throws Exception {
		for(ResourcePool resourcePool : getResourcePoolList(vimServiceUtil)) {
			if (name.equals(URLDecoder.decode(resourcePool.getName(), "UTF-8"))) {
				return resourcePool;
			}
		}
		throw new RuntimeException("Resource pool not found : " + name);
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
					resourcePool.setName(URLDecoder.decode((String) prop.getVal(), "UTF-8"));
				}
			}
			list.add(resourcePool);
		}
		return list;
	}
}
