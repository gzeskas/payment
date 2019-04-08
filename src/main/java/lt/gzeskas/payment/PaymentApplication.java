package lt.gzeskas.payment;

import lt.gzeskas.payment.configuration.ApplicationConfiguration;
import lt.gzeskas.payment.datasource.DatabaseConnectionManager;
import lt.gzeskas.payment.datasource.DatabaseSchemaInitializer;
import lt.gzeskas.payment.http.server.HttpServer;
import lt.gzeskas.payment.http.server.jetty.JettyHttpServer;
import lt.gzeskas.payment.service.MoneyTransferService;
import lt.gzeskas.payment.web.servlet.transfer.MoneyTransferServlet;

public class PaymentApplication {
    private final DatabaseConnectionManager databaseConnectionManager;
    private final HttpServer httpServer;
    private final DatabaseSchemaInitializer databaseSchemaInitializer = new DatabaseSchemaInitializer();

    public PaymentApplication(ApplicationConfiguration applicationConfiguration) {
        this.databaseConnectionManager = new DatabaseConnectionManager(applicationConfiguration.getDatabaseConfiguration());
        this.httpServer = new JettyHttpServer(applicationConfiguration.getHttpServerConfiguration());
    }

    public void start() {
        databaseSchemaInitializer.init(databaseConnectionManager.getConnection());
        MoneyTransferService moneyTransferService = new MoneyTransferService(databaseConnectionManager);
        httpServer.configureServletContextHolder(servletContextHandler -> {
            servletContextHandler.setAttribute("moneyTransferService", moneyTransferService);
            servletContextHandler.addServlet(MoneyTransferServlet.class, "/v1/transfers/");
        });
        httpServer.start();
    }

    public DatabaseConnectionManager getDatabaseConnectionManager() {
        return databaseConnectionManager;
    }

    public void stop() {
        httpServer.stop();
    }
}
