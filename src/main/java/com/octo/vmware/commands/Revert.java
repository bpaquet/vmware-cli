package com.octo.vmware.commands;

import vim25.ManagedObjectReference;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.services.PropertiesService;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class Revert implements ICommand {

	public void execute(IOutputer outputer, String[] args) throws Exception {
		if (args.length != 1) {
			throw new SyntaxError();
		}
		VmLocation vmLocation = new VmLocation(args[0]);
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(vmLocation.getEsxName());
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, vmLocation.getVmName());
		outputer.log("Reverting virtual machine " + vmInfo.getName() + " on host " + vmLocation.getEsxName());
		ManagedObjectReference task = vimServiceUtil.getService().revertToCurrentSnapshotTask(vmInfo.getManagedObjectReference(), null, false);
		outputer.result(PropertiesService.waitForTaskEnd(vimServiceUtil, task));
	}

	public String getSyntax() {
		return "esx_name:vm_name"; 
	}

	public String getHelp() {
		return "revert a virutal machine the last snapshot";
	}

	public String getCommand() {
		return "revert";
	}

	public Target getTarget() {
		return Target.ESX;
	}
	
}
