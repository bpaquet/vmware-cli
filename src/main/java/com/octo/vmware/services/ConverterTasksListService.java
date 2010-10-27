package com.octo.vmware.services;

import java.util.ArrayList;
import java.util.List;

import vim25.ManagedObjectReference;

import com.octo.vmware.entities.ConverterTask;
import com.octo.vmware.utils.ConverterServiceUtil;

import converter.ConverterTaskFilterSpec;
import converter.ConverterTaskInfo;

public class ConverterTasksListService {

	public static ConverterTask getTask(ConverterServiceUtil converterServiceUtil, String id) throws Exception {
		for(ConverterTask converterTask : getTaskList(converterServiceUtil)) {
			if (id.equals(converterTask.getId())) {
				return converterTask;
			}
		}
		throw new RuntimeException("Task not found " + id);
	}
	
	public static List<ConverterTask> getTaskList(ConverterServiceUtil converterServiceUtil) throws Exception {
		ConverterTaskFilterSpec filter = new ConverterTaskFilterSpec();
		ManagedObjectReference collector = converterServiceUtil.getService().converterCreateCollectorForTasks(
				converterServiceUtil.getServiceContent().getTaskManager(), filter);
		List<ConverterTaskInfo> rawList = converterServiceUtil.getService().converterReadNextTasks(collector, 200);

		List<ConverterTask> l = new ArrayList<ConverterTask>();
		for (ConverterTaskInfo info : rawList) {
			ConverterTask converterTask = new ConverterTask();
			converterTask.setId(info.getTask().getValue().substring(5));
			converterTask.setSource(info.getSource());
			converterTask.setTarget(info.getTarget());
			converterTask.setProgress(info.getProgress() != null ? info.getProgress() : 0);
			converterTask.setStatus(info.getState().toString());
			converterTask.setManagedObjectReference(info.getTask());
			l.add(converterTask);
		}
		
		return l;
	}
	
}
