package com.octo.vmware.commands;

import vim25.ManagedObjectReference;
import vim25.VirtualDeviceConfigSpec;
import vim25.VirtualDeviceConfigSpecOperation;
import vim25.VirtualEthernetCard;
import vim25.VirtualEthernetCardNetworkBackingInfo;
import vim25.VirtualMachineConfigSpec;
import vim25.VirtualVmxnet3;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VMNetwork;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.services.PropertiesService;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class ReconfigureNetworkCards implements ICommand {

	public void execute(String[] args) throws Exception {
		if (args.length != 1) {
			throw new SyntaxError();
		}
		VmLocation vmLocation = new VmLocation(args[0]);
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(vmLocation.getEsxName());
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, vmLocation.getVmName());
		VirtualMachineConfigSpec configSpec = new VirtualMachineConfigSpec();
		for(VMNetwork network : vmInfo.getNetworks()) {
			VirtualEthernetCard card = new VirtualEthernetCard();
			card.setKey(network.getKey());
			
			VirtualDeviceConfigSpec specRemove = new VirtualDeviceConfigSpec();
			specRemove.setDevice(card);
			specRemove.setOperation(VirtualDeviceConfigSpecOperation.REMOVE);
			configSpec.getDeviceChange().add(specRemove);
			
			VirtualEthernetCard newCard = new VirtualVmxnet3();
			VirtualEthernetCardNetworkBackingInfo backingInfo = new VirtualEthernetCardNetworkBackingInfo();
			backingInfo.setDeviceName("Intranet");
			newCard.setBacking(backingInfo);
			VirtualDeviceConfigSpec specAdd = new VirtualDeviceConfigSpec();
			specAdd.setDevice(newCard);
			specAdd.setOperation(VirtualDeviceConfigSpecOperation.ADD);
			configSpec.getDeviceChange().add(specAdd);
			
			System.out.println("Reconfigure card on network " + network.getNetworkName() + " [" + network.getType() + "] to " +  newCard.getClass().getSimpleName() + " on virtual machine " + vmInfo.getName());			
		}
		ManagedObjectReference task = vimServiceUtil.getService().reconfigVMTask(vmInfo.getManagedObjectReference(), configSpec);
		System.out.println("Result : " + (PropertiesService.waitForTaskEnd(vimServiceUtil, task) ? "OK" : "Error"));
	}

	public String getSyntax() {
		return "esx_name:vm_name"; 
	}

	public String getHelp() {
		return "reconfigure network cards";
	}

	public String getCommand() {
		return "reconfigure_network_cards";
	}

	public Target getTarget() {
		return Target.ESX;
	}
	
}
