package com.octo.vmware.commands;

import java.util.List;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.ConverterTask;
import com.octo.vmware.services.ConverterTasksListService;
import com.octo.vmware.utils.ConverterServiceUtil;

public class ClearFinishedTasks implements ICommand {

	public void execute(IOutputer outputer, String[] args) throws Exception {
		ConverterServiceUtil converterServiceUtil = ConverterServiceUtil.getConverter();
		
		List<ConverterTask> list = ConverterTasksListService.getTaskList(converterServiceUtil);
		for(ConverterTask task : list) {
			if (task.isFinished()) {
				outputer.log("Removing task " + task.getId());
				converterServiceUtil.getService().converterDestroyTask(converterServiceUtil.getServiceContent().getTaskManager(), task.getManagedObjectReference());
			}
		}
		outputer.result(true);
	}

	public String getCommand() {
		return "clear_finished_task";
	}

	public String getHelp() {
		return "clear all finished tasks";
	}

	public String getSyntax() {
		return "";
	}

	public Target getTarget() {
		return Target.CONVERTER;
	}

}
