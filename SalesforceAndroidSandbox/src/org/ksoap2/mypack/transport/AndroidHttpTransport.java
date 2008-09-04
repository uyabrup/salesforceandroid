/**
 * Copyright (C) 2008 Dai Odahara.
 */

package org.ksoap2.mypack.transport;

import java.io.*;

import org.ksoap2.*;
import org.xmlpull.v1.*;

import android.util.Log;

/**
 * Apache HttpComponent based HttpTransport layer.
 * 
 * @author Dai Odahara
 */
public class AndroidHttpTransport extends Transport {

	private static final String TAG = "Transport2";
    /**
     * Creates instance of HttpTransport with set url
     * 
     * @param url
     *            the destination to POST SOAP data
     */
    public AndroidHttpTransport(String url) {
        super(url);
    }

    /**
     * set the desired soapAction header field
     * 
     * @param soapAction
     *            the desired soapAction
     * @param envelope
     *            the envelope containing the information for the soap call.
     */
        public void call(String soapAction, SoapEnvelope envelope) {

    	AndroidServiceConnection connection = null;
    	try {
            soapAction = "\"\"";

	        byte[] requestData = createRequestData(envelope);

	        requestDump = debug ? new String(requestData) : null;
	        responseDump = null;

	        connection = getServiceConnection();
	        connection.connect();

	        connection.setRequestProperty("User-Agent", "ApexG/2.0");
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
	        parseResponse(envelope, is);
        } catch (IOException ex) {
        	ex.printStackTrace();
        }  catch (XmlPullParserException ex) {
        	ex.printStackTrace();
        }  finally {
        	connection.disconnect();
        }
    }

    protected AndroidServiceConnection getServiceConnection() throws IOException {
    	return new AndroidServiceConnection(url);
    }

}
