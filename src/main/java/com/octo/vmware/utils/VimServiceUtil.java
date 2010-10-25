package com.octo.vmware.utils;

import com.octo.vmware.entities.EsxServer;
import com.octo.vmware.services.Configuration;

import vim2.ManagedObjectReference;
import vim2.ServiceContent;
import vim2service.VimPortType;
import vim2service.VimService;

public class VimServiceUtil {

	private VimPortType service;
	private ServiceContent serviceContent;

	public VimServiceUtil(String esxName) throws Exception {
		EsxServer esx = Configuration.getCurrent().getEsxServer(esxName);
		initializeService(esx.getUrl(), esx.getUsername(), esx.getPassword());
	}
	
	private void initializeService(String url, String username, String password) throws Exception {
		service = new VimService().getVimPort();
		SoapUtils.configureStub(service, url);
		
		ManagedObjectReference managedObjectReference = new ManagedObjectReference();
		managedObjectReference.setType("ServiceInstance");
		managedObjectReference.setValue("ServiceInstance");

		serviceContent = service.retrieveServiceContent(managedObjectReference);
		service.login(serviceContent.getSessionManager(), username, password, null);
	}

	public VimPortType getService() {
		return service;
	}

	public ServiceContent getServiceContent() {
		return serviceContent;
	}

}
