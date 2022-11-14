package bridgeServer;

import java.util.HashMap;

import emailsSender.MailSender;
import emailsSender.MailVendor;

/**
 * Bridge Class.Hold one instance (singleton design pattern) that hold all MailVendor and response to send the mail throw the right account)  
 * @author yaron
 *
 */
public class Bridge {

	private static Bridge instance = null;
	private MailVendor gmail;
	private MailVendor walla;
	private MailVendor yahoo;
	private HashMap<String, MailSender> vendors;

	private Bridge() {
		gmail = new MailVendor("smtp.gmail.com", "admin", "admin", "@gmail.com");
		walla = new MailVendor("smtp.Walla.co.il", "admin", "admin", "@walla.co.il");
		yahoo = new MailVendor("smtp.yahoo.com", "admin", "admin", "@yahoo.com");

		vendors = new HashMap<String, MailSender>();
		vendors.put(gmail.getPostFix(), gmail);
		vendors.put(walla.getPostFix(), walla);
		vendors.put(yahoo.getPostFix(), yahoo);
	}

	public static Bridge getInstance() {
		if (instance == null) {
			synchronized (Bridge.class) { // synchronized flag that catch all threads that may come and try to
											// Parallel create the singleton at start.
				if (instance == null) { // for the two first threads that coming through, the second one will be be
										// Trapped here.
					instance = new Bridge();
				}
			}
		}

		return instance;
	}

	
	public void sendEmail(String toEmail, String fromEmail, String subject, String body) throws IllegalArgumentException{
		String domain = fromEmail.substring(fromEmail.indexOf("@") + 1);
		try {
			vendors.get(domain).sendEmail(toEmail, fromEmail, subject, body);
		} catch (Exception e) {
			throw new IllegalArgumentException("mail domain don't exsist: " + domain);
		}
	}
}
