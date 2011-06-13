package com.octo.vmware.commands;

import vim25.ManagedObjectReference;
import vim25.VirtualDeviceConfigSpec;
import vim25.VirtualDeviceConfigSpecOperation;
import vim25.VirtualE1000;
import vim25.VirtualEthernetCard;
import vim25.VirtualEthernetCardNetworkBackingInfo;
import vim25.VirtualMachineConfigSpec;
import vim25.VirtualPCNet32;
import vim25.VirtualVmxnet2;
import vim25.VirtualVmxnet3;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.entities.VmNetwork;
import com.octo.vmware.services.PropertiesService;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class ReconfigureNetworkCards implements ICommand {

	public void execute(IOutputer outputer, String[] args) throws Exception {
		if (args.length != 2) {
			throw new SyntaxError();
		}
		VmLocation vmLocation = new VmLocation(args[0]);
		Class<?> clazz = null;
		if ("vmxnet3".equals(args[1])) {
			clazz = VirtualVmxnet3.class;
		}
		if ("vmxnet2".equals(args[1])) {
			clazz = VirtualVmxnet2.class;
		}
		if ("e1000".equals(args[1])) {
			clazz = VirtualE1000.class;
		}
		if ("pcnet32".equals(args[1])) {
			clazz = VirtualPCNet32.class;
		}
		if (clazz == null) {
			throw new SyntaxError();
		}
		
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(vmLocation.getEsxName());
		VmInfo vmInfo = VmsListService.findVmByName(vimServiceUtil, vmLocation.getVmName());
		VirtualMachineConfigSpec configSpec = new VirtualMachineConfigSpec();
		for(VmNetwork network : vmInfo.getNetworks()) {
			VirtualEthernetCard card = new VirtualEthernetCard();
			card.setKey(network.getKey());
			
			VirtualDeviceConfigSpec specRemove = new VirtualDeviceConfigSpec();
			specRemove.setDevice(card);
			specRemove.setOperation(VirtualDeviceConfigSpecOperation.REMOVE);
			configSpec.getDeviceChange().add(specRemove);
			
			VirtualEthernetCard newCard = (VirtualEthernetCard) clazz.newInstance();
			VirtualEthernetCardNetworkBackingInfo backingInfo = new VirtualEthernetCardNetworkBackingInfo();
			backingInfo.setDeviceName(network.getNetworkName());
			newCard.setBacking(backingInfo);
			VirtualDeviceConfigSpec specAdd = new VirtualDeviceConfigSpec();
			specAdd.setDevice(newCard);
			specAdd.setOperation(VirtualDeviceConfigSpecOperation.ADD);
			configSpec.getDeviceChange().add(specAdd);
			
			outputer.log("Reconfigure card on network " + network.getNetworkName() + " [" + network.getType() + "] to " +  newCard.getClass().getSimpleName() + " on virtual machine " + vmInfo.getName());			
		}
		ManagedObjectReference task = vimServiceUtil.getService().reconfigVMTask(vmInfo.getManagedObjectReference(), configSpec);
		outputer.result(PropertiesService.waitForTaskEnd(vimServiceUtil, task));
	}

	public String getSyntax() {
		return "esx_name:vm_name [vmxnet3|vmxnet2|e1000|pcnet32]"; 
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
