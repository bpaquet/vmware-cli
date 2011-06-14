package com.octo.vmware.commands;

import vim25.ManagedObjectReference;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.services.PropertiesService;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class DeleteFromDisk implements ICommand {

	public void execute(IOutputer outputer, String[] args) throws Exception {
		if (args.length != 1) {
			throw new SyntaxError();
		}
		VmLocation vmLocation = new VmLocation(args[0]);
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(vmLocation.getEsxName());
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, vmLocation.getVmName());
		outputer.log("Delete virtual machine " + vmInfo.getName() + " on host " + vmLocation.getEsxName());
		ManagedObjectReference task = vimServiceUtil.getService().destroyTask(vmInfo.getManagedObjectReference());
		outputer.result(PropertiesService.waitForTaskEnd(vimServiceUtil, task));
	}

	public String getCommand() {
		return "delete_from_disk";
	}

	public String getHelp() {
		return "delete a virtual machine from an esx host";
	}

	public String getSyntax() {
		return "esx_name:vm_name";
	}

	public Target getTarget() {
		return Target.ESX;
	}

}
