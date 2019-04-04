package lt.gzeskas.payment;

import lt.gzeskas.payment.http.server.HttpServer;
import lt.gzeskas.payment.http.server.jetty.JettyHttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentApplication {
	private static final Logger logger = LoggerFactory.getLogger(PaymentApplication.class);
	private static HttpServer httpServer = new JettyHttpServer();

	public static void main(String[] args) {
		logger.info("Start");
		httpServer.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.info("Received shutdown hook, stopping application.");
				httpServer.stop();
			}
		});
	}

}
