package com.octo.vmware.services;

import java.util.Arrays;
import java.util.List;

import vim2.DynamicProperty;
import vim2.ManagedObjectReference;
import vim2.ObjectContent;
import vim2.ObjectSpec;
import vim2.PropertyFilterSpec;
import vim2.PropertySpec;
import vim2.TaskInfo;
import vim2.TaskInfoState;

import com.octo.vmware.utils.VimServiceUtil;

public class PropertiesService {

	public static boolean waitForTaskEnd(VimServiceUtil vimServiceUtil, ManagedObjectReference task) throws Exception {
		while(true) {
			TaskInfo taskInfo = getProperties(vimServiceUtil, "info", task);
			if (taskInfo.getProgress() == 100) {
				return taskInfo.getState() == TaskInfoState.SUCCESS;
			}
			Thread.sleep(2000);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getProperties(VimServiceUtil vimServiceUtil, String propName, ManagedObjectReference managementObjectReference) throws Exception {
		ObjectSpec objectSpec = new ObjectSpec();
		objectSpec.setObj(managementObjectReference);
		PropertySpec propertySpec = new PropertySpec();
		propertySpec.getPathSet().add(propName);
		//propertySpec.setAll(true);
		propertySpec.setType(managementObjectReference.getType());
		
		PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
		propertyFilterSpec.getObjectSet().add(objectSpec);
		propertyFilterSpec.getPropSet().add(propertySpec);
		List<PropertyFilterSpec> propertyFilterSpecsList = Arrays.asList(propertyFilterSpec);
		List<ObjectContent> objs = vimServiceUtil.getService().retrieveProperties(
				vimServiceUtil.getServiceContent().getPropertyCollector(), propertyFilterSpecsList);
		
		for(ObjectContent objectContent : objs) {
			for(DynamicProperty prop : objectContent.getPropSet()) {
				if (propName.equals(prop.getName())) {
					return (T) prop.getVal();
				}
			}
		}
		
		throw new RuntimeException("Unable to find object " + managementObjectReference.getValue());
	}
}
