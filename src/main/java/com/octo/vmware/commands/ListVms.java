package com.octo.vmware.commands;

import java.util.List;

import vim2.VirtualMachinePowerState;
import vim2.VirtualMachineToolsStatus;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class ListVms implements ICommand {

	public String getCommandHelp() {
		return "list esx_name                       : list virtual machiness of an esx server";
	}

	public String getCommandName() {
		return "list";
	}

	public void execute(String [] args) throws Exception {
		if (args.length != 1) {
			throw new SyntaxError();
		}
		String esxName = args[0];
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(esxName);
		List<VmInfo> vmsList = VmsListService.getVmsList(vimServiceUtil);

		System.out.println("Found " + vmsList.size() + " VM(s) on " + esxName);
		if (vmsList.size() > 0) {
			System.out.println(String.format("%-20s %-30s %11s %-9s %-40s", "Resource Pool", "VM Name", "Status", "CPU/RAM", "Guest"));
			System.out.println("--------------------------------------------------------------------------------------");
			for (VmInfo info : vmsList) {
				System.out.println(String.format("%-20s %-30s %11s %2d %6d %s", info.getResourcePool().getName(), info.getName(), info.getStatus(), info.getCpu(), info.getRam(), formatGuest(info)));
			}
		}
	}
	
	private String formatGuest(VmInfo vmInfo) {
		if (VirtualMachineToolsStatus.TOOLS_OK.toString().equals(vmInfo.getGuestToolsStatus())) {
			return vmInfo.getGuestToolsStatus() + " (" + vmInfo.getGuestIp() + " : " + vmInfo.getGuestHostname() + ")";
		}
		else {
			return VirtualMachinePowerState.POWERED_ON.toString().equals(vmInfo.getStatus()) ? vmInfo.getGuestToolsStatus() : "";
		}
	}
	
	public Target getTarget() {
		return Target.ESX;
	}

}
