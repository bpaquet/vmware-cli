package com.octo.vmware;

public interface ICommand {
	
	class SyntaxError extends Exception {

		private static final long serialVersionUID = -4345923345805320457L;
		
	}
	
	String getCommandName();

	String getCommandHelp();

	void execute(String [] args) throws Exception;

}
