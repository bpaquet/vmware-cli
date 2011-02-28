package com.octo.vmware;

import jline.console.completer.StringsCompleter;

public class CommandCompleter extends StringsCompleter {

	public CommandCompleter() {
		for(ICommand command : ICommands.COMMANDS) {
			getStrings().add(command.getCommand());
		}
	}
	
}
