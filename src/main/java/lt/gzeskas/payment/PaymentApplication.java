package lt.gzeskas.payment;

import lt.gzeskas.payment.http.server.HttpServer;
import lt.gzeskas.payment.http.server.jetty.JettyHttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class PaymentApplication {
	private static final Logger logger = LoggerFactory.getLogger(PaymentApplication.class);
	private static HttpServer httpServer = new JettyHttpServer();

	public static void main(String[] args) {
		logger.info("Start");

		String url = "jdbc:h2:mem:play";
		String user = "sa";
		String passwd = "s$cret";

		String query = "SELECT * FROM cars";

		try (Connection con = DriverManager.getConnection(url, user, passwd);
			 Statement st = con.createStatement();
			 ResultSet rs = st.executeQuery(query)) {

			while (rs.next()) {

				System.out.printf("%d %s %d%n", rs.getInt(1),
						rs.getString(2), rs.getInt(3));
			}

		} catch (SQLException ex) {
			logger.error("SQL exception", ex);
		}


		//httpServer.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.info("Received shutdown hook, stopping application.");
		//		httpServer.stop();
			}
		});
	}

}
