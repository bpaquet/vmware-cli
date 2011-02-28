package com.octo.vmware;

import com.octo.vmware.services.Configuration;

import jline.console.completer.StringsCompleter;

public class EsxCompleter extends StringsCompleter {

	public EsxCompleter() {
		for(String esx : Configuration.getCurrent().getEsxServers().keySet()) {
			getStrings().add(esx);
		}
	}
	
}
