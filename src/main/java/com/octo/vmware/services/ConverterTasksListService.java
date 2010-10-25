package com.octo.vmware.services;

import java.util.ArrayList;
import java.util.List;

import vim25.ManagedObjectReference;

import com.octo.vmware.entities.ConverterTask;
import com.octo.vmware.utils.ConverterServiceUtil;

import converter.ConverterTaskFilterSpec;
import converter.ConverterTaskInfo;

public class ConverterTasksListService {

	public static List<ConverterTask> getTaskList(ConverterServiceUtil converterServiceUtil) throws Exception {
		// Empty/Mandatory tasks filter
		ConverterTaskFilterSpec filter = new ConverterTaskFilterSpec();
		ManagedObjectReference collector = converterServiceUtil.getService().converterCreateCollectorForTasks(
				converterServiceUtil.getServiceContent().getTaskManager(), filter);
		List<ConverterTaskInfo> rawList = converterServiceUtil.getService().converterReadNextTasks(collector, 200);

		List<ConverterTask> l = new ArrayList<ConverterTask>();
		for (ConverterTaskInfo info : rawList) {
			ConverterTask converterTask = new ConverterTask();
			converterTask.setSource(info.getSource());
			converterTask.setTarget(info.getTarget());
			converterTask.setProgress(info.getProgress());
			converterTask.setStatus(info.getState().toString());
			l.add(converterTask);
		}
		
		return l;
	}
	
}
