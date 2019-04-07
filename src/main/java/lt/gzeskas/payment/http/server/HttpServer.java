package lt.gzeskas.payment.http.server;

import org.eclipse.jetty.servlet.ServletContextHandler;

import java.util.function.Consumer;

public interface HttpServer {
    void configureServletContextHolder(Consumer<ServletContextHandler> contextHandlerConsumer);
    void start();
    void stop();
}
