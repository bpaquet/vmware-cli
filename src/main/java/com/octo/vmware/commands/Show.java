package com.octo.vmware.commands;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class Show implements ICommand {

	public void execute(String[] args) throws Exception {
		if (args.length != 2) {
			throw new SyntaxError();
		}
		VimServiceUtil vimServiceUtil = new VimServiceUtil(args[0]);
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, args[1]);
		System.out.println("VM '" + args[1] + "' on " + args[0]);
		System.out.println("Status : " + vmInfo.getStatus());
		System.out.println("RAM : " + vmInfo.getRam());
		System.out.println("CPU : " + vmInfo.getCpu());
		System.out.println("VMWare tools : " + vmInfo.getGuestToolsStatus());
		System.out.println("Guest hostname : " + vmInfo.getGuestHostname());
		System.out.println("Guest ip : " + vmInfo.getGuestIp());
		System.out.println("Guest os name : " + vmInfo.getGuestFullName());
		System.out.println("UUID : " + vmInfo.getUuid());
		System.out.print("Datastores : ");
		for(String s : vmInfo.getDatastores()) {
			System.out.print(s + ", ");
		}
		System.out.println();
		System.out.print("Networks : ");
		for(String s : vmInfo.getNetworks()) {
			System.out.print(s + ", ");
		}
		System.out.println();
	}

	public String getCommandHelp() {
		return "show esx_server_name vm_name";
	}

	public String getCommandName() {
		return "show";
	}

}
