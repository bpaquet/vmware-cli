package com.octo.vmware.commands;

import java.util.List;

import com.octo.vmware.ICommand;
import com.octo.vmware.SizeFormatter;
import com.octo.vmware.entities.Datastore;
import com.octo.vmware.services.DatastoreService;
import com.octo.vmware.utils.VimServiceUtil;

public class ListDatastores implements ICommand {

	public void execute(IOutputer outputer, String[] args) throws Exception {
		if (args.length != 1) {
			throw new SyntaxError();
		}
		String esxName = args[0];
		VimServiceUtil vimServiceUtil = VimServiceUtil.get(esxName);
		List<Datastore> datastores = DatastoreService.getDatastoreList(vimServiceUtil);

		outputer.output(datastores, vimServiceUtil, new IObjectOutputer<List<Datastore>>() {

			public void output(IOutputer outputer, VimServiceUtil vimServiceUtil, List<Datastore> datastores) {
				outputer.log("Found " + datastores.size() + " datastore(s) on "
						+ vimServiceUtil.getEsxServer().getName());
				if (datastores.size() > 0) {
					outputer.log(String.format("%-20s %-20s %-20s", "Datastore name", "Capacity", "Free space"));
					outputer.log("---------------------------------------------------------------");
					for (Datastore datastore : datastores) {
						outputer.log(String.format("%-20s %-20s %-20s", datastore.getName(),
								SizeFormatter.formatSize(datastore.getCapcity()),
								SizeFormatter.formatSize(datastore.getFree())));
					}
				}
			}

		});

	}

	public String getSyntax() {
		return "esx_name";
	}

	public String getHelp() {
		return "list datastores of an esx";
	}

	public String getCommand() {
		return "list_datastore";
	}

	public Target getTarget() {
		return Target.ESX;
	}

}
