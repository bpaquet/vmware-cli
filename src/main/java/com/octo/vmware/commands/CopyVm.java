package com.octo.vmware.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import vim25.ManagedObjectReference;
import vim25.VirtualMachinePowerState;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.ConverterTask;
import com.octo.vmware.entities.EsxServer;
import com.octo.vmware.entities.ResourcePool;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.services.ConverterTasksListService;
import com.octo.vmware.services.ResourcePoolService;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.ConverterServiceUtil;
import com.octo.vmware.utils.VimServiceUtil;

import converter.ConverterCloningParams;
import converter.ConverterComputerSpec;
import converter.ConverterComputerSpecManagedVmLocation;
import converter.ConverterConversionParams;
import converter.ConverterNetworkParams;
import converter.ConverterNetworkParamsManagedNicParams;
import converter.ConverterServerConversionConversionJobInfo;
import converter.ConverterServerConversionConversionJobSpec;
import converter.ConverterStorageParams;
import converter.ConverterStorageParamsCloningMode;
import converter.ConverterStorageParamsDiskControllerType;
import converter.ConverterTargetVmSpec;
import converter.ConverterTargetVmSpecManagedVmLocation;
import converter.ConverterVimConnectionSpec;
import converter.ConverterVimConnectionSpecLoginVimCredentials;

public class CopyVm implements ICommand {

	private static final String NETWORK = "network";
	private static final String RESOURCE_POOL = "resource_pool";

	public void execute(String[] args) throws Exception {
		if (args.length < 2) {
			throw new SyntaxError();
		}
		
		Map<String, String> opts = new HashMap<String, String>();
		for(int i = 2; i < args.length; i ++) {
			String [] splitted = args[i].split(":");
			if (splitted.length != 2) {
				throw new RuntimeException("Wrong option syntax : " + args[i]);
			}
			opts.put(splitted[0], splitted[1]);
		}
		System.out.println("Build copy task");
		
		ConverterServiceUtil converterServiceUtil = ConverterServiceUtil.getConverter();
		
		VmLocation vmLocationFrom = new VmLocation(args[0]);
		VimServiceUtil vimServiceUtilFrom = VimServiceUtil.get(vmLocationFrom.getEsxName());
		VmInfo vmFrom = VmsListService.findVmByName(vimServiceUtilFrom, vmLocationFrom.getVmName());
		if (!VirtualMachinePowerState.POWERED_OFF.toString().equals(vmFrom.getStatus())) {
			throw new RuntimeException("Virtual machine " + vmFrom.getName() + " in bad state : " + vmFrom.getStatus());
		}
		System.out.println("From : " + vmFrom.getName() + " on esx server " + vmLocationFrom.getEsxName());
		
		VmLocation vmLocationDest = new VmLocation(args[1]);
		VimServiceUtil vimServiceUtilDest = VimServiceUtil.get(vmLocationDest.getEsxName());
		for(VmInfo vmInfo : VmsListService.getVmsList(vimServiceUtilDest)) {
			if (vmInfo.getName().equals(vmLocationDest.getVmName())) {
				throw new RuntimeException("Virtual machine " + vmLocationDest.getVmName() + " already exist on " + vmLocationDest.getEsxName());
			}
		}
		System.out.println("To   : " + vmLocationDest.getVmName() + " on esx server " + vmLocationDest.getEsxName());
		
		ConverterComputerSpec computerSpec = makeConverterComputerSpec(vmFrom, vimServiceUtilFrom.getEsxServer());
		
		ConverterConversionParams conversionParams = makeConversionParams(vmLocationDest, vimServiceUtilDest, opts);
		
		System.out.println("Validating task");
		converterServiceUtil.getService().converterValidateParams(converterServiceUtil.getServiceContent().getQueryManager(), computerSpec, null, conversionParams);
		
		ConverterServerConversionConversionJobSpec conversionConversionJobSpec = new ConverterServerConversionConversionJobSpec();
		conversionConversionJobSpec.setSource(computerSpec);
		conversionConversionJobSpec.setConversionParams(conversionParams);
		conversionConversionJobSpec.setName("job" + UUID.randomUUID());
		
		System.out.println("Launching task");
		
		ConverterServerConversionConversionJobInfo job = converterServiceUtil.getService().converterServerConversionManagerCreateJob(converterServiceUtil.getServiceContent().getConversionManager(), conversionConversionJobSpec, null);
		Thread.sleep(5000);
		ConverterServerConversionConversionJobInfo jobAfter5000 = converterServiceUtil.getService().converterServerConversionManagerGetJobInfo(converterServiceUtil.getServiceContent().getConversionManager(), job.getJob());
		String id = jobAfter5000.getActiveTask().getValue().substring(5);
		System.out.println("Task created, id " +  id);
		waitForEnd(converterServiceUtil, id);
	}

	private ConverterConversionParams makeConversionParams(VmLocation vmLocationDest, VimServiceUtil vimServiceUtilDest, Map<String, String> opts) throws Exception {
		ConverterTargetVmSpecManagedVmLocation to = new ConverterTargetVmSpecManagedVmLocation();
		to.setVimConnect(makeConverterVimConnectionSpec(vimServiceUtilDest.getEsxServer()));
		to.setHostName(vimServiceUtilDest.getEsxServer().getHostname());
		
		if (opts.get(RESOURCE_POOL) != null) {
			ResourcePool resourcePool = ResourcePoolService.findResourcePoolByName(vimServiceUtilDest, opts.get(RESOURCE_POOL));
			System.out.println("Target resource pool " + opts.get(RESOURCE_POOL));
			to.setResourcePool(transcodeManagedObjectReference(resourcePool.getManagedObjectReference()));
			to.setResourcePoolName(opts.get(RESOURCE_POOL));
		}
		
		ConverterTargetVmSpec converterTargetVmSpec = new ConverterTargetVmSpec();
		converterTargetVmSpec.setLocation(to);
		converterTargetVmSpec.setName(vmLocationDest.getVmName());
		//converterTargetVmSpec.setHardwareVersion("vmx-07");
		
		ConverterCloningParams cloningParams = new ConverterCloningParams();
		cloningParams.setTarget(converterTargetVmSpec);
	
		ConverterStorageParams storageParams = new ConverterStorageParams();
		storageParams.setCloningMode(ConverterStorageParamsCloningMode.DISK_BASED_CLONING.value());
		storageParams.setDiskControllerType(ConverterStorageParamsDiskControllerType.KEEP_SOURCE.toString());
		cloningParams.setStorageParams(storageParams);
		
		if (opts.get(NETWORK) != null) {
			System.out.println("Target network " + opts.get(NETWORK));
			ConverterNetworkParamsManagedNicParams converterNetworkParamsManagedNicParams = new ConverterNetworkParamsManagedNicParams();
			converterNetworkParamsManagedNicParams.setConnected(true);
			converterNetworkParamsManagedNicParams.setNetworkName(opts.get(NETWORK));
			
			ConverterNetworkParams converterNetworkParams = new ConverterNetworkParams();
			converterNetworkParams.setPreserveNicCount(true);
			converterNetworkParams.setPreserveNicMapping(false);
			converterNetworkParams.getNic().add(converterNetworkParamsManagedNicParams);
			cloningParams.setNetworkParams(converterNetworkParams);
		}
		
		ConverterConversionParams conversionParams = new ConverterConversionParams();
		conversionParams.setDoClone(true);
		conversionParams.setDoReconfig(false);
		conversionParams.setDoInstallTools(false);
		conversionParams.setDoUninstallAgent(false);
		conversionParams.setPowerOnTargetVM(false);
		conversionParams.setRemoveRestoreCheckpoints(true);
	
		conversionParams.setCloningParams(cloningParams);
		return conversionParams;
	}

	private ConverterComputerSpec makeConverterComputerSpec(VmInfo vmFrom, EsxServer esxServerFrom) {
		ConverterComputerSpecManagedVmLocation from = new ConverterComputerSpecManagedVmLocation();
		from.setVimConnect(makeConverterVimConnectionSpec(esxServerFrom));
		from.setUuid(vmFrom.getUuid());
		from.setVmName(vmFrom.getName());
		from.setVm(transcodeManagedObjectReference(vmFrom.getManagedObjectReference()));
		
		ConverterComputerSpec computerSpec = new ConverterComputerSpec();
		computerSpec.setLocation(from);
		return computerSpec;
	}
	
	private void waitForEnd(ConverterServiceUtil converterServiceUtil, String id) throws Exception {
		while(true) {
			Thread.sleep(30000);
			ConverterTask converterTask = ConverterTasksListService.getTask(converterServiceUtil, id);
			if (converterTask.isFinished()) {
				System.out.println("Result " + converterTask.getStatus());
				if (!converterTask.isSuccess()) {
					throw new RuntimeException("Copy error");
				}
				return;
			}
			System.out.println("Progression : " + converterTask.getProgress() + "%");
		}
	}

	private ConverterVimConnectionSpec makeConverterVimConnectionSpec(EsxServer esxServer) {
		ConverterVimConnectionSpec converterVimConnectionSpec = new ConverterVimConnectionSpec();
		converterVimConnectionSpec.setHostname(esxServer.getHostname());
		ConverterVimConnectionSpecLoginVimCredentials credentials = new ConverterVimConnectionSpecLoginVimCredentials();
		credentials.setUsername(esxServer.getUsername());
		credentials.setPassword(esxServer.getPassword());
		converterVimConnectionSpec.setCredentials(credentials);
		return converterVimConnectionSpec;
	}
	
	private ManagedObjectReference transcodeManagedObjectReference(vim25.ManagedObjectReference managedObjectReference) {
		ManagedObjectReference newManagedObjectReference = new ManagedObjectReference();
		newManagedObjectReference.setType(managedObjectReference.getType());
		newManagedObjectReference.setValue(managedObjectReference.getValue());
		return newManagedObjectReference;
	}
	
	public String getCommand() {
		return "copy_vm";
	}

	public String getHelp() {
		return "copy virtual machine";
	}

	public String getSyntax() {
		return "esx_source_name:vm_source_name esx_target_name:vm_target_name";
	}

	public Target getTarget() {
		return Target.CONVERTER;
	}

}
