package com.octo.vmware.commands;

import vim25.ManagedObjectReference;
import vim25.VirtualMachineConfigSpec;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.services.PropertiesService;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class SetCpu implements ICommand {

	public void execute(String[] args) throws Exception {
		if (args.length != 2) {
			throw new SyntaxError();
		}
		VmLocation vmLocation = new VmLocation(args[0]);
		int numberOfCpu = Integer.parseInt(args[1]);
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(vmLocation.getEsxName());
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, vmLocation.getVmName());
		System.out.println("Set number of cpu : " + numberOfCpu + " for virtual machine " + vmInfo.getName() + " on host " + vmLocation.getEsxName());
		VirtualMachineConfigSpec configSpec = new VirtualMachineConfigSpec();
		configSpec.setNumCPUs(numberOfCpu);
		ManagedObjectReference task = vimServiceUtil.getService().reconfigVMTask(vmInfo.getManagedObjectReference(), configSpec);
		System.out.println("Result : " + (PropertiesService.waitForTaskEnd(vimServiceUtil, task) ? "OK" : "Error"));
	}

	public String getSyntax() {
		return "esx_name:vm_name cpu_number"; 
	}

	public String getHelp() {
		return "set number of cpu";
	}

	public String getCommand() {
		return "set_cpu";
	}

	public Target getTarget() {
		return Target.ESX;
	}
	
}
