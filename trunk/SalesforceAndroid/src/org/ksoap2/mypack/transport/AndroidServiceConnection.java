/**
 * Copyright (C) 2008 Dai Odahara.
 */

package org.ksoap2.mypack.transport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.util.Log;

/**
 * This class Connects over web using apache HttpComponent
 * 
 * @author Dai Odahara
 */ 
public class AndroidServiceConnection implements ServiceConnection {
	//private static HttpConnectionManager connectionManager = new SimpleHttpConnectionManager();
	//private static HttpConnection connection;
	//private static PostMethod postMethod;
	private ByteArrayOutputStream bufferStream = null;
	private static final String TAG = "AndroidServiceConnection";
    public String requestDump;
    /** String dump of response for debugging */
    public String responseDump;
    public boolean debug;
    private HttpPost method;
    private InputStream is;
	/**
	 * Constructor taking the url to the endpoint for this soap communication
	 * @param url
	 */
	public AndroidServiceConnection(String url, SoapSerializationEnvelope envelope, byte[] requestData) {
		try {
	        requestDump = debug ? new String(requestData) : null;
	        responseDump = null;
			final SchemeRegistry schemeRegistryRegistry = new SchemeRegistry();

			schemeRegistryRegistry.register(
			    new Scheme("https", // "http"
			    		SSLSocketFactory.getSocketFactory(),
//			        PlainSocketFactory.getSocketFactory(),
			        443));    // ポート番号
			
			final HttpParams httpParams = new BasicHttpParams();
			//httpParams.setParameter("host", url);
			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);    // HTTP 1.1
			HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);        // UTF-8
	
			final HttpClient httpClient = new DefaultHttpClient(
				    new ThreadSafeClientConnManager(httpParams, schemeRegistryRegistry),
				    httpParams);

			String soapAction = "\"\"";
			method = new HttpPost(url);
			method.setHeader(new BasicHeader("User-Agent", "SalesforceAndroid"));
			method.setHeader(new BasicHeader("SOAPAction", soapAction));
			method.setHeader(new BasicHeader("Content-Type", "text/xml"));
			method.setHeader(new BasicHeader("Connection", "keep-alive"));
			//method.setHeader(new BasicHeader("Content-Length", "" + requestData.length));
			
			HttpEntity entity = new ByteArrayEntity(requestData);
			method.setEntity(entity);
			
	        OutputStream os = openOutputStream();
	        os.write(requestData, 0, requestData.length);
	        os.flush();
	        os.close();
	       
	        try {
	    		ByteArrayEntity re = new ByteArrayEntity(bufferStream
	    				.toByteArray());
	    		method.setEntity(re);
				HttpResponse response = httpClient.execute(method);

	        	is = response.getEntity().getContent();
	        } catch (IOException e) {
	            is = getErrorStream();
	            Log.v(TAG, "error:" + e.getMessage());
	            if (is == null) {
	                //connection.disconnect();
	                throw (e);
	            }
	        }

	        boolean flag = true;
	        if (flag) {
	            ByteArrayOutputStream bos = new ByteArrayOutputStream();
	            byte[] buf = new byte[256];
	            while (true) {
	                int rd = is.read(buf, 0, 256);
	                if (rd == -1)
	                    break;
	                bos.write(buf, 0, rd);
	            }
	            bos.flush();
	            buf = bos.toByteArray();
	            responseDump = new String(buf);
	            is.close();

	            is = new ByteArrayInputStream(buf);
	            if (flag) {
	            	//Log.v(TAG, "DBG:responseDump:" + responseDump);
		            //Log.v(TAG, "DBG:request:" + requestDump);
	            }
	        }	        

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void connect() throws IOException {
		/*
		if (!connection.isOpen()) {
			connection.open();
        	//connection.setSocketTimeout(1000);
		} else {
			//Log.v(TAG, "ServiceConnectoin : Already Connected");
		}
		*/
	}

	/**
	public void disconnect() {
		connection.releaseConnection();
	}
	*/
	public void setRequestProperty(String name, String value) {
		//postMethod.setRequestHeader(name, value);
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

	@Override
	public InputStream openInputStream() throws IOException {
		ByteArrayEntity re = new ByteArrayEntity(bufferStream
				.toByteArray());
		method.setEntity(re);
		
		//method.execute(new HttpState(), connection);
		return method.getEntity().getContent();// getResponseBodyAsStream();
	}
	

	public InputStream getErrorStream() {
		return null;
	}

	@Override
	public void disconnect() throws IOException {
		// TODO Auto-generated method stub		
	}
	
	public InputStream getInputStream() {
		return this.is;
	}

}
