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

public class TaskInfoService {

	public static boolean waitForEnd(VimServiceUtil vimServiceUtil, ManagedObjectReference task) throws Exception {
		while(true) {
			TaskInfo taskInfo = getTaskInfo(vimServiceUtil, task);
			if (taskInfo.getProgress() == 100) {
				return taskInfo.getState() == TaskInfoState.SUCCESS;
			}
			Thread.sleep(2000);
		}
	}
	
	public static TaskInfo getTaskInfo(VimServiceUtil vimServiceUtil, ManagedObjectReference task) throws Exception {
		ObjectSpec objectSpec = new ObjectSpec();
		objectSpec.setObj(task);
		// Specifies objects to retrieve are VirtualMachines
		PropertySpec propertySpec = new PropertySpec();
		propertySpec.setAll(true);
		propertySpec.setType(task.getType());
		
		// Finally retrieve VirtualMachines in datacenter
		PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
		propertyFilterSpec.getObjectSet().add(objectSpec);
		propertyFilterSpec.getPropSet().add(propertySpec);
		List<PropertyFilterSpec> propertyFilterSpecsList = Arrays.asList(propertyFilterSpec);
		List<ObjectContent> objs = vimServiceUtil.getService().retrieveProperties(
				vimServiceUtil.getServiceContent().getPropertyCollector(), propertyFilterSpecsList);
		
		for(ObjectContent objectContent : objs) {
			for(DynamicProperty prop : objectContent.getPropSet()) {
				if ("info".equals(prop.getName())) {
					return (TaskInfo) prop.getVal();
				}
			}
		}
		throw new RuntimeException("Unable to find task " + task.getValue());
	}
}
