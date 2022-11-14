package EmailsSender;

import java.util.HashMap;

public class MailVendorTest {

	public static void main(String[] args) {
		
		MailSender gmail = new MailVendor("smtp.gmail.com", "admin", "admin", "@gmail.com");
		MailSender walla = new MailVendor("smtp.Walla.co.il", "admin", "admin", "@walla.co.il");

		HashMap<String, MailSender> vendors = new HashMap<String, MailSender>();
		vendors.put(gmail.getPostFix(), gmail);
		vendors.put(walla.getPostFix(), walla);
		System.out.println(vendors);
		
		String[] key = { "example@other.com", "example@gmail.com", "example@walla.co.il" };
		for (String mail : key) {
			String domain = mail.substring(mail.indexOf("@") + 1);
			MailSender vendor = vendors.get(domain);
//			System.out.println(vendors.get(domain));
			try {
				vendors.get(domain).sendEmail("example@email.com", mail, "topic", "body");
			} catch (Exception e) {
				System.err.println("mail domain don't exsist: " + domain);
			}
		}

	}

}
