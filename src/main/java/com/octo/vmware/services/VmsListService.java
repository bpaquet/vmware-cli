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
import com.octo.vmware.entities.VmDisk;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.entities.VmNetwork;
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
		TraversalSpec traversalSpec = new TraversalSpec();
		ts.setName("taverseEntities");
		ts.setPath("view");
		ts.setSkip(false);
		ts.setType("ContainerView");

                List<String> objectTypes = Arrays.asList(new String[]{"VirtualMachine"});
		ManagedObjectReference containerView = vimServiceUtil.getService().createContainerView(
				vimServiceUtil.getServiceContent().getViewManager(),
				vimServiceUtil.getServiceContent().getRootFolder(), objectTypes, true
                        );

		ObjectSpec objectSpec = new ObjectSpec();
		objectSpec.setObj(containerView);
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

               vimServiceUtil.getService().destroyView(containerView);

		// Build return list
		List<VmInfo> list = new ArrayList<VmInfo>();
		for (ObjectContent obj : vms) {
			VmInfo vmInfo = new VmInfo();
			vmInfo.setManagedObjectReference(obj.getObj());
			for (DynamicProperty prop : obj.getPropSet()) {
				if (prop.getName().equals("name")) {
					String name = prop.getVal().toString();
					vmInfo.setName(name);
					vmInfo.setVmLocation(new VmLocation(vimServiceUtil.getEsxServer().getName(), name));
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
					
					List<VmNetwork> networks = new ArrayList<VmNetwork>();
					List<VmDisk> disks = new ArrayList<VmDisk>();
					for(VirtualDevice vd : configInfo.getHardware().getDevice()) {
						if (vd instanceof VirtualEthernetCard) {
							VirtualEthernetCardNetworkBackingInfo cardNetworkBackingInfo = (VirtualEthernetCardNetworkBackingInfo) vd.getBacking();
							VmNetwork network = new VmNetwork();
							network.setNetworkName(cardNetworkBackingInfo.getDeviceName());
							network.setType(vd.getClass().getSimpleName());
							network.setKey(vd.getKey());
							networks.add(network);
						}
						if (vd instanceof VirtualDisk) {
							VmDisk virtualDisk = new VmDisk();
							if (vd.getBacking() instanceof VirtualDiskFlatVer2BackingInfo) {
								VirtualDiskFlatVer2BackingInfo virtualDiskFlatVer2BackingInfo = (VirtualDiskFlatVer2BackingInfo) vd.getBacking();
								virtualDisk.setFileName(virtualDiskFlatVer2BackingInfo.getFileName());
							}
							else {
								virtualDisk.setFileName(vd.getBacking().getClass().getSimpleName());
							}
							virtualDisk.setControllerKey(vd.getControllerKey());
							virtualDisk.setUnitNumber(vd.getUnitNumber());
							virtualDisk.setKey(vd.getKey());
							virtualDisk.setSizeKb(((VirtualDisk) vd).getCapacityInKB());
							disks.add(virtualDisk);
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
