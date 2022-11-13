package EmailsSender;

public class MailVendorSenderTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MailVendorSender gmail = new MailVendorSender("smtp.gmail.com", "admin", "admin", "@gmail.com");
		gmail.sendEmail("mail@example.com", "example@gmail.com","topic" , "body");
	}

}
