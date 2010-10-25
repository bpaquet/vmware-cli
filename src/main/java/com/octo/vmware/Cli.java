package com.octo.vmware;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.octo.vmware.ICommand.SyntaxError;
import com.octo.vmware.commands.ListTasks;
import com.octo.vmware.commands.ListVms;
import com.octo.vmware.commands.MountVmwareTools;
import com.octo.vmware.commands.PowerOff;
import com.octo.vmware.commands.PowerOn;
import com.octo.vmware.commands.Reset;
import com.octo.vmware.commands.Show;
import com.octo.vmware.commands.ShutdownGuest;
import com.octo.vmware.services.Configuration;
import com.octo.vmware.utils.PropertiesUtils;
import com.octo.vmware.utils.SoapUtils;

public class Cli {

	public static void main(String[] args) throws Exception {
		SoapUtils.initSSL();
		Cli client = new Cli();
		client.run(args);
	}

	private void run(String[] args) throws Exception {
		List<ICommand> commands = new ArrayList<ICommand>();
		commands.add(new ListVms());
		commands.add(new Show());
		commands.add(new PowerOn());
		commands.add(new PowerOff());
		commands.add(new ShutdownGuest());
		commands.add(new Reset());
		commands.add(new MountVmwareTools());
		
		commands.add(new ListTasks());
		
		FileInputStream fis = new FileInputStream("config.properties");
		Configuration.initCurrentConfiguration(PropertiesUtils.loadProperties(fis));
		if (args.length != 0) {
			for(ICommand command : commands) {
				if (args[0].equals(command.getCommandName())) {
					List<String> l = new ArrayList<String>();
					for(int i = 1; i < args.length; i ++) {
						if (args[i].length() != 0) {
							l.add(args[i]);
						}
					}
					try {
						command.execute(l.toArray(new String[0]));
						System.exit(0);
					}
					catch(SyntaxError e) {
						System.err.println("Wrong command syntax : " + command.getCommandHelp());
						System.exit(1);
					}
				}
			}
		}
		System.err.println("Syntax error. Available commands : ");
		for(ICommand command : commands) {
			System.err.println("- " + command.getCommandHelp());
		}
		System.exit(1);
	}

	/*
	private void displayTasksList() throws Exception {
		TasksListService service = new TasksListService();
		// service.display();
	}

	private void displayVmsList() throws Exception {
	}

	private void cloneVm() throws Exception {
		VmCloningService service = new VmCloningService();
		// service.cloneVm(Configuration.getCurrent().getEsxServer(Configuration.ESX_NAMES.BRAD).getMachine(),
		// "Master WinXP",
		// Configuration.getCurrent().getEsxServer(Configuration.ESX_NAMES.BRAD).getMachine(),
		// "Master WinXP - Copy");
	}
	*/

}