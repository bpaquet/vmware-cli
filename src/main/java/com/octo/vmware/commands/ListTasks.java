package com.octo.vmware.commands;

import java.util.List;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.ConverterTask;
import com.octo.vmware.services.ConverterTasksListService;
import com.octo.vmware.utils.ConverterServiceUtil;

public class ListTasks implements ICommand {

	public void execute(String[] args) throws Exception {
		ConverterServiceUtil converterServiceUtil = ConverterServiceUtil.getConverter();
		List<ConverterTask> list = ConverterTasksListService.getTaskList(converterServiceUtil);

		// Display output
		System.out.println("Found " + list.size() + " task(s).");
		if (list.size() > 0) {
			String header = String
					.format("%-5s %-40s %-40s %-10s %-10s %s", "Id", "Source VM", "Target VM", "Status", "Progress", "Error message");
			System.out.println(header);
			System.out
					.println("------------------------------------------------------------------------------------------------------------------------");
			for (ConverterTask converterTask : list) {
				System.out.println(String.format("%-5s %-40s %-40s %-10s %-10s %s", converterTask.getId(), converterTask.getSource(),
						converterTask.getTarget(), converterTask.getStatus(), converterTask.getProgress()
								+ "%".toString(), converterTask.getErrorMessage()));
			}
		}

	}

	public String getSyntax() {
		return ""; 
	}
	
	public String getHelp() {
		return "lists all tasks of the converter";
	}

	public String getCommand() {
		return "list_tasks";
	}

	public Target getTarget() {
		return Target.CONVERTER;
	}

}
