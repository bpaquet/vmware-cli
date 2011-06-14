package com.octo.vmware.entities;

import java.util.List;

import vim25.ManagedObjectReference;

public class VmInfo {

	private VmLocation vmLocation;

	private String path;

	private String name;
	private String uuid;

	private int ram;
	private int cpu;

	private String status;

	private String guestHostname;
	private String guestIp;
	private String guestFullName;
	private String guestToolsStatus;
	private String guestId;

	private List<String> datastores;
	private List<VmNetwork> networks;
	private List<VmDisk> disks;

	private ManagedObjectReference managedObjectReference;

	private ResourcePool resourcePool;

	private String autoStart;
	
	public VmLocation getVmLocation() {
		return vmLocation;
	}

	public void setVmLocation(VmLocation vmLocation) {
		this.vmLocation = vmLocation;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getRam() {
		return ram;
	}

	public void setRam(int ram) {
		this.ram = ram;
	}

	public int getCpu() {
		return cpu;
	}

	public void setCpu(int cpu) {
		this.cpu = cpu;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGuestHostname() {
		return guestHostname;
	}

	public void setGuestHostname(String guestHostname) {
		this.guestHostname = guestHostname;
	}

	public String getGuestIp() {
		return guestIp;
	}

	public void setGuestIp(String guestIp) {
		this.guestIp = guestIp;
	}

	public String getGuestFullName() {
		return guestFullName;
	}

	public void setGuestFullName(String guestFullName) {
		this.guestFullName = guestFullName;
	}

	public void setGuestId(String guestId) {
		this.guestId = guestId;
	}

	public String getGuestId() {
		return guestId;
	}

	public String getGuestToolsStatus() {
		return guestToolsStatus;
	}

	public void setGuestToolsStatus(String guestToolsStatus) {
		this.guestToolsStatus = guestToolsStatus;
	}

	public List<String> getDatastores() {
		return datastores;
	}

	public void setDatastores(List<String> datastores) {
		this.datastores = datastores;
	}

	public List<VmNetwork> getNetworks() {
		return networks;
	}

	public void setNetworks(List<VmNetwork> networks) {
		this.networks = networks;
	}

	public List<VmDisk> getDisks() {
		return disks;
	}

	public void setDisks(List<VmDisk> disks) {
		this.disks = disks;
	}

	public ManagedObjectReference getManagedObjectReference() {
		return managedObjectReference;
	}

	public void setManagedObjectReference(ManagedObjectReference managedObjectReference) {
		this.managedObjectReference = managedObjectReference;
	}

	public ResourcePool getResourcePool() {
		return resourcePool;
	}

	public void setResourcePool(ResourcePool resourcePool) {
		this.resourcePool = resourcePool;
	}

	public String getAutoStart() {
		return autoStart;
	}

	public void setAutoStart(String autoStart) {
		this.autoStart = autoStart;
	}
	
}
