package EmailsSender;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
/**
 * Emulator to pass all mail sending stuff.. wait 0.5 second and print success message. 
 * @author yaron
 *
 */
public class TransportEmulator extends Transport {

	public TransportEmulator(Session session, URLName urlname) {
		super(session, urlname);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void sendMessage(Message msg, Address[] addresss) throws MessagingException {
	}
	
	public static void send(Message msg) throws MessagingException {
		System.out.println("server "+ msg.getSession().getProperties().getProperty("mail.smtp.host") +" sending Message to address: "+ msg.getAllRecipients()[0]);
		try {
			  Thread.sleep(500);
			} catch (InterruptedException e) {
			  Thread.currentThread().interrupt();
			}
		System.out.println("server "+ msg.getSession().getProperties().getProperty("mail.smtp.host")  + " Sent Successfully!!");

	}

	
}
