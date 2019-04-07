package lt.gzeskas.payment.datasource;

import lt.gzeskas.payment.exception.DataSourceSqlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestDatabaseOperations {
    private static final Logger logger = LoggerFactory.getLogger(TestDatabaseOperations.class);
    private static final String CLEAR_TRANSACTIONS_TABLE = "DELETE FROM payment_transactions;";
    private static final String CLEAR_BALANCE_TABLE = "DELETE FROM account_balance;";

    public void clearBalanceTable(Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(CLEAR_BALANCE_TABLE);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            logger.error("SQL exception", ex);
            throw new DataSourceSqlException(ex);
        }
    }

    public void clearTransactionsTable(Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(CLEAR_TRANSACTIONS_TABLE);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            logger.error("SQL exception", ex);
            throw new DataSourceSqlException(ex);
        }
    }
}
