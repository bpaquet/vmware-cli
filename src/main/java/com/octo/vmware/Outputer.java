package com.octo.vmware;

import com.octo.vmware.ICommand.IObjectOutputer;
import com.octo.vmware.ICommand.IOutputer;
import com.octo.vmware.utils.VimServiceUtil;

public class Outputer implements IOutputer {

	public void log(String message) {
		System.out.println(message);
	}

	public <T> void output(T result, VimServiceUtil vimServiceUtil, IObjectOutputer<T> objectOutputer) {
		objectOutputer.output(this, vimServiceUtil, result);
	}

	public void result(boolean result) {
		System.out.println("Result : " + (result ? "OK" : "ERROR"));
		if (!result) {
			throw new RuntimeException("Error executing command");
		}
	}
	
}
