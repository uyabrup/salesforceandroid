/**
 * Copyright (C) 2008 Dai Odahara.
 */

package org.ksoap2.mypack.transport;

import java.io.*;

import org.ksoap2.*;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.*;

import android.util.Log;

/**
 * Apache HttpComponent based HttpTransport layer.
 * 
 * @author Dai Odahara
 */
public class AndroidHttpTransport extends Transport {

	private static final String TAG = "AndroidHttpTransport";
	private static String namespace;
    /**
     * Creates instance of HttpTransport with set url
     * 
     * @param url
     *            the destination to POST SOAP data
     */
    public AndroidHttpTransport(String url, String namespace) {
        super(url);
        this.namespace = namespace;
    }

    /**
     * set the desired soapAction header field
     * 
     * @param soapAction
     *            the desired soapAction
     * @param envelope
     *            the envelope containing the information for the soap call.
     */
        public void call(String soapAction, SoapSerializationEnvelope envelope) {

    	AndroidServiceConnection connection = null;
    	try {
            soapAction = "\"\"";

	        byte[] requestData = createRequestData(envelope, namespace);

	        requestDump = debug ? new String(requestData) : null;
	        responseDump = null;

	        connection = getServiceConnection();
	        connection.connect();

	        connection.setRequestProperty("User-Agent", "SalesforceAndroid");
	        connection.setRequestProperty("SOAPAction", soapAction);	        
	        connection.setRequestProperty("Content-Type", "text/xml");
	        connection.setRequestProperty("Connection", "keep-alive");
	        connection.setRequestProperty("Content-Length", "" + requestData.length);
	        connection.setRequestMethod("POST");
	        
	        OutputStream os = connection.openOutputStream();
	        os.write(requestData, 0, requestData.length);
	        os.flush();
	        os.close();

	        InputStream is;
	        try {
	            is = connection.openInputStream();
	        } catch (IOException e) {
	            is = connection.getErrorStream();
	            if (is == null) {
	                connection.disconnect();
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
		            Log.v(TAG, "DBG:request:" + requestDump);
	            }
	        }	        
	        parseSoapResponse(envelope, is);
        } catch (IOException ex) {
        	ex.printStackTrace();
        }  catch (XmlPullParserException ex) {
        	ex.printStackTrace();
        }  finally {
        	connection.disconnect();
        }
    }

        public void parseXmlAsJson(SoapSerializationEnvelope envelope, InputStream is, String ename) {
        	try {
    	    	parseXmlResponse(envelope, is, ename);
    	    } catch (IOException ex) {
    	    	ex.printStackTrace();
    	    }  catch (XmlPullParserException ex) {
    	    	ex.printStackTrace();
    	    }  catch (Exception ex) {
    	    	ex.printStackTrace();
    	    }
        }
            
        
    protected AndroidServiceConnection getServiceConnection() throws IOException {
    	return new AndroidServiceConnection(url);
    }

}
