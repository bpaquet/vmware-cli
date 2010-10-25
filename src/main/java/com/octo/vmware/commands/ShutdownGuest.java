package com.octo.vmware.commands;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class ShutdownGuest implements ICommand {

	public void execute(String[] args) throws Exception {
		if (args.length != 2) {
			throw new SyntaxError();
		}
		VimServiceUtil vimServiceUtil = new VimServiceUtil(args[0]);
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, args[1]);
		System.out.println("Shutdown guest vm " + vmInfo.getName() + " on host " + args[0]);
		vimServiceUtil.getService().shutdownGuest(vmInfo.getManagedObjectReference());
		System.out.println("Done");
	}
	
	public String getCommandHelp() {
		return "shutdown_guest esx_server_name vm_name : try to shutdown a vm on an esx server";
	}

	public String getCommandName() {
		return "shutdown_guest";
	}

}
