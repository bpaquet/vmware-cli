package com.octo.vmware;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;

import com.octo.vmware.ICommand.SyntaxError;
import com.octo.vmware.ICommand.Target;
import com.octo.vmware.services.Configuration;
import com.octo.vmware.utils.PropertiesUtils;
import com.octo.vmware.utils.SoapUtils;

public class Cli {

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
			StoredArgumentDelimiter storedArgumentDelimiter = new StoredArgumentDelimiter();
			ArgumentCompleter argumentCompleter = new ArgumentCompleter(storedArgumentDelimiter, new CommandCompleter(), new ArgsXCompleter(1, storedArgumentDelimiter), new ArgsXCompleter(2, storedArgumentDelimiter), new ArgsXCompleter(3, storedArgumentDelimiter));
			consoleReader.addCompleter(argumentCompleter);
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
		for (ICommand command : ICommands.COMMANDS) {
			if ("help".equals(run)) {
				help();
				return true;
			} else {
				if (command.getCommand().equals(run)) {
					try {
						command.execute(new Outputer(), args);
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

		public void execute(IOutputer outputer, String[] args) throws Exception {
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
		for (ICommand command : ICommands.COMMANDS) {
			if (command.getTarget() == target) {
				System.out.println(format(command));
			}
		}
	}

}