package com.octo.vmware.utils;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import com.octo.vmware.entities.EsxServer;
import com.octo.vmware.services.Configuration;


public class PropertiesUtilsTest {
	
	private static String CONVERTER_HOSTNAME = "converter.hostname";
	private static String CONVERTER_USERNAME = "converter.username";
	private static String CONVERTER_PASSWORD = "converter.password";
	
	private static String BRAD_HOSTNAME = "esx.brad.hostname";
	private static String BRAD_USERNAME = "esx.brad.username";
	private static String BRAD_PASSWORD = "esx.brad.password";
	private static String BRAD_URL = "https://esx.brad.hostname/sdk";
	
	private static String PIT_USERNAME = "esx.pit.username";
	private static String PIT_PASSWORD = "esx.pit.password";

	@Test
	public void loadPropertiesTestConverter() throws Exception {
		PropertiesUtils pu = new PropertiesUtils();
		Properties p = new Properties();
		p.put(CONVERTER_HOSTNAME, CONVERTER_HOSTNAME);
		p.put(CONVERTER_USERNAME, CONVERTER_USERNAME);
		p.put(CONVERTER_PASSWORD, CONVERTER_PASSWORD);
		Configuration conf =  pu.loadConfiguration(p);
		Assert.assertNotNull(conf);
		Assert.assertNotNull(conf.getConverter());
		Assert.assertEquals(CONVERTER_HOSTNAME, conf.getConverter().getHostname());
		Assert.assertEquals(CONVERTER_USERNAME, conf.getConverter().getUsername());
		Assert.assertEquals(CONVERTER_PASSWORD, conf.getConverter().getPassword());
		Assert.assertEquals(0, conf.getEsxServers().size());
	}
	
	@Test
	public void loadPropertiesTestEsxServer() throws Exception {
		PropertiesUtils pu = new PropertiesUtils();
		Properties p = new Properties();
		p.put(BRAD_USERNAME, BRAD_USERNAME);
		p.put(BRAD_PASSWORD, BRAD_PASSWORD);
		p.put(BRAD_HOSTNAME, BRAD_HOSTNAME);
		p.put(PIT_USERNAME, PIT_USERNAME);
		p.put(PIT_PASSWORD, PIT_PASSWORD);
		Configuration conf =  pu.loadConfiguration(p);
		Assert.assertNotNull(conf);
		Assert.assertEquals(2, conf.getEsxServers().size());
		EsxServer brad = conf.getEsxServer("brad");
		Assert.assertEquals(BRAD_USERNAME, brad.getUsername());
		Assert.assertEquals(BRAD_PASSWORD, brad.getPassword());
		Assert.assertEquals(BRAD_HOSTNAME, brad.getHostname());
		Assert.assertEquals(BRAD_URL, brad.getUrl());
		EsxServer pit = conf.getEsxServer("pit");
		Assert.assertEquals(PIT_USERNAME, pit.getUsername());
		Assert.assertEquals(PIT_PASSWORD, pit.getPassword());
	}
	
	@Test
	public void loadPropertiesTestFromResource() throws Exception {
		Configuration conf = PropertiesUtils.loadProperties(this.getClass().getResourceAsStream("/app.test.properties"));
		Assert.assertNotNull(conf);
		Assert.assertNotNull(conf.getConverter());
		Assert.assertEquals(CONVERTER_HOSTNAME, conf.getConverter().getHostname());
		Assert.assertEquals(CONVERTER_USERNAME, conf.getConverter().getUsername());
		Assert.assertEquals(CONVERTER_PASSWORD, conf.getConverter().getPassword());
		Assert.assertEquals(2, conf.getEsxServers().size());
		EsxServer brad = conf.getEsxServer("brad");
		Assert.assertEquals(BRAD_HOSTNAME, brad.getHostname());
		Assert.assertEquals(BRAD_URL, brad.getUrl());
		Assert.assertEquals(BRAD_USERNAME, brad.getUsername());
		Assert.assertEquals(BRAD_PASSWORD, brad.getPassword());
		EsxServer pit = conf.getEsxServer("pit");
		Assert.assertEquals(PIT_USERNAME, pit.getUsername());
		Assert.assertEquals(PIT_PASSWORD, pit.getPassword());
	}
	
}
