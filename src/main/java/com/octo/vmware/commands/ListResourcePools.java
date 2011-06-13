package com.octo.vmware.commands;

import java.util.List;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.ResourcePool;
import com.octo.vmware.services.ResourcePoolService;
import com.octo.vmware.utils.VimServiceUtil;

public class ListResourcePools implements ICommand {

	public void execute(IOutputer outputer, String[] args) throws Exception {
		if (args.length != 1) {
			throw new SyntaxError();
		}
		String esxName = args[0];
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(esxName);
		List<ResourcePool> resourcePools = ResourcePoolService.getResourcePoolList(vimServiceUtil);

		outputer.output(resourcePools, vimServiceUtil, new IObjectOutputer<List<ResourcePool>>() {

			public void output(IOutputer outputer, VimServiceUtil vimServiceUtil, List<ResourcePool> resourcePools) {
				outputer.log("Found " + resourcePools.size() + " resource pool(s) on " + vimServiceUtil.getEsxServer().getName());
				if (resourcePools.size() > 0) {
					outputer.log(String.format("%-20s", "Resource Pool Name"));
					outputer.log("-------------------");
					for (ResourcePool resourcePool : resourcePools) {
						outputer.log(String.format("%-20s", resourcePool.getName()));
					}
				}
			}
			
		});
		
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
