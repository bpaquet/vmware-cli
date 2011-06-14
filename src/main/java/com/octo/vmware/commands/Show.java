package com.octo.vmware.commands;

import com.octo.vmware.ICommand;
import com.octo.vmware.SizeFormatter;
import com.octo.vmware.entities.VmDisk;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.entities.VmNetwork;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class Show implements ICommand {

	public void execute(IOutputer outputer, String[] args) throws Exception {
		if (args.length != 1) {
			throw new SyntaxError();
		}
		VmLocation vmLocation = new VmLocation(args[0]);
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(vmLocation.getEsxName());
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, vmLocation.getVmName());
		
		outputer.output(vmInfo, vimServiceUtil, new IObjectOutputer<VmInfo>() {

			public void output(IOutputer outputer, VimServiceUtil vimServiceUtil, VmInfo vmInfo) {
				outputer.log("Virtual machine '" + vmInfo.getVmLocation().getVmName() + "' on " + vmInfo.getVmLocation().getEsxName());
				outputer.log("Status : " + vmInfo.getStatus());
				outputer.log("RAM : " + vmInfo.getRam());
				outputer.log("CPU : " + vmInfo.getCpu());
				outputer.log("Resource pool : " + vmInfo.getResourcePool().getName());
				outputer.log("VMWare tools : " + vmInfo.getGuestToolsStatus());
				outputer.log("Guest hostname : " + vmInfo.getGuestHostname());
				outputer.log("Guest ip : " + vmInfo.getGuestIp());
				outputer.log("Guest os name : " + vmInfo.getGuestFullName());
				outputer.log("Guest os id : " + vmInfo.getGuestId());
				outputer.log("UUID : " + vmInfo.getUuid());
				outputer.log("VM Path : " + vmInfo.getPath());
				String datastores = "Datastores : ";
				for(String s : vmInfo.getDatastores()) {
					datastores += s + ", ";
				}
				outputer.log(datastores);
				String disks = "Disks : ";
				for(VmDisk vmDisk : vmInfo.getDisks()) {
					disks += vmDisk.getFileName() + " [size=" + SizeFormatter.formatSizeKb(vmDisk.getSizeKb()) + ", unitNumber=" + vmDisk.getUnitNumber() + "], ";
				}
				outputer.log(disks);
				String networks = "Networks : ";
				for(VmNetwork network : vmInfo.getNetworks()) {
					networks += network.getNetworkName() + " [" + network.getType() + "], ";
				}
				outputer.log(networks);
			}
			
		});
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
