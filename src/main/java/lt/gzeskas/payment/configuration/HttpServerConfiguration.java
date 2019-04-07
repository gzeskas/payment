package lt.gzeskas.payment.configuration;

public class HttpServerConfiguration {
    private final String contextPath;
    private final int port;

    public HttpServerConfiguration(int port, String contextPath) {
        this.port = port;
        this.contextPath = contextPath;
    }

    public int getPort() {
        return port;
    }

    public String getContextPath() {
        return contextPath;
    }
}
