package com.octo.vmware;

import com.octo.vmware.utils.VimServiceUtil;

public interface ICommand {
	
	enum Target { ESX, CONVERTER };
	
	interface IObjectOutputer<T> {
		
		void output(IOutputer outputer, VimServiceUtil vimServiceUtil, T object);
		
	}
	
	interface IOutputer {
	
		void log(String message);
		
		<T> void output(T result, VimServiceUtil vimServiceUtil, IObjectOutputer<T> objectOutputer);
		
		void result(boolean result);
		
	}
	
	class SyntaxError extends Exception {

		private static final long serialVersionUID = -4345923345805320457L;
		
	}
	
	Target getTarget();
	
	String getCommand();

	String getSyntax();

	String getHelp();

	void execute(IOutputer outputer, String [] args) throws Exception;

}
