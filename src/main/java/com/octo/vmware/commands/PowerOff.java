package com.octo.vmware.commands;

import vim2.ManagedObjectReference;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.services.PropertiesService;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class PowerOff implements ICommand {

	public void execute(String[] args) throws Exception {
		if (args.length != 1) {
			throw new SyntaxError();
		}
		VmLocation vmLocation = new VmLocation(args[0]);
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(vmLocation.getEsxName());
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, vmLocation.getVmName());
		System.out.println("Power off virtual machine " + vmInfo.getName() + " on host " + vmLocation.getEsxName());
		ManagedObjectReference task = vimServiceUtil.getService().powerOffVMTask(vmInfo.getManagedObjectReference());
		System.out.println("Result : " + (PropertiesService.waitForTaskEnd(vimServiceUtil, task) ? "OK" : "Error"));
	}
	
	public String getCommandHelp() {
		return "power_off esx_name:vm_name                                     : power off a virtual machine on an esx server";
	}

	public String getCommandName() {
		return "power_off";
	}

	public Target getTarget() {
		return Target.ESX;
	}

}
