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
	        
	        connection = new AndroidServiceConnection(url, envelope, requestData);
	           
	        parseSoapResponse(envelope, connection.getInputStream());
	        
        } catch (IOException ex) {
        	ex.printStackTrace();
        }  catch (Exception ex) {
        	ex.printStackTrace();
        }  finally {
        	//connection.disconnect();
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
}
