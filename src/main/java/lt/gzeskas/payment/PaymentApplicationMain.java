package lt.gzeskas.payment;

import lt.gzeskas.payment.configuration.ApplicationConfiguration;
import lt.gzeskas.payment.configuration.HttpServerConfiguration;
import lt.gzeskas.payment.datasource.configuration.DatabaseConfiguration;

public class PaymentApplicationMain {
	private static final int DEFAULT_HTTP_PORT = 8008;
	private static final String DEFAULT_CONTEXT_PATH = "/api/";

	public static void main(String[] args) {
		ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration(
				DatabaseConfiguration.h2EmbededDatabaseConfiguration(),
				new HttpServerConfiguration(DEFAULT_HTTP_PORT, DEFAULT_CONTEXT_PATH)
		);
		PaymentApplication paymentApplication = new PaymentApplication(applicationConfiguration);
		paymentApplication.start();
		Runtime.getRuntime().addShutdownHook(new Thread(paymentApplication::stop));
	}

}
