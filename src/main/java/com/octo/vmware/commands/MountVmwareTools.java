package com.octo.vmware.commands;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class MountVmwareTools implements ICommand {

	public void execute(String[] args) throws Exception {
		if (args.length != 1) {
			throw new SyntaxError();
		}
		VmLocation vmLocation = new VmLocation(args[0]);
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(vmLocation.getEsxName());
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, vmLocation.getVmName());
		System.out.println("Mount tools install on virtual machine " + vmInfo.getName() + " on host " + vmLocation.getEsxName());
		vimServiceUtil.getService().mountToolsInstaller(vmInfo.getManagedObjectReference());
		System.out.println("Done");
	}

	public String getCommandHelp() {
		return "mount_tools esx_name:vm_name                                   : mounts the vmware tools cd into virtual machine";
	}

	public String getCommandName() {
		return "mount_tools";
	}

	public Target getTarget() {
		return Target.ESX;
	}

}
