package com.octo.vmware.commands;

import java.util.List;

import vim25.AutoStartPowerInfo;
import vim25.HostAutoStartManagerConfig;
import vim25.HostConfigInfo;

import com.octo.vmware.ICommand;
import com.octo.vmware.entities.VmInfo;
import com.octo.vmware.services.HostConfigService;
import com.octo.vmware.services.VmsListService;
import com.octo.vmware.utils.VimServiceUtil;

public class AutostartInfo implements ICommand {

	public void execute(IOutputer outputer, String[] args) throws Exception {
		if (args.length != 1) {
			throw new SyntaxError();
		}
		String esxName = args[0];
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(esxName);
		List<VmInfo> vmInfos = VmsListService.getVmsList(vimServiceUtil);
		HostConfigInfo hostConfigInfo = HostConfigService.getHostConfig(vimServiceUtil, "config");
		HostAutoStartManagerConfig hostAutoStartManagerConfig = hostConfigInfo.getAutoStart();
		for(VmInfo vmInfo : vmInfos) {
			AutoStartPowerInfo autostart = null;
			for(AutoStartPowerInfo autoStartPowerInfo : hostAutoStartManagerConfig.getPowerInfo()) {
				if (vmInfo.getManagedObjectReference().getValue().equals(autoStartPowerInfo.getKey().getValue())) {
					autostart = autoStartPowerInfo;
				}
			}
			if (autostart != null) {
				vmInfo.setAutoStart(autostart.getStopAction());
			}
		}
		
		outputer.output(vmInfos, vimServiceUtil, new IObjectOutputer<List<VmInfo>>() {

			public void output(IOutputer outputer, VimServiceUtil vimServiceUtil, List<VmInfo> vmInfos) {
				System.out.println("Found " + vmInfos.size() + " VM(s) on " + vimServiceUtil.getEsxServer().getName());
				if (vmInfos.size() > 0) {
					outputer.log(String.format("%-30s %-10s %-10s", "VM Name", "Autostart", "Stop action"));
					outputer.log("----------------------------------------------");
					for(VmInfo vmInfo : vmInfos) {
						outputer.log(String.format("%-30s %-10s %-10s", vmInfo.getName(), vmInfo.getAutoStart() != null ? "Enable" : "", vmInfo.getAutoStart() != null ? vmInfo.getAutoStart() : ""));
					}
				}
			}
			
		});
	}

	public String getSyntax() {
		return "esx_name"; 
	}

	public String getHelp() {
		return "show auto start config for an esx server";
	}

	public String getCommand() {
		return "autostart_show";
	}

	public Target getTarget() {
		return Target.ESX;
	}

}
