package com.octo.vmware;

import jline.console.completer.ArgumentCompleter.ArgumentList;
import jline.console.completer.ArgumentCompleter.WhitespaceArgumentDelimiter;

public class StoredArgumentDelimiter extends WhitespaceArgumentDelimiter {

	private ArgumentList lastArgumentList;
	
	@Override
	public ArgumentList delimit(CharSequence arg0, int arg1) {
		lastArgumentList = super.delimit(arg0, arg1);
		return lastArgumentList;
	}
	
	public ArgumentList getLastArgumentList() {
		return lastArgumentList;
	}

}
