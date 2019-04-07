package lt.gzeskas.payment.db.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSchemaInitializer {
    private static final String INIT_FILE_LOCATION = "/migration/init.sql";
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
                //connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String readFromInitFile() {
        try {
            return new String(Files.readAllBytes(Paths.get(getClass().getResource(INIT_FILE_LOCATION).toURI())));
        } catch (IOException | URISyntaxException  e) {
            logger.error("Couldn't read sql init file from resource: " + INIT_FILE_LOCATION);
            throw new RuntimeException(e);
        }
    }

}
