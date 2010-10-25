package com.octo.vmware;

public interface ICommand {
	
	enum Target { ESX, CONVERTER };
	
	class SyntaxError extends Exception {

		private static final long serialVersionUID = -4345923345805320457L;
		
	}
	
	Target getTarget();
	
	String getCommandName();

	String getCommandHelp();

	void execute(String [] args) throws Exception;

}
