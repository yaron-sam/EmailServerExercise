package EmailsSender;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 
 * MailVendor class implements MailSender that represent any vendor.
 * The class hold all vendor's server information. when needed, can be extend
 * to specific vendor method (like special authentication and so on)
 * 
 * @author yaron
 * 
 */
public class MailVendor implements MailSender {
	private String hostAddress;
	private String userName;
	private String password;
	private String postFix;

	private Session session = null;
	
	public MailVendor(String hostAddress, String userName, String password, String postFix) {
		updateHostAddress(hostAddress);
		updateUserName(userName);
		updatePassword(password);
		updatePostFix(postFix);
		setSessionProp();
	}

	private void setSessionProp() {

		Properties props = new Properties();
		props.put("mail.smtp.host", this.getHostAddress());
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "false");
		props.put("mail.smtp.starttls.enable", "false"); 


		// create Authenticator object to pass in Session.getInstance argument
		Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(getUserName(), getPassword());
			}
		};
		session = Session.getInstance(props, auth);
	}

	@Override
	public void sendEmail(String toEmail, String fromEmail, String subject, String body) {
		try {
			MimeMessage msg = new MimeMessage(session);
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");

			msg.setFrom(new InternetAddress(fromEmail));

			msg.setSubject(subject, "UTF-8");

			msg.setText(body, "UTF-8");

			msg.setSentDate(new Date());

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
			
			TransportEmulator.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return "MailVendor [hostAddress=" + hostAddress + ", userName=" + userName + ", password=" + password + ", postFix="
				+ postFix + "]";
	}


	public String getHostAddress() {
		return hostAddress;
	}

	public void updateHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}

	public String getUserName() {
		return userName;
	}

	public void updateUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void updatePassword(String password) {
		this.password = password;
	}

	public String getPostFix() {
		return postFix.replaceAll("@","");
	}

	public void updatePostFix(String postFix) {
		this.postFix = postFix;
	}
}
