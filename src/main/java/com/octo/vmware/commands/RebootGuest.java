package com.octo.vmware.commands;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class RebootGuest implements ICommand {

	public void execute(IOutputer outputer, String[] args) throws Exception {
		if (args.length != 1) {
			throw new SyntaxError();
		}
		VmLocation vmLocation = new VmLocation(args[0]);
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(vmLocation.getEsxName());
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, vmLocation.getVmName());
		outputer.log("Reboot guest virtual machine " + vmInfo.getName() + " on host " + vmLocation.getEsxName());
		vimServiceUtil.getService().rebootGuest(vmInfo.getManagedObjectReference());
		outputer.result(true);
	}
	
	public String getSyntax() {
		return "esx_name:vm_name"; 
	}

	public String getHelp() {
		return "try to reboot a virtual machine on an esx server";
	}

	public String getCommand() {
		return "reboot_guest";
	}

	public Target getTarget() {
		return Target.ESX;
	}

}
