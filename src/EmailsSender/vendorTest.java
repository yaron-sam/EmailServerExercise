package EmailsSender;

public class vendorTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MailVendorSender GMAIL = new MailVendorSender("smtp.gmail.com", "admin", "admin", "@gmail.com");
		GMAIL.sendEmail("mail@example.com", "example@gmail.com","topic" , "body");
	}

}
