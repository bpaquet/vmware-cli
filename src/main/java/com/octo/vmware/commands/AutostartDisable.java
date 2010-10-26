package com.octo.vmware.commands;

import java.util.List;

import vim2.AutoStartPowerInfo;
import vim2.HostAutoStartManagerConfig;
import vim2.HostConfigInfo;
import vim2.HostConfigManager;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.services.HostConfigService;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class AutostartDisable implements ICommand {

	public void execute(String[] args) throws Exception {
		if (args.length != 1) {
			throw new SyntaxError();
		}
		VmLocation vmLocation = new VmLocation(args[0]);
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(vmLocation.getEsxName());
		List<VmInfo> vmInfos = VmsListService.getVmsList(vimServiceUtil);
		HostConfigInfo hostConfigInfo = HostConfigService.getHostConfig(vimServiceUtil, "config");
		HostConfigManager hostConfigManager = HostConfigService.getHostConfig(vimServiceUtil, "configManager");
		HostAutoStartManagerConfig hostAutoStartManagerConfig = hostConfigInfo.getAutoStart();
		AutoStartPowerInfo a = null;
		for(VmInfo vmInfo : vmInfos) {
			if (vmInfo.getName().equals(vmLocation.getVmName())) {
				for(AutoStartPowerInfo autoStartPowerInfo : hostAutoStartManagerConfig.getPowerInfo()) {
					if (vmInfo.getManagedObjectReference().getValue().equals(autoStartPowerInfo.getKey().getValue())) {
						a = autoStartPowerInfo;
					}
				}
			}
		}
		if (a == null) {
			throw new RuntimeException("Virtual machine not found or no auto start enabled " + vmLocation.getVmName());
		}
		System.out.println("Disable auto start for virtual machine : " + vmLocation.getVmName());
		a.setStartAction("None");
		a.setStopAction("None");
		vimServiceUtil.getService().reconfigureAutostart(hostConfigManager.getAutoStartManager(), hostAutoStartManagerConfig);
		System.out.println("Done");
	}

	public String getSyntax() {
		return "esx_name:vm_name"; 
	}
	
	public String getHelp() {
		return "configure virtual machine for auto start";
	}

	public String getCommand() {
		return "autostart_disable";
	}

	public Target getTarget() {
		return Target.ESX;
	}

}
