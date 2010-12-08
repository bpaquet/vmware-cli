package com.octo.vmware.commands;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class Show implements ICommand {

	public void execute(String[] args) throws Exception {
		if (args.length != 1) {
			throw new SyntaxError();
		}
		VmLocation vmLocation = new VmLocation(args[0]);
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(vmLocation.getEsxName());
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, vmLocation.getVmName());
		System.out.println("Virtual machine '" + vmLocation.getVmName() + "' on " + vmLocation.getEsxName());
		System.out.println("Status : " + vmInfo.getStatus());
		System.out.println("RAM : " + vmInfo.getRam());
		System.out.println("CPU : " + vmInfo.getCpu());
		System.out.println("Resource pool : " + vmInfo.getResourcePool().getName());
		System.out.println("VMWare tools : " + vmInfo.getGuestToolsStatus());
		System.out.println("Guest hostname : " + vmInfo.getGuestHostname());
		System.out.println("Guest ip : " + vmInfo.getGuestIp());
		System.out.println("Guest os name : " + vmInfo.getGuestFullName());
		System.out.println("UUID : " + vmInfo.getUuid());
		System.out.println("VM Path : " + vmInfo.getPath());
		System.out.print("Datastores : ");
		for(String s : vmInfo.getDatastores()) {
			System.out.print(s + ", ");
		}
		System.out.println();
		System.out.print("Disks : ");
		for(String s : vmInfo.getDisks()) {
			System.out.print(s + ", ");
		}
		System.out.println();
		System.out.print("Networks : ");
		for(String s : vmInfo.getNetworks()) {
			System.out.print(s + ", ");
		}
		System.out.println();
	}

	public String getSyntax() {
		return "esx_name:vm_name"; 
	}

	public String getHelp() {
		return "show details about a virtual machine";
	}

	public String getCommand() {
		return "show";
	}

	public Target getTarget() {
		return Target.ESX;
	}

}
