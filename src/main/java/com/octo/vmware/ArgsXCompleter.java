package com.octo.vmware;

import java.util.List;

import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;

public class ArgsXCompleter implements Completer {

	private StoredArgumentDelimiter storedArgumentDelimiter;
	
	private ArgumentCompleter vmCompleter;
	
	private Completer esxCompleter;
	private int argsIndex;
	
	public ArgsXCompleter(int argsIndex, StoredArgumentDelimiter storedArgumentDelimiter) {
		this.argsIndex = argsIndex;
		this.storedArgumentDelimiter = storedArgumentDelimiter;
		this.esxCompleter = new EsxCompleter();
		StoredColonArgumentDelimiter storedColonArgumentDelimiter = new StoredColonArgumentDelimiter();
		this.vmCompleter = new ArgumentCompleter(storedColonArgumentDelimiter, new StoredColonArgumentDelimiter.SpaceToColonCompleter(new EsxCompleter()), new VmNameCompleter(storedColonArgumentDelimiter));
	}

	public int complete(String buffer, int index, List<CharSequence> candidates) {
		String command = storedArgumentDelimiter.getLastArgumentList().getArguments()[0]; 
		String type = null;
		for(ICommand c : ICommands.COMMANDS) {
			if (command.equals(c.getCommand())) {
				type = c.getSyntax().split(" ")[argsIndex - 1];
			}
		}
		if (type == null) {
			return -1;
		}
		if (type.equals("esx_name")) {
			return esxCompleter.complete(buffer, index, candidates);
		}
		if (type.equals("esx_name:vm_name")) {
			return vmCompleter.complete(buffer, index, candidates);
		}
	
		return index;
	}

}
