package com.octo.vmware.services;
/*
import java.util.ArrayList;
import java.util.List;

import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.services.Configuration.EsxServer;
import com.octo.vmware.utils.ConverterServiceUtil;
import com.octo.vmware.utils.VimServiceUtil;

import converter.ConverterCloningParams;
import converter.ConverterComputerSpec;
import converter.ConverterComputerSpecManagedVmLocation;
import converter.ConverterConversionParams;
import converter.ConverterStorageParams;
import converter.ConverterStorageParamsCloningMode;
import converter.ConverterTargetVmSpec;
import converter.ConverterTargetVmSpecManagedVmLocation;
import converter.ConverterTaskSpec;
import converter.ConverterVimConnectionSpec;
import converter.ConverterVimConnectionSpecLoginVimCredentials;
*/
public class VmCloningService {
/*
	private ConverterServiceUtil converterServiceUtil;
	private VimServiceUtil vimServiceUtil;

	public VmCloningService() throws Exception {
		converterServiceUtil = new ConverterServiceUtil();
	}

	public void cloneVm(String sourceHost, String sourceVmName, String targetHost, String targetVmName)
			throws Exception {

		// Initialize vim web service
		vimServiceUtil = new VimServiceUtil();
		EsxServer bradConf = Configuration.getCurrent().getEsxServer(Configuration.ESX_NAMES.BRAD);
		vimServiceUtil.initializeService(bradConf.getUrl(), bradConf.getUsr(), bradConf.getPwd());

		// Get uuid from vmName
		String sourceUuid = getUuidByName(sourceVmName);

		// Finally create task
		createTask(sourceHost, sourceUuid, targetHost, targetVmName);
	}

	private String getUuidByName(String vmName) throws Exception {
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
		propertySpec.getPathSet().add("config.uuid");
		propertySpec.setAll(false);
		propertySpec.setType("VirtualMachine");

		// Finally retrieve VirtualMachines in datacenter
		PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
		propertyFilterSpec.getObjectSet().add(objectSpec);
		propertyFilterSpec.getPropSet().add(propertySpec);
		List<PropertyFilterSpec> propertyFilterSpecsList = new ArrayList<PropertyFilterSpec>();
		propertyFilterSpecsList.add(propertyFilterSpec);
		List<ObjectContent> vms = vimServiceUtil.getService().retrieveProperties(
				vimServiceUtil.getServiceContent().getPropertyCollector(), propertyFilterSpecsList);

		// Parse vms list
		for (ObjectContent obj : vms) {
			VmInfo vmInfo = new VmInfo();
			for (DynamicProperty prop : obj.getPropSet()) {
				if (prop.getName().equals("name"))
					vmInfo.setName(prop.getVal().toString());
				else if (prop.getName().equals("config.uuid"))
					vmInfo.setUuid(prop.getVal().toString());
			}
			if (vmInfo.getName().equals(vmName))
				return vmInfo.getUuid();
		}
		return null;
	}

	private void createTask(String sourceHost, String sourceUuid, String targetHost, String targetVmName)
			throws Exception {
		ConverterTaskSpec taskSpec = new ConverterTaskSpec();

		// Define source Vm
		ConverterComputerSpec sourceComputer = buildComputerSpec(sourceHost, sourceUuid, null);
		taskSpec.setSource(sourceComputer);

		// Define target Vm credentials
		ConverterVimConnectionSpecLoginVimCredentials credentials = new ConverterVimConnectionSpecLoginVimCredentials();
		Configuration.EsxServer confBrad = Configuration.getCurrent().getEsxServer(Configuration.ESX_NAMES.BRAD);
		credentials.setUsername(confBrad.getUsr());
		credentials.setPassword(confBrad.getPwd());
		// Define target Vm connection
		ConverterVimConnectionSpec connection = new ConverterVimConnectionSpec();
		connection.setHostname(targetHost);
		connection.setCredentials(credentials);
		// Define target Vm location
		ConverterTargetVmSpecManagedVmLocation targetLocation = new ConverterTargetVmSpecManagedVmLocation();
		targetLocation.setVimConnect(connection);
		// Finally define target Vm (for cloning params)
		ConverterTargetVmSpec spec = new ConverterTargetVmSpec();
		// TODO override source vmName if defined as parameter
		spec.setName(targetVmName);
		spec.setLocation(targetLocation);

		// Set cloning params (including target Vm)
		ConverterCloningParams cloningParams = new ConverterCloningParams();
		cloningParams.setTarget(spec);
		// StorageParameters
		ConverterStorageParams storageParams = new ConverterStorageParams();
		storageParams.setCloningMode(ConverterStorageParamsCloningMode.VOLUME_BASED_CLONING.value());
		cloningParams.setStorageParams(storageParams);

		// Set conversion params
		ConverterConversionParams conversionParams = new ConverterConversionParams();
		conversionParams.setDoClone(true);
		conversionParams.setCloningParams(cloningParams);
		taskSpec.setConversionParams(conversionParams);

		// Finally move vm from source to target
		converterServiceUtil.getService().converterCreateTask(
				converterServiceUtil.getServiceContent().getTaskManager(), taskSpec);
	}

	private ConverterComputerSpec buildComputerSpec(String host, String uuid, String name) {
		// Define source credentials
		ConverterVimConnectionSpecLoginVimCredentials credentials = new ConverterVimConnectionSpecLoginVimCredentials();
		Configuration.EsxServer confBrad = Configuration.getCurrent().getEsxServer(Configuration.ESX_NAMES.BRAD);
		credentials.setUsername(confBrad.getUsr()); // TODO: get from properties
		credentials.setPassword(confBrad.getPwd()); // TODO: get from properties
		// Define source connection
		ConverterVimConnectionSpec connection = new ConverterVimConnectionSpec();
		connection.setHostname(host);
		connection.setCredentials(credentials);
		// Define source vm
		ConverterComputerSpecManagedVmLocation location = new ConverterComputerSpecManagedVmLocation();
		location.setVimConnect(connection);
		if (uuid != null)
			location.setUuid(uuid);
		if (name != null)
			location.setVmName(name);
		// Define source computer
		ConverterComputerSpec computerSpec = new ConverterComputerSpec();
		computerSpec.setLocation(location);
		// Return ConverterComputerSpec
		return computerSpec;
	}
*/
}
