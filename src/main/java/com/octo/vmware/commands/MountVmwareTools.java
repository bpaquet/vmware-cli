package com.octo.vmware.commands;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class MountVmwareTools implements ICommand {

	public void execute(IOutputer outputer, String[] args) throws Exception {
		if (args.length != 1) {
			throw new SyntaxError();
		}
		VmLocation vmLocation = new VmLocation(args[0]);
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(vmLocation.getEsxName());
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, vmLocation.getVmName());
		outputer.log("Mount tools install on virtual machine " + vmInfo.getName() + " on host " + vmLocation.getEsxName());
		vimServiceUtil.getService().mountToolsInstaller(vmInfo.getManagedObjectReference());
		outputer.result(true);
	}


	public String getSyntax() {
		return "esx_name:vm_name"; 
	}

	public String getHelp() {
		return "mounts the vmware tools cd into virtual machine";
	}

	public String getCommand() {
		return "mount_tools";
	}

	public Target getTarget() {
		return Target.ESX;
	}

}
