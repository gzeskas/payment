package lt.gzeskas.payment.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseSchemaInitializer {
    private static final String INIT_FILE_LOCATION = "migration/init.sql";
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSchemaInitializer.class);

    public void init(Connection connection) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(readFromInitFile());
        } catch (SQLException e) {
            logger.error("Couldn't initialize database schema.", e);
            throw new RuntimeException(e);
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String readFromInitFile() {
        return new BufferedReader(
                new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream(INIT_FILE_LOCATION),
                        Charset.defaultCharset()
                )
        ).lines().collect(Collectors.joining());
    }

}
