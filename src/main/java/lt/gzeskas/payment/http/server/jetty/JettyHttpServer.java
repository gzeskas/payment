package lt.gzeskas.payment.http.server.jetty;

import lt.gzeskas.payment.configuration.HttpServerConfiguration;
import lt.gzeskas.payment.http.server.HttpServer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class JettyHttpServer implements HttpServer {
    private static final Logger logger = LoggerFactory.getLogger(JettyHttpServer.class);
    private final HttpServerConfiguration configuration;
    private Server server;
    private ServletContextHandler servletContextHandler;

    public JettyHttpServer(HttpServerConfiguration configuration) {
        this.configuration = configuration;
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(configuration.getPort());
        server.setConnectors(new Connector[] {connector});
        servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletContextHandler.setContextPath(configuration.getContextPath());
        server.setHandler(servletContextHandler);
    }

    @Override
    public void configureServletContextHolder(Consumer<ServletContextHandler> contextHandlerConsumer) {
        contextHandlerConsumer.accept(servletContextHandler);
    }

    @Override
    public void start() {
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
