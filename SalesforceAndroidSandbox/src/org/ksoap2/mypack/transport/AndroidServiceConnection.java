/**
 * Copyright (C) 2008 Dai Odahara.
 */

package org.ksoap2.mypack.transport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

import android.util.Log;

/**
 * This class Connects over web using apache HttpComponent
 * 
 * @author Dai Odahara
 */
public class AndroidServiceConnection implements ServiceConnection {
	private static HttpConnectionManager connectionManager = new SimpleHttpConnectionManager();
	private static HttpConnection connection;
	private static PostMethod postMethod;
	private ByteArrayOutputStream bufferStream = null;
	private static final String TAG = "AndroidServiceConnection2";

	/**
	 * Constructor taking the url to the endpoint for this soap communication
	 * 
	 * @param url
	 */
	public AndroidServiceConnection(String url) {
		try {
			HttpsURL httpURL = new HttpsURL(url);
			HostConfiguration host = new HostConfiguration();
			host.setHost(httpURL.getHost(), httpURL.getPort(), "https");
			connection = connectionManager.getConnection(host);
			postMethod = new PostMethod(url);
			Log.v(TAG, "host : " + httpURL.getHost());
			Log.v(TAG, "port : " + httpURL.getPort());
		} catch (URIException ex) {
			ex.printStackTrace();
		}
	}

	public void connect() throws IOException {
		if (!connection.isOpen()) {
			connection.open();
		} else {
			Log.v(TAG, "ServiceConnectoin1 : Already Connected");
		}
	}

	public void disconnect() {
		connection.releaseConnection();
	}

	public void setRequestProperty(String name, String value) {
		postMethod.setRequestHeader(name, value);
	}

	public void setRequestMethod(String requestMethod) throws IOException {
		if (!requestMethod.toLowerCase().equals("post")) {
			throw (new IOException("Only POST method is supported"));
		}
	}

	public OutputStream openOutputStream() throws IOException {
		bufferStream = new java.io.ByteArrayOutputStream();
		return bufferStream;
	}

	public InputStream openInputStream() throws IOException {
		RequestEntity re = new ByteArrayRequestEntity(bufferStream
				.toByteArray());
		postMethod.setRequestEntity(re);
		postMethod.execute(new HttpState(), connection);
		return postMethod.getResponseBodyAsStream();
	}

	public InputStream getErrorStream() {
		return null;
	}

}
