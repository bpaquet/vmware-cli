package com.octo.vmware.utils;


import vim25.ManagedObjectReference;

import com.octo.vmware.entities.Converter;
import com.octo.vmware.services.Configuration;

import converter.ConverterServiceContent;
import converterservice.ConverterPortType;
import converterservice.ConverterService;

public class ConverterServiceUtil {

	private static ConverterServiceUtil instance;
	
	public static ConverterServiceUtil getConverter() throws Exception {
		if (instance == null) {
			instance = new ConverterServiceUtil();
		}
		return instance;
	}
	
	private ConverterPortType service;
	private ConverterServiceContent serviceContent;

	private ConverterServiceUtil() throws Exception {
		Converter converterConf = Configuration.getCurrent().getConverter();
		initializeService(converterConf.getUrl(), converterConf.getUsername(), converterConf.getPassword());
	}

	private void initializeService(String url, String username, String password) throws Exception {
		service = new ConverterService(this.getClass().getResource("/converterService.wsdl")).getConverterPort();
		SoapUtils.configureStub(service, url);

		ManagedObjectReference reference = new ManagedObjectReference();
		reference.setType("ConverterServiceInstance");
		reference.setValue("ServiceInstance");
		serviceContent = service.converterRetrieveServiceContent(reference);

		service.converterLogin(serviceContent.getSessionManager(), username, password, null);
	}

	public ConverterPortType getService() {
		return service;
	}

	public ConverterServiceContent getServiceContent() {
		return serviceContent;
	}

}
