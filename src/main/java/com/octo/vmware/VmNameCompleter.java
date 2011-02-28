package com.octo.vmware;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.octo.vmware.services.VmsListCache;

import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;

public class VmNameCompleter implements Completer {

	private Map<String, Completer> completers = new HashMap<String, Completer>();
	
	private StoredColonArgumentDelimiter storedColonArgumentDelimiter;
	
	public VmNameCompleter(StoredColonArgumentDelimiter storedColonArgumentDelimiter) {
		this.storedColonArgumentDelimiter = storedColonArgumentDelimiter;
	}

	public int complete(String arg0, int arg1, List<CharSequence> arg2) {
		String name = storedColonArgumentDelimiter.getLastArgumentList().getArguments()[0];
		if (completers.get(name) == null) {
			StringsCompleter stringsCompleter = new StringsCompleter();
			List<String> vms = VmsListCache.get().get(name);
			if (vms != null) {
				for(String s : vms) {
					stringsCompleter.getStrings().add(s);
				}
			}
			completers.put(name, stringsCompleter);
		}
		return completers.get(name).complete(arg0, arg1, arg2);
	}
}
