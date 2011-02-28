package com.octo.vmware.services;

import java.util.Arrays;
import java.util.List;

import vim25.DynamicProperty;
import vim25.ObjectContent;
import vim25.ObjectSpec;
import vim25.PropertyFilterSpec;
import vim25.PropertySpec;
import vim25.TraversalSpec;

import com.octo.vmware.utils.TraversalSpecHelper;
import com.octo.vmware.utils.VimServiceUtil;

public class HostConfigService {

	@SuppressWarnings("unchecked")
	public static <T> T getHostConfig(VimServiceUtil vimServiceUtil, String type) throws Exception {
		TraversalSpec crToH = TraversalSpecHelper.makeTraversalSpec("ComputeResource", "host", "crToH", false, new String[]{}, new TraversalSpec[]{});
		TraversalSpec dataCenterToHostFolder = TraversalSpecHelper.makeTraversalSpec("Datacenter", "hostFolder", "dcToHf", false, new String[]{"VisitFolders"}, new TraversalSpec[]{});
		TraversalSpec traversalSpec = TraversalSpecHelper.makeTraversalSpec("Folder", "childEntity", "VisitFolders", false, new String[]{}, new TraversalSpec[]{dataCenterToHostFolder, crToH});
	
		ObjectSpec objectSpec = new ObjectSpec();
		objectSpec.setObj(vimServiceUtil.getServiceContent().getRootFolder());
		objectSpec.setSkip(false);
		objectSpec.getSelectSet().add(traversalSpec);
		
		PropertySpec propertySpec = new PropertySpec();
		propertySpec.getPathSet().add(type);
		propertySpec.setAll(false);
		propertySpec.setType("HostSystem");
		
		PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
		propertyFilterSpec.getObjectSet().add(objectSpec);
		propertyFilterSpec.getPropSet().add(propertySpec);
		List<PropertyFilterSpec> propertyFilterSpecsList = Arrays.asList(propertyFilterSpec);
		List<ObjectContent> hostSystems = vimServiceUtil.getService().retrieveProperties(
				vimServiceUtil.getServiceContent().getPropertyCollector(), propertyFilterSpecsList);
		
		for(ObjectContent objectContent :  hostSystems) {
			for(DynamicProperty prop : objectContent.getPropSet()) {
				if (type.equals(prop.getName())) {
					return (T) prop.getVal();
				}
			}
		}
		throw new RuntimeException("Host config not found");
	}
}
