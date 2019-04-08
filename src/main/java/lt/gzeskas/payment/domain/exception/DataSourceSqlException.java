package lt.gzeskas.payment.domain.exception;

import java.sql.SQLException;

public class DataSourceSqlException extends RuntimeException {
    public DataSourceSqlException(SQLException ex) {
        super(ex);
    }

    public DataSourceSqlException(String message) {
        super(message)  ;
    }
}
