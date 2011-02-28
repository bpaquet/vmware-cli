package com.octo.vmware;

import java.util.ArrayList;
import java.util.List;

import jline.console.completer.Completer;
import jline.console.completer.ArgumentCompleter.ArgumentList;
import jline.console.completer.ArgumentCompleter.WhitespaceArgumentDelimiter;

public class StoredColonArgumentDelimiter extends WhitespaceArgumentDelimiter {

	private ArgumentList lastArgumentList;
	
	@Override
	public ArgumentList delimit(CharSequence arg0, int arg1) {
		lastArgumentList = super.delimit(arg0, arg1);
		return lastArgumentList;
	}
	
	public ArgumentList getLastArgumentList() {
		return lastArgumentList;
	}

	@Override
	public boolean isDelimiterChar(CharSequence arg0, int arg1) {
		return arg0.charAt(arg1) == ':';
	}

	public static class SpaceToColonCompleter implements Completer {
		
		private Completer completer;

		public SpaceToColonCompleter(Completer completer) {
			this.completer = completer;
		}

		public int complete(String arg0, int arg1, List<CharSequence> arg2) {
			List<CharSequence> l = new ArrayList<CharSequence>();
			int result = completer.complete(arg0, arg1, l);
			for(CharSequence s : l) {
				String ss = "";
				for(int i = 0; i < s.length() - 1; i ++) {
					ss += s.charAt(i);
				}
				ss += ":";
				arg2.add(ss);
			}
			return result;
		}
		
	}

}
