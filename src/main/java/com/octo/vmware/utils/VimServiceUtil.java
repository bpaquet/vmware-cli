package com.octo.vmware.utils;

import java.util.HashMap;
import java.util.Map;

import vim2.ManagedObjectReference;
import vim2.ServiceContent;
import vim2service.VimPortType;
import vim2service.VimService;

import com.octo.vmware.entities.EsxServer;
import com.octo.vmware.services.Configuration;

public class VimServiceUtil {

	private static Map<String, VimServiceUtil> map = new HashMap<String, VimServiceUtil>();
	
	public static VimServiceUtil get(String esxName) throws Exception {
		if (!map.containsKey(esxName)) {
			map.put(esxName, new VimServiceUtil(esxName));
		}
		return map.get(esxName);
	}
	
	private VimPortType service;
	private ServiceContent serviceContent;
	private EsxServer esxServer;

	private VimServiceUtil(String esxName) throws Exception {
		esxServer = Configuration.getCurrent().getEsxServer(esxName);
		initializeService(esxServer.getUrl(), esxServer.getUsername(), esxServer.getPassword());
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
	
	public EsxServer getEsxServer() {
		return esxServer;
	}

}
