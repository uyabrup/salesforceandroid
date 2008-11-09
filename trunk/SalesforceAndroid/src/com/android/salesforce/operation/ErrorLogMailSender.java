package com.android.salesforce.operation;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import android.util.Log;

import javax.net.ssl.TrustManagerFactory;

import com.android.salesforce.util.StaticInformation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

import java.security.Security;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

/**
 * This class has been used for sendin error log for investigating.
 * @author Dai Odahara
 *
 */
public class ErrorLogMailSender extends javax.mail.Authenticator {
	private static final String APP_NAME = "SalesforceAndroid";
	private static final String TAG = "ErrorLogMailSender";
	private static String mailhost = "smtp.gmail.com";
	private static String fuser = "salesforce.android@gmail.com";
	private static String tuser = "dai.odahara@gmail.com";
	
	private static String password = "123456789sfdc";
	private static Session session;
	private static DataHandler handler;
	private static ErrorLogMailSender instance = new ErrorLogMailSender();

	static {
		try {
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			Security.addProvider(tmf.getProvider());
			// org.apache.harmony.xnet.provider.jsse.JSSEProvider());
		} catch (NoSuchAlgorithmException ex) {
			Log.v(TAG, ex.toString());
		} catch (Exception ex) {
			Log.v(TAG, ex.toString());
		}
	}

	// private constructor
	private ErrorLogMailSender(){
		init();
	};
	
	public static ErrorLogMailSender getInstance() {
		    return instance;
	}
	
	
	public void init() {
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.host", mailhost);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.quitwait", "false");

		session = Session.getDefaultInstance(props, this);
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(fuser, password);
	}

	public static synchronized void sendMail(String subject, String body) {
		sendMail(subject, body, fuser, tuser);
	}
	
	public static synchronized void sendMail(String subject, StackTraceElement body[]) {
		StringBuffer err = new StringBuffer();
		for(StackTraceElement e : body) err.append(e + System.getProperty("line.separator"));
		Log.v(TAG, "err:" + err.toString());
		sendMail(subject, err.toString(), fuser, tuser);
	}
	
	public static synchronized void sendMail(String subject, String body,
			String sender, String recipients) {
		MimeMessage message = new MimeMessage(session);
		TimeZone tz = TimeZone.getTimeZone("GMT");
		
		//Date dt = new Date();
		Date dt = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();		
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
		df.setTimeZone(tz);
		String time = df.format(dt) + " " + StaticInformation.USER_ID + System.getProperty("line.separator");
		
		ByteArrayDataSource dbs = new ByteArrayDataSource((time + body)
				.getBytes(), "text/plain");
		DataHandler handler = new DataHandler(dbs);
		try {
			message.setSender(new InternetAddress(sender));
			message.setSubject(APP_NAME + ":" + subject);
			message.setDataHandler(handler);
			if (recipients.indexOf(',') > 0)
				message.setRecipients(Message.RecipientType.TO, InternetAddress
						.parse(recipients));
			else
				message.setRecipient(Message.RecipientType.TO, new InternetAddress(
						recipients));
			Transport.send(message);
		} catch (AddressException ex) {
			Log.v(TAG, ex.toString());
		} catch (MessagingException ex) {
			Log.v(TAG, ex.toString());
		}
	}

	public static class ByteArrayDataSource implements DataSource {
		private byte[] data;
		private String type;

		public ByteArrayDataSource(byte[] data, String type) {
			super();
			this.data = data;
			this.type = type;
		}

		public ByteArrayDataSource(byte[] data) {
			super();
			this.data = data;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getContentType() {
			if (type == null)
				return "application/octet-stream";
			else
				return type;
		}

		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(data);
		}

		public String getName() {
			return "ByteArrayDataSource";
		}

		public OutputStream getOutputStream() throws IOException {
			throw new IOException("Not Supported");
		}
	}
}