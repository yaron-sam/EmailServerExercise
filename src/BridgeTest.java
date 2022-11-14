import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import EmailsSender.MailSender;

public class BridgeTest {

	public static void main(String[] args) {
		Bridge server = Bridge.getInstance();
		String[] sendermail = { "example@other.com", "example@gmail.com", "example@walla.co.il", "user1@yahoo.com",
				"user1@gmail.com" };
		{
			Instant start = Instant.now();
			for (String sender : sendermail) {
				try {
					server.sendEmail("example@walla.co.il", sender, "subject", "body");
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
			double durationInMillis = Duration.between(start, Instant.now()).toMillis();
			System.out.println("Delta: " + durationInMillis);
		}

		{
			ExecutorService executor = Executors.newCachedThreadPool();
			Instant start = Instant.now();

			for (String sender : sendermail) {

				Runnable runnable = () -> {
					try {
						server.sendEmail("example@walla.co.il", sender, "subject", "body");
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
				};

				executor.execute(runnable);
			}

			double durationInMillis = Duration.between(start, Instant.now()).toMillis();
			System.out.println("Delta: " + durationInMillis);

			executor.shutdown();
			try {
				if (!executor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
					executor.shutdownNow();
				}
			} catch (InterruptedException e) {
				executor.shutdownNow();
			}
		}

	}

}
