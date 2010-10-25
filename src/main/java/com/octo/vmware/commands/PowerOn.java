package com.octo.vmware.commands;

import vim2.ManagedObjectReference;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.services.TaskInfoService;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class PowerOn implements ICommand {

	public void execute(String[] args) throws Exception {
		if (args.length != 2) {
			throw new SyntaxError();
		}
		VimServiceUtil vimServiceUtil = new VimServiceUtil(args[0]);
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, args[1]);
		System.out.println("Power on vm " + vmInfo.getName() + " on host " + args[0]);
		ManagedObjectReference task = vimServiceUtil.getService().powerOnVMTask(vmInfo.getManagedObjectReference(), null);
		System.out.println("Result : " + (TaskInfoService.waitForEnd(vimServiceUtil, task) ? "OK" : "Error"));
	}
	
	public String getCommandHelp() {
		return "power_on esx_server_name vm_name : power on a vm on an esx server";
	}

	public String getCommandName() {
		return "power_on";
	}

}
