package com.octo.vmware.entities;

import converter.ConverterTaskInfoState;
import vim25.ManagedObjectReference;


public class ConverterTask {

	private String id;
	
	private String source;
	
	private String target;
	
	private int progress;

	private String status;
	
	private ManagedObjectReference managedObjectReference;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ManagedObjectReference getManagedObjectReference() {
		return managedObjectReference;
	}

	public void setManagedObjectReference(ManagedObjectReference managedObjectReference) {
		this.managedObjectReference = managedObjectReference;
	}
	
	public boolean isFinished() {
		return ConverterTaskInfoState.ERROR.toString().equals(status)
		|| ConverterTaskInfoState.SUCCESS.toString().equals(status);
	}
	
	public boolean isSuccess() {
		return ConverterTaskInfoState.SUCCESS.toString().equals(status);
	}
}
