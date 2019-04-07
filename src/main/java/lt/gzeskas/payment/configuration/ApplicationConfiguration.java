package lt.gzeskas.payment.configuration;

import lt.gzeskas.payment.datasource.configuration.DatabaseConfiguration;

public class ApplicationConfiguration {
    private final DatabaseConfiguration databaseConfiguration;
    private final HttpServerConfiguration httpServerConfiguration;

    public ApplicationConfiguration(DatabaseConfiguration databaseConfiguration,
                                    HttpServerConfiguration httpServerConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
        this.httpServerConfiguration = httpServerConfiguration;
    }

    public DatabaseConfiguration getDatabaseConfiguration() {
        return databaseConfiguration;
    }

    public HttpServerConfiguration getHttpServerConfiguration() {
        return httpServerConfiguration;
    }
}
