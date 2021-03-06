package com.octo.vmware.commands;

import java.util.List;

import vim25.AutoStartPowerInfo;
import vim25.AutoStartWaitHeartbeatSetting;
import vim25.HostAutoStartManagerConfig;
import vim25.HostConfigInfo;
import vim25.HostConfigManager;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.entities.VmLocation;
import com.octo.vmware.services.HostConfigService;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class AutostartEnable implements ICommand {

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
		VmInfo target = null;
		for(VmInfo vmInfo : vmInfos) {
			if (vmInfo.getName().equals(vmLocation.getVmName())) {
				target = vmInfo;
				for(AutoStartPowerInfo autoStartPowerInfo : hostAutoStartManagerConfig.getPowerInfo()) {
					if (vmInfo.getManagedObjectReference().getValue().equals(autoStartPowerInfo.getKey().getValue())) {
						throw new RuntimeException("Already configured for auto start " + args[0]);
					}
				}
			}
		}
		if (target == null) {
			throw new RuntimeException("Virtual machine not found " + vmLocation.getVmName());
		}
		outputer.log("Enable auto start for virtual machine : " + vmLocation.getVmName());
		AutoStartPowerInfo n = new AutoStartPowerInfo();
		n.setKey(target.getManagedObjectReference());
		n.setDynamicType(null);
		n.setStartAction("PowerOn");
		n.setStartDelay(-1);
		n.setStartOrder(-1);
		n.setStopAction("SystemDefault");
		n.setStopDelay(-1);
		n.setWaitForHeartbeat(AutoStartWaitHeartbeatSetting.SYSTEM_DEFAULT);
		hostAutoStartManagerConfig.getPowerInfo().add(n);
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
		return "autostart_enable";
	}

	public Target getTarget() {
		return Target.ESX;
	}

}
