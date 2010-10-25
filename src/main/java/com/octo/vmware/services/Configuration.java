package com.octo.vmware.services;

import java.util.HashMap;
import java.util.Map;

import com.octo.vmware.entities.Converter;
import com.octo.vmware.entities.EsxServer;

public class Configuration {
	
	private static Configuration current;
	
	private Converter converter;
	private Map<String, EsxServer> esxServers = new HashMap<String, EsxServer>();
	
	public static final Configuration getCurrent() {
		assert current != null;
		return current;
	}
	
	public static void initCurrentConfiguration(final Configuration config) {
		current = config;
	}
	
	public Converter getConverter() {
		if (converter == null) {
			throw new RuntimeException("No converter defined !");
		}
		return converter;
	}
	
	public void setConverter(Converter converter) {
		this.converter = converter;
	}
	
	public Map<String, EsxServer> getEsxServers() {
		return esxServers;
	}
	
	public EsxServer getEsxServer(String name) {
		EsxServer esxServer = esxServers.get(name);
		if (esxServer == null) {
			throw new RuntimeException("Esx server not defined : " + name);
		}
		return esxServer;
	}
	
}
