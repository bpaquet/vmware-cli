package com.octo.vmware.commands;

import java.util.List;

import vim25.AutoStartPowerInfo;
import vim25.HostAutoStartManagerConfig;
import vim25.HostConfigInfo;
import vim25.HostConfigManager;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.services.HostConfigService;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class AutostartDisable implements ICommand {

	public void execute(IOutputer outputer, String[] args) throws Exception {
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
		outputer.log("Disable auto start for virtual machine : " + vmLocation.getVmName());
		a.setStartAction("None");
		a.setStopAction("None");
		vimServiceUtil.getService().reconfigureAutostart(hostConfigManager.getAutoStartManager(), hostAutoStartManagerConfig);
		outputer.result(true);
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
