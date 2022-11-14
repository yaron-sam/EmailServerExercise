package emailsSender;

import javax.mail.MessagingException;

/**MailSender interface represent Email vendor with ability to send simple mail by the server authentication property.   
 * 
 * @author yaron
 *
 */
public interface MailSender {
	
	public void sendEmail(String toEmail, String fromEmail, String subject, String body) throws MessagingException;

	public String getPostFix();
}
