package com.octo.vmware.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vim25.FileBackedVirtualDiskSpec;
import vim25.ManagedObjectReference;
import vim25.VirtualDeviceConfigSpec;
import vim25.VirtualDeviceConfigSpecOperation;
import vim25.VirtualDisk;
import vim25.VirtualDiskAdapterType;
import vim25.VirtualDiskFlatVer2BackingInfo;
import vim25.VirtualDiskMode;
import vim25.VirtualDiskType;
import vim25.VirtualMachineConfigSpec;

import com.octo.vmware.ICommand;
import com.octo.vmware.SizeFormatter;
import com.octo.vmware.entities.VmDisk;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.services.PropertiesService;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class AddDisk implements ICommand {

	public void execute(IOutputer outputer, String[] args) throws Exception {
		if (args.length != 2) {
			throw new SyntaxError();
		}
		VmLocation vmLocation = new VmLocation(args[0]);
		long sizeKb = Long.parseLong(args[1]) * 1024 * 1024;
		
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(vmLocation.getEsxName());
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, vmLocation.getVmName());
		
		int controllerKey = vmInfo.getDisks().get(0).getControllerKey();
		int unitNumber = -1;
		for(VmDisk vmDisk : vmInfo.getDisks()) {
			if (vmDisk.getUnitNumber() > unitNumber) {
				unitNumber = vmDisk.getUnitNumber();
			}
		}
		unitNumber ++;
		
		String diskUrl = computeNewDiskUrl(vmInfo.getDisks().get(0).getFileName(), unitNumber);
		
		outputer.log("Creating disk " + diskUrl + ", size " + SizeFormatter.formatSizeKb(sizeKb) + " GB, unitNumber " + unitNumber + " on virtual machine " + vmInfo.getName());
		
		FileBackedVirtualDiskSpec diskSpec = new FileBackedVirtualDiskSpec();
		diskSpec.setCapacityKb(sizeKb);
		diskSpec.setAdapterType(VirtualDiskAdapterType.LSI_LOGIC.value());
		diskSpec.setDiskType(VirtualDiskType.THICK.value());
		
		ManagedObjectReference task1 = vimServiceUtil.getService().createVirtualDiskTask(vimServiceUtil.getServiceContent().getVirtualDiskManager(), diskUrl, null, diskSpec);
		if (!PropertiesService.waitForTaskEnd(vimServiceUtil, task1)) {
			throw new RuntimeException("Unable to create disk");
		}
	
		VirtualMachineConfigSpec configSpec = new VirtualMachineConfigSpec();
		VirtualDisk disk = new VirtualDisk();
		disk.setCapacityInKB(sizeKb);
		disk.setControllerKey(controllerKey);
		disk.setUnitNumber(unitNumber);
		
		VirtualDiskFlatVer2BackingInfo backingInfo = new VirtualDiskFlatVer2BackingInfo();
		backingInfo.setThinProvisioned(false);
		backingInfo.setDiskMode(VirtualDiskMode.PERSISTENT.value());
		backingInfo.setFileName(diskUrl);
		disk.setBacking(backingInfo);
			
		VirtualDeviceConfigSpec specAdd = new VirtualDeviceConfigSpec();
		specAdd.setDevice(disk);
		specAdd.setOperation(VirtualDeviceConfigSpecOperation.ADD);
		configSpec.getDeviceChange().add(specAdd);
		
		ManagedObjectReference task2 = vimServiceUtil.getService().reconfigVMTask(vmInfo.getManagedObjectReference(), configSpec);
		outputer.result(PropertiesService.waitForTaskEnd(vimServiceUtil, task2));
	}
	
	public static String computeNewDiskUrl(String fileName, int unitNumber) {
		Matcher matcher = Pattern.compile("^(\\[[^\\]]+\\] [^_]*)_?\\d?\\.vmdk$").matcher(fileName);
		if (matcher.matches()) {
			return matcher.group(1) + "_" + unitNumber + ".vmdk";
		}
		else {
			return null;
		}
	}

	public String getSyntax() {
		return "esx_name:vm_name size_in_GB";
	}

	public String getHelp() {
		return "add a virtual disk to virtual machine";
	}

	public String getCommand() {
		return "add_disk";
	}

	public Target getTarget() {
		return Target.ESX;
	}

}
