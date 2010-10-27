package com.octo.vmware.commands;

import vim25.ManagedObjectReference;

import com.octo.vmware.ICommand;
import com.octo.vmware.utils.ConverterServiceUtil;

public class CancelTask implements ICommand {

	public void execute(String[] args) throws Exception {
		if (args.length != 1) {
			throw new SyntaxError();
		}
		
		ConverterServiceUtil converterServiceUtil = ConverterServiceUtil.getConverter();
		ManagedObjectReference managedObjectReference = new ManagedObjectReference();
		managedObjectReference.setType("ConverterTask");
		managedObjectReference.setValue("task-" + args[0]);
		
		System.out.println("Cancelling task " + args[0]);
		converterServiceUtil.getService().converterCancelTask(managedObjectReference);
		System.out.println("Done.");
	}

	public String getCommand() {
		return "cancel_task";
	}

	public String getHelp() {
		return "cancel a converter task";
	}

	public String getSyntax() {
		return "task_id";
	}

	public Target getTarget() {
		return Target.CONVERTER;
	}

}
