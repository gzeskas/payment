package lt.gzeskas.payment.http.server.jetty;

import lt.gzeskas.payment.http.server.HttpServer;
import lt.gzeskas.payment.http.servlet.AsyncStatusServlet;
import lt.gzeskas.payment.http.servlet.StatusServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyHttpServer implements HttpServer {
    private static final Logger logger = LoggerFactory.getLogger(JettyHttpServer.class);
    private Server server;

    @Override
    public void start() {
        int maxThreads = 100;
        int minThreads = 10;
        int idleTimeout = 120;
        QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);

        server = new Server(threadPool);
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.setConnectors(new Connector[] {connector});
        ServletContextHandler servletContextHandler= new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletContextHandler.setContextPath("/");
        server.setHandler(servletContextHandler);

        ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, "/api/*");
        servletHolder.setInitOrder(0);
        servletHolder.setInitParameter(
                "jersey.config.server.provider.packages",
                "lt.gzeskas.payment.web.controller"
        );
        try {
            server.start();
        } catch (Exception e) {
            logger.error("Unable to start jetty server", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            logger.error("Unable to stop jetty server", e);
            throw new RuntimeException(e);
        }
    }
}
