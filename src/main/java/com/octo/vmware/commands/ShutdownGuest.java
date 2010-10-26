package com.octo.vmware.commands;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class ShutdownGuest implements ICommand {

	public void execute(String[] args) throws Exception {
		if (args.length != 1) {
			throw new SyntaxError();
		}
		VmLocation vmLocation = new VmLocation(args[0]);
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(vmLocation.getEsxName());
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, vmLocation.getVmName());
		System.out.println("Shutdown guest virtual machine " + vmInfo.getName() + " on host " + vmLocation.getEsxName());
		vimServiceUtil.getService().shutdownGuest(vmInfo.getManagedObjectReference());
		System.out.println("Done");
	}
	
	public String getCommandHelp() {
		return "shutdown_guest esx_name:vm_name                                : try to shutdown a virtual machine on an esx server";
	}

	public String getCommandName() {
		return "shutdown_guest";
	}

	public Target getTarget() {
		return Target.ESX;
	}

}
