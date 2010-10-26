package com.octo.vmware.commands;

import java.util.List;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.ResourcePool;
import com.octo.vmware.services.ResourcePoolService;
import com.octo.vmware.utils.VimServiceUtil;

public class ListResourcePools implements ICommand {

	public void execute(String[] args) throws Exception {
		if (args.length != 1) {
			throw new SyntaxError();
		}
		String esxName = args[0];
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(esxName);
		List<ResourcePool> resourcePools = ResourcePoolService.getResourcePoolList(vimServiceUtil);

		System.out.println("Found " + resourcePools.size() + " resource pool(s) on " + esxName);
		if (resourcePools.size() > 0) {
			System.out.println(String.format("%-20s", "Resource Pool Name"));
			System.out.println("-------------------");
			for (ResourcePool resourcePool : resourcePools) {
				System.out.println(String.format("%-20s", resourcePool.getName()));
			}
		}
	}

	public String getSyntax() {
		return "esx_name"; 
	}

	public String getHelp() {
		return "list resources pool of an esx";
	}

	public String getCommand() {
		return "list_resource_pool";
	}

	public Target getTarget() {
		return Target.ESX;
	}

}
