package com.octo.vmware;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;

import com.octo.vmware.ICommand.SyntaxError;
import com.octo.vmware.ICommand.Target;
import com.octo.vmware.commands.AutostartDisable;
import com.octo.vmware.commands.AutostartEnable;
import com.octo.vmware.commands.AutostartInfo;
import com.octo.vmware.commands.CancelTask;
import com.octo.vmware.commands.ClearFinishedTasks;
import com.octo.vmware.commands.CopyVm;
import com.octo.vmware.commands.DeleteFromDisk;
import com.octo.vmware.commands.ListResourcePools;
import com.octo.vmware.commands.ListTasks;
import com.octo.vmware.commands.ListVms;
import com.octo.vmware.commands.MountVmwareTools;
import com.octo.vmware.commands.MoveIntoResourcePool;
import com.octo.vmware.commands.PowerOff;
import com.octo.vmware.commands.PowerOn;
import com.octo.vmware.commands.RebootGuest;
import com.octo.vmware.commands.Reset;
import com.octo.vmware.commands.Show;
import com.octo.vmware.commands.ShutdownGuest;
import com.octo.vmware.commands.UnMountVmwareTools;
import com.octo.vmware.services.Configuration;
import com.octo.vmware.utils.PropertiesUtils;
import com.octo.vmware.utils.SoapUtils;

public class Cli {

	private static final ICommand[] COMMANDS = { new ListVms(), new Show(), new PowerOff(), new PowerOn(),
			new ShutdownGuest(), new RebootGuest(), new Reset(), new MountVmwareTools(), new UnMountVmwareTools(),
			new MoveIntoResourcePool(), new ListResourcePools(), new AutostartInfo(), new AutostartEnable(),
			new AutostartDisable(), new DeleteFromDisk(), new ListTasks(), new CancelTask(), new CopyVm(), new ClearFinishedTasks()};

	public static void main(String[] args) throws Exception {
		SoapUtils.initSSL();
		Cli client = new Cli();
		client.run(args);
	}

	private void run(String[] origArgs) throws Exception {
		FileInputStream fis = new FileInputStream("config.properties");
		Configuration.initCurrentConfiguration(PropertiesUtils.loadProperties(fis));
		List<String> l = new ArrayList<String>();
		for (String s : origArgs) {
			if (s.length() != 0) {
				l.add(s);
			}
		}
		String[] args = l.toArray(new String[0]);
		if (args.length != 0) {
			boolean ok = executeComand(args[0], Arrays.copyOfRange(args, 1, args.length));
			System.exit(ok ? 0 : 1);
		} else {
			ConsoleReader consoleReader = new ConsoleReader();
			consoleReader.addCompleter(new Completer() {
				
				public int complete(String arg0, int arg1, List<CharSequence> arg2) {
					String before = arg0.substring(0, arg1);
					if (before.length() == 0) {
						for(ICommand command : COMMANDS) {
							arg2.add(command.getCommand() + " ");
						}
					}
					else {
						for(ICommand command : COMMANDS) {
							if (command.getCommand().startsWith(before)) {
								arg2.add(command.getCommand().substring(before.length() - 1) + " ");
							}
						}
					}
					return arg1 - 1;
				}
				
			});
			while (true) {
				String cmd = consoleReader.readLine("VMWare-Cli> ");
				if (cmd == null) {
					System.exit(0);
				}
				String[] splittedCommand = CommandSplitter.split(cmd);
				if (splittedCommand.length != 0) {
					try {
						if (!executeComand(splittedCommand[0], Arrays.copyOfRange(splittedCommand, 1,
								splittedCommand.length))) {
							help();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private String format(ICommand command) {
		return String.format("- %-65s : %s", command.getCommand() + " " + command.getSyntax(), command.getHelp());
	}
	
	private boolean executeComand(String run, String[] args) throws Exception {
		for (ICommand command : COMMANDS) {
			if ("help".equals(run)) {
				help();
				return true;
			} else {
				if (command.getCommand().equals(run)) {
					try {
						command.execute(args);
						return true;
					} catch (SyntaxError e) {
						System.err.println("Wrong command syntax :\n" + format(command));
						return true;
					}
				}
			}
		}
		return false;
	}

	class Help implements ICommand {

		public void execute(String[] args) throws Exception {
		}

		public String getCommand() {
			return "help";
		}

		public String getHelp() {
			return "this help";
		}

		public String getSyntax() {
			return "";
		}

		public Target getTarget() {
			return null;
		}
		
	}
	
	private void help() {
		System.out.println("Available commands : ");
		System.out.println(format(new Help()));
		System.out.println("Esx commands : ");
		help(Target.ESX);
		System.out.println("Converter commands : ");
		help(Target.CONVERTER);
	}

	private void help(Target target) {
		for (ICommand command : COMMANDS) {
			if (command.getTarget() == target) {
				System.out.println(format(command));
			}
		}
	}

}