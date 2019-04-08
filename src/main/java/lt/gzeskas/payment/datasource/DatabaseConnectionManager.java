package lt.gzeskas.payment.datasource;

import lt.gzeskas.payment.configuration.datasource.DatabaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionManager.class);
    private final DatabaseConfiguration databaseConfiguration;

    public DatabaseConnectionManager(DatabaseConfiguration databaseConfiguration) {
        tryInitConnectionDriverClass(databaseConfiguration);
        this.databaseConfiguration = databaseConfiguration;

    }

    private void tryInitConnectionDriverClass(DatabaseConfiguration databaseConfiguration) {
        try {
            Class.forName(databaseConfiguration.getDriverClass());
        } catch (ClassNotFoundException e) {
            logger.error("Provided database connection driver class not found, class" + databaseConfiguration.getDriverClass(), e);
            throw new RuntimeException(e);
        }
    }


    public Connection getConnection() {
        try {
            return DriverManager.getConnection(databaseConfiguration.getJdbcConnectionString(), databaseConfiguration.getUserName(), databaseConfiguration.getPassword());
        } catch (SQLException e) {
            logger.error("Connection to database couldn't be created.", e);
            throw new RuntimeException(e);
        }
    }
}
