package com.octo.vmware.commands;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class MountVmwareTools implements ICommand {

	public void execute(String[] args) throws Exception {
		if (args.length != 2) {
			throw new SyntaxError();
		}
		VimServiceUtil vimServiceUtil = new VimServiceUtil(args[0]);
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, args[1]);
		System.out.println("Mount tools install on vm " + vmInfo.getName() + " on host " + args[0]);
		vimServiceUtil.getService().mountToolsInstaller(vmInfo.getManagedObjectReference());
		System.out.println("Done");
	}

	public String getCommandHelp() {
		return "mount_tools esx_server_name vm_name : mounts the vmware tools cd into vm";
	}

	public String getCommandName() {
		return "mount_tools";
	}

}
