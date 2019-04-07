package lt.gzeskas.payment.datasource;

import lt.gzeskas.payment.exception.AccountNotFoundException;
import lt.gzeskas.payment.exception.DataSourceSqlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class PaymentTransactionRepository {
    private static final Logger logger = LoggerFactory.getLogger(PaymentTransactionRepository.class);
    private static final String ACCOUNT_BALANCE_SQL = "SELECT SUM(amount) FROM payment_transactions WHERE account_id = ?";
    private static final String INSERT_INTO_PAYMENT_TRANSACTIONS = "INSERT INTO payment_transactions(transaction_uuid, account_id, timestamp, amount) VALUES(?,?,?,?)";

    public double getBalance(long accountId, Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(ACCOUNT_BALANCE_SQL);
            preparedStatement.setLong(1, accountId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
            throw new AccountNotFoundException("Account with id: " + accountId +" not found");
        } catch (SQLException ex) {
            logger.error("SQL exception", ex);
            throw new DataSourceSqlException(ex);
        }
    }

    public void addToAccount(String transactionUUID, long accountId, double amount, Connection connection){
        addToTransactions(transactionUUID, accountId, amount, connection);
    }

    public void subtractFromAccount(String transactionUUID, long accountId, double amount, Connection connection){
        addToTransactions(transactionUUID, accountId, amount * -1, connection);
    }

    private void addToTransactions(String transactionUUID, long accountId, double amount, Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_PAYMENT_TRANSACTIONS);
            preparedStatement.setString(1, transactionUUID);
            preparedStatement.setLong(2, accountId);
            preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC.normalized())));
            preparedStatement.setDouble(4, amount);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            logger.error("SQL exception", ex);
            throw new DataSourceSqlException(ex);
        }
    }

}
