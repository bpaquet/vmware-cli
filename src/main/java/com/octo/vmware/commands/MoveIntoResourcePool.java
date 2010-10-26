package com.octo.vmware.commands;

import java.util.Arrays;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.ResourcePool;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.services.ResourcePoolService;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class MoveIntoResourcePool implements ICommand {

	public void execute(String[] args) throws Exception {
		if (args.length != 2) {
			throw new SyntaxError();
		}
		VmLocation vmLocation = new VmLocation(args[0]);
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(vmLocation.getEsxName());
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, vmLocation.getVmName());
		ResourcePool resourcePool = ResourcePoolService.findResourcePoolByName(vimServiceUtil, args[1]);
		System.out.println("Moving virtual machine " + vmInfo.getName() + " on host " + vmLocation.getEsxName() + " into resource pool " + resourcePool.getName());
		vimServiceUtil.getService().moveIntoResourcePool(resourcePool.getManagedObjectReference(), Arrays.asList(vmInfo.getManagedObjectReference()));
		System.out.println("Done");
	}

	public String getSyntax() {
		return "esx_name:vm_name resource_pool_name"; 
	}

	public String getHelp() {
		return "move virtual machine into resource pool";
	}

	public String getCommand() {
		return "move_into_resource_pool";
	}

	public Target getTarget() {
		return Target.ESX;
	}

}
