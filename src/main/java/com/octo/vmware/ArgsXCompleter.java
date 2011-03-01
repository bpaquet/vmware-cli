package com.octo.vmware;

import java.util.List;

import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import vim25.VirtualMachineGuestOsIdentifier;

public class ArgsXCompleter implements Completer {

	private StoredArgumentDelimiter storedArgumentDelimiter;
	
	private ArgumentCompleter vmCompleter;
	
	private Completer esxWithColonCompleter;
	private Completer esxCompleter;
	private StringsCompleter guestOsCompleter;
	
	private int argsIndex;
	
	public ArgsXCompleter(int argsIndex, StoredArgumentDelimiter storedArgumentDelimiter) {
		this.argsIndex = argsIndex;
		this.storedArgumentDelimiter = storedArgumentDelimiter;
		this.esxCompleter = new EsxCompleter();
		StoredColonArgumentDelimiter storedColonArgumentDelimiter = new StoredColonArgumentDelimiter();
		this.esxWithColonCompleter = new StoredColonArgumentDelimiter.SpaceToColonCompleter(new EsxCompleter());
		this.vmCompleter = new ArgumentCompleter(storedColonArgumentDelimiter, esxWithColonCompleter, new VmNameCompleter(storedColonArgumentDelimiter));
		this.guestOsCompleter = new StringsCompleter();
		for(VirtualMachineGuestOsIdentifier virtualMachineGuestOsIdentifier : VirtualMachineGuestOsIdentifier.values()) {
			this.guestOsCompleter.getStrings().add(virtualMachineGuestOsIdentifier.value());
		}
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
		if (type.equals("esx_name:vm_name") || type.equals("esx_source_name:vm_source_name")) {
			return vmCompleter.complete(buffer, index, candidates);
		}
	
		if (type.equals("esx_target_name:vm_target_name")) {
			return esxWithColonCompleter.complete(buffer, index, candidates);
		}
		if (type.equals("guest_os")) {
			return guestOsCompleter.complete(buffer, index, candidates);
		}
	
		return index;
	}

}
