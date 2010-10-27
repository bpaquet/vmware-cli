package com.octo.vmware.commands;

import vim2.VirtualMachinePowerState;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.ConverterTask;
import com.octo.vmware.entities.EsxServer;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.services.ConverterTasksListService;
import com.octo.vmware.services.VmsListService;
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
import converter.ConverterTaskInfo;
import converter.ConverterTaskSpec;
import converter.ConverterVimConnectionSpec;
import converter.ConverterVimConnectionSpecLoginVimCredentials;

public class CopyVm implements ICommand {

	public void execute(String[] args) throws Exception {
		if (args.length != 2) {
			throw new SyntaxError();
		}
		System.out.println("Checking copy params");
		
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
		System.out.println("To : " + vmLocationDest.getVmName() + " on esx server " + vmLocationDest.getEsxName());
		System.out.println("Launching copy");
		
		ConverterComputerSpecManagedVmLocation from = new ConverterComputerSpecManagedVmLocation();
		from.setVimConnect(makeConverterVimConnectionSpec(vimServiceUtilFrom.getEsxServer()));
		from.setUuid(vmFrom.getUuid());
		from.setVmName(vmFrom.getName());
		
		ConverterComputerSpec computerSpec = new ConverterComputerSpec();
		computerSpec.setLocation(from);
		
		ConverterTargetVmSpecManagedVmLocation to = new ConverterTargetVmSpecManagedVmLocation();
		to.setVimConnect(makeConverterVimConnectionSpec(vimServiceUtilDest.getEsxServer()));
	
		ConverterTargetVmSpec converterTargetVmSpec = new ConverterTargetVmSpec();
		converterTargetVmSpec.setLocation(to);
		converterTargetVmSpec.setName(vmLocationDest.getVmName());
		
		ConverterCloningParams cloningParams = new ConverterCloningParams();
		cloningParams.setTarget(converterTargetVmSpec);
	
		ConverterStorageParams storageParams = new ConverterStorageParams();
		storageParams.setCloningMode(ConverterStorageParamsCloningMode.VOLUME_BASED_CLONING.value());
		cloningParams.setStorageParams(storageParams);

		ConverterConversionParams conversionParams = new ConverterConversionParams();
		conversionParams.setDoClone(true);
		conversionParams.setCloningParams(cloningParams);
		
		ConverterTaskSpec converterTaskSpec = new ConverterTaskSpec();
		converterTaskSpec.setSource(computerSpec);
		converterTaskSpec.setConversionParams(conversionParams);
		
		ConverterTaskInfo converterTaskInfo = converterServiceUtil.getService().converterCreateTask(converterServiceUtil.getServiceContent().getTaskManager(), converterTaskSpec);
		
		String id = converterTaskInfo.getTask().getValue().substring(5);
		System.out.println("Task created, id " +  id);
		waitForEnd(converterServiceUtil, id);
	}
	
	private void waitForEnd(ConverterServiceUtil converterServiceUtil, String id) throws Exception {
		while(true) {
			Thread.sleep(10000);
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
