package com.octo.vmware.commands;

import vim25.ManagedObjectReference;
import vim25.VirtualDeviceConfigSpec;
import vim25.VirtualDeviceConfigSpecOperation;
import vim25.VirtualDisk;
import vim25.VirtualMachineConfigSpec;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmDisk;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.services.PropertiesService;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class RemoveDisk implements ICommand {

	public void execute(IOutputer outputer, String[] args) throws Exception {
		if (args.length != 2) {
			throw new SyntaxError();
		}
		VmLocation vmLocation = new VmLocation(args[0]);
		int unitNumber = Integer.parseInt(args[1]);
		
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(vmLocation.getEsxName());
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, vmLocation.getVmName());
		
		VmDisk disk = null;
		for(VmDisk vmDisk : vmInfo.getDisks()) {
			if (vmDisk.getUnitNumber() == unitNumber) {
				disk = vmDisk;
			}
		}
		
		if (disk == null) {
			throw new RuntimeException("Disk unitNumber " + unitNumber + " not found");
		}
		
		outputer.log("Removing disk unitNumber " + unitNumber + ", file " + disk.getFileName() + " on virtual machine " + vmInfo.getName());
		
		VirtualMachineConfigSpec configSpec = new VirtualMachineConfigSpec();
		VirtualDisk vd = new VirtualDisk();
		vd.setKey(disk.getKey());
			
		VirtualDeviceConfigSpec specRemove = new VirtualDeviceConfigSpec();
		specRemove.setDevice(vd);
		specRemove.setOperation(VirtualDeviceConfigSpecOperation.REMOVE);
		configSpec.getDeviceChange().add(specRemove);
		
		ManagedObjectReference task1 = vimServiceUtil.getService().reconfigVMTask(vmInfo.getManagedObjectReference(), configSpec);
		if (!PropertiesService.waitForTaskEnd(vimServiceUtil, task1)) {
			throw new RuntimeException("Unable to detach disk from vm");
		}
		
		ManagedObjectReference task2 = vimServiceUtil.getService().deleteVirtualDiskTask(vimServiceUtil.getServiceContent().getVirtualDiskManager(), disk.getFileName(), null);
		outputer.result(PropertiesService.waitForTaskEnd(vimServiceUtil, task2));
	}

	public String getSyntax() {
		return "esx_name:vm_name unit number";
	}

	public String getHelp() {
		return "remove a virtual disk to virtual machine";
	}

	public String getCommand() {
		return "remove_disk";
	}

	public Target getTarget() {
		return Target.ESX;
	}

}
