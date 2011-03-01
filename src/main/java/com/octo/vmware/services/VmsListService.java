package com.octo.vmware.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vim25.DynamicProperty;
import vim25.GuestInfo;
import vim25.ManagedObjectReference;
import vim25.ObjectContent;
import vim25.ObjectSpec;
import vim25.PropertyFilterSpec;
import vim25.PropertySpec;
import vim25.TraversalSpec;
import vim25.VirtualDevice;
import vim25.VirtualDisk;
import vim25.VirtualDiskFlatVer2BackingInfo;
import vim25.VirtualEthernetCard;
import vim25.VirtualEthernetCardNetworkBackingInfo;
import vim25.VirtualMachineConfigInfo;
import vim25.VirtualMachineConfigInfoDatastoreUrlPair;
import vim25.VirtualMachineRuntimeInfo;

import com.octo.vmware.entities.ResourcePool;
import com.octo.vmware.entities.VMNetwork;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.utils.TraversalSpecHelper;
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
		TraversalSpec dataCenterToVMFolder = TraversalSpecHelper.makeTraversalSpec("Datacenter", "vmFolder", "DataCenterToVMFolder", false, new String[]{"VisitFolders"}, new TraversalSpec[]{});
		TraversalSpec traversalSpec = TraversalSpecHelper.makeTraversalSpec("Folder", "childEntity", "VisitFolders", false, new String[]{}, new TraversalSpec[]{dataCenterToVMFolder});
		
		ObjectSpec objectSpec = new ObjectSpec();
		objectSpec.setObj(vimServiceUtil.getServiceContent().getRootFolder());
		objectSpec.setSkip(true);
		objectSpec.getSelectSet().add(traversalSpec);
		
		PropertySpec propertySpec = new PropertySpec();
		propertySpec.getPathSet().add("name");
		propertySpec.getPathSet().add("config");
		propertySpec.getPathSet().add("guest");
		propertySpec.getPathSet().add("runtime");
		propertySpec.getPathSet().add("resourcePool");
		propertySpec.setAll(false);
		propertySpec.setType("VirtualMachine");

		// Finally retrieve VirtualMachines in datacenter
		PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
		propertyFilterSpec.getObjectSet().add(objectSpec);
		propertyFilterSpec.getPropSet().add(propertySpec);
		List<PropertyFilterSpec> propertyFilterSpecsList = Arrays.asList(propertyFilterSpec);
		List<ObjectContent> vms = vimServiceUtil.getService().retrieveProperties(
				vimServiceUtil.getServiceContent().getPropertyCollector(), propertyFilterSpecsList);

		// Build return list
		List<VmInfo> list = new ArrayList<VmInfo>();
		for (ObjectContent obj : vms) {
			VmInfo vmInfo = new VmInfo();
			vmInfo.setManagedObjectReference(obj.getObj());
			for (DynamicProperty prop : obj.getPropSet()) {
				if (prop.getName().equals("name")) {
					vmInfo.setName(prop.getVal().toString());
				}
				else if (prop.getName().equals("resourcePool")) {
					ManagedObjectReference managedObjectReference = (ManagedObjectReference) prop.getVal();
					ResourcePool resourcePool = ResourcePoolService.searchPoolName(vimServiceUtil, managedObjectReference);
					vmInfo.setResourcePool(resourcePool);
				}
				else if (prop.getName().equals("runtime")) {
					VirtualMachineRuntimeInfo runtimeInfo = (VirtualMachineRuntimeInfo) prop.getVal();
					vmInfo.setStatus(runtimeInfo.getPowerState().toString());
				}
				else if (prop.getName().equals("config")) {
					VirtualMachineConfigInfo configInfo = (VirtualMachineConfigInfo) prop.getVal();
					vmInfo.setPath(configInfo.getFiles().getVmPathName());
					vmInfo.setUuid(configInfo.getUuid());
					List<String> datastores = new ArrayList<String>();
					for(VirtualMachineConfigInfoDatastoreUrlPair datastoreUrlPair : configInfo.getDatastoreUrl()) {
						datastores.add(datastoreUrlPair.getName());
					}
					vmInfo.setDatastores(datastores);
					vmInfo.setRam(configInfo.getHardware().getMemoryMB());
					vmInfo.setCpu(configInfo.getHardware().getNumCPU());
					
					List<VMNetwork> networks = new ArrayList<VMNetwork>();
					List<String> disks = new ArrayList<String>();
					for(VirtualDevice vd : configInfo.getHardware().getDevice()) {
						if (vd instanceof VirtualEthernetCard) {
							VirtualEthernetCardNetworkBackingInfo cardNetworkBackingInfo = (VirtualEthernetCardNetworkBackingInfo) vd.getBacking();
							VMNetwork network = new VMNetwork();
							network.setNetworkName(cardNetworkBackingInfo.getDeviceName());
							network.setType(vd.getClass().getSimpleName());
							network.setKey(vd.getKey());
							networks.add(network);
						}
						if (vd instanceof VirtualDisk) {
							if (vd.getBacking() instanceof VirtualDiskFlatVer2BackingInfo) {
								VirtualDiskFlatVer2BackingInfo virtualDiskFlatVer2BackingInfo = (VirtualDiskFlatVer2BackingInfo) vd.getBacking();
								disks.add(virtualDiskFlatVer2BackingInfo.getFileName());
							}
							else {
								disks.add(vd.getBacking().getClass().getSimpleName());
							}
						}
					}
					vmInfo.setNetworks(networks);
					vmInfo.setDisks(disks);
				}
				else if (prop.getName().equals("guest")) {
					GuestInfo guestInfo = (GuestInfo) prop.getVal();
					vmInfo.setGuestId(guestInfo.getGuestId());
					vmInfo.setGuestHostname(guestInfo.getHostName());
					vmInfo.setGuestIp(guestInfo.getIpAddress());
					vmInfo.setGuestFullName(guestInfo.getGuestFullName());
					vmInfo.setGuestToolsStatus(guestInfo.getToolsStatus().toString());
				}
			}
			list.add(vmInfo);
		}
		
		VmsListCache.set(vimServiceUtil.getEsxServer().getName(), list);
		
		return list;
	}
}
