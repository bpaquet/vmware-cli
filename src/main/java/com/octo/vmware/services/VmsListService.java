package com.octo.vmware.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vim2.DynamicProperty;
import vim2.GuestInfo;
import vim2.ObjectContent;
import vim2.ObjectSpec;
import vim2.PropertyFilterSpec;
import vim2.PropertySpec;
import vim2.SelectionSpec;
import vim2.TraversalSpec;
import vim2.VirtualDevice;
import vim2.VirtualEthernetCard;
import vim2.VirtualEthernetCardNetworkBackingInfo;
import vim2.VirtualMachineConfigInfo;
import vim2.VirtualMachineConfigInfoDatastoreUrlPair;
import vim2.VirtualMachineRuntimeInfo;

import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.utils.VimServiceUtil;

public class VmsListService {

	public static VmInfo findVmByName(VimServiceUtil vimServiceUtil, String vmName) throws Exception {
		for(VmInfo vmInfo : getVmsList(vimServiceUtil)) {
			if (vmInfo.getName().equals(vmName)) {
				return vmInfo;
			}
		}
		throw new RuntimeException("Vm not found : " + vmName);
	}
	
	public static List<VmInfo> getVmsList(VimServiceUtil vimServiceUtil) throws Exception {
		// Build search path
		// Traversal to get to the vmFolder from DataCenter
 		TraversalSpec dataCenterToVMFolder = new TraversalSpec();
		dataCenterToVMFolder.setType("Datacenter");
		dataCenterToVMFolder.setPath("vmFolder");
		dataCenterToVMFolder.setSkip(false);
		SelectionSpec ss = new SelectionSpec();
		ss.setName("VisitFolders");
		dataCenterToVMFolder.getSelectSet().add(ss);
		dataCenterToVMFolder.setName("DataCenterToVMFolder");
		// TraversalSpec to get to the DataCenter from rootFolder
		TraversalSpec traversalSpec = new TraversalSpec();
		traversalSpec.setType("Folder");
		traversalSpec.setPath("childEntity");
		traversalSpec.setSkip(false);
		traversalSpec.getSelectSet().add(dataCenterToVMFolder);
		traversalSpec.setName("VisitFolders");

		// Set root folder
		ObjectSpec objectSpec = new ObjectSpec();
		objectSpec.setObj(vimServiceUtil.getServiceContent().getRootFolder());
		objectSpec.setSkip(true);
		objectSpec.getSelectSet().add(traversalSpec);
		// Specifies objects to retrieve are VirtualMachines
		PropertySpec propertySpec = new PropertySpec();
		propertySpec.getPathSet().add("name");
		propertySpec.getPathSet().add("config");
		propertySpec.getPathSet().add("guest");
		propertySpec.getPathSet().add("runtime");
		propertySpec.setAll(false);
		propertySpec.setType("VirtualMachine");

		// Finally retrieve VirtualMachines in datacenter
		PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
		propertyFilterSpec.getObjectSet().add(objectSpec);
		propertyFilterSpec.getPropSet().add(propertySpec);
		List<PropertyFilterSpec> propertyFilterSpecsList = Arrays.asList(propertyFilterSpec);
		List<ObjectContent> vms = vimServiceUtil.getService().retrieveProperties(
				vimServiceUtil.getServiceContent().getPropertyCollector(), propertyFilterSpecsList);

		// TODO: filter by runtime.powerstate ?

		// Build return list
		List<VmInfo> list = new ArrayList<VmInfo>();
		for (ObjectContent obj : vms) {
			VmInfo vmInfo = new VmInfo();
			vmInfo.setManagedObjectReference(obj.getObj());
			for (DynamicProperty prop : obj.getPropSet()) {
				if (prop.getName().equals("name")) {
					vmInfo.setName(prop.getVal().toString());
				}
				else if (prop.getName().equals("runtime")) {
					VirtualMachineRuntimeInfo runtimeInfo = (VirtualMachineRuntimeInfo) prop.getVal();
					vmInfo.setStatus(runtimeInfo.getPowerState().toString());
				}
				else if (prop.getName().equals("config")) {
					VirtualMachineConfigInfo configInfo = (VirtualMachineConfigInfo) prop.getVal();
					vmInfo.setUuid(configInfo.getUuid());
					List<String> datastores = new ArrayList<String>();
					for(VirtualMachineConfigInfoDatastoreUrlPair datastoreUrlPair : configInfo.getDatastoreUrl()) {
						datastores.add(datastoreUrlPair.getName());
					}
					vmInfo.setDatastores(datastores);
					vmInfo.setRam(configInfo.getHardware().getMemoryMB());
					vmInfo.setCpu(configInfo.getHardware().getNumCPU());
					
					List<String> networks = new ArrayList<String>();
					for(VirtualDevice vd : configInfo.getHardware().getDevice()) {
						if (vd instanceof VirtualEthernetCard) {
							VirtualEthernetCardNetworkBackingInfo cardNetworkBackingInfo = (VirtualEthernetCardNetworkBackingInfo) vd.getBacking();
							networks.add(cardNetworkBackingInfo.getDeviceName());
						}
					}
					vmInfo.setNetworks(networks);
				}
				else if (prop.getName().equals("guest")) {
					GuestInfo guestInfo = (GuestInfo) prop.getVal();
					vmInfo.setGuestHostname(guestInfo.getHostName());
					vmInfo.setGuestIp(guestInfo.getIpAddress());
					vmInfo.setGuestFullName(guestInfo.getGuestFullName());
					vmInfo.setGuestToolsStatus(guestInfo.getToolsStatus().toString());
				}
			}
			list.add(vmInfo);
		}
		return list;
	}
}
