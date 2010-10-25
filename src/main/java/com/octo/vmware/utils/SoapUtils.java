package com.octo.vmware.utils;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

public class SoapUtils {

	public final static String USER_AGENT = "VMware VI Client/4.0.0";

	public static class FakeX509TrustManager implements X509TrustManager {

		private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

		public boolean isClientTrusted(X509Certificate[] chain) {
			return (true);
		}

		public boolean isServerTrusted(X509Certificate[] chain) {
			return (true);
		}

		public X509Certificate[] getAcceptedIssuers() {
			return (_AcceptedIssuers);
		}

		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}
	}

	public static class FakeHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return (true);
		}
	}

	public static void initSSL() {
		try {
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(null, new TrustManager[] { new FakeX509TrustManager() }, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new FakeHostnameVerifier());
		} catch (Exception e) {
			throw new RuntimeException("Unable to initialize SSL", e);
		}

	}

	public static void configureStub(Object stub, String url) {
		BindingProvider bp = (BindingProvider) stub;
		bp.getRequestContext().put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
		bp.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS,
				Collections.singletonMap("User-Agent", Collections.singletonList(USER_AGENT)));
	}
}
