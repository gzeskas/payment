package lt.gzeskas.payment.datasource;

import lt.gzeskas.payment.exception.AccountNotFoundException;
import lt.gzeskas.payment.exception.DataSourceSqlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountBalanceRepository {
    private static final Logger logger = LoggerFactory.getLogger(AccountBalanceRepository.class);
    private static final String CREATE_BALANCE_SQL  = "INSERT INTO account_balance(account_id, amount) VALUES(?,?)";
    private static final String BALANCE_UPDATE_SQL  = "UPDATE account_balance SET amount = amount + ? WHERE account_id = ?";
    private static final String ACCOUNT_BALANCE_SQL = "SELECT amount FROM account_balance WHERE account_id = ?";

    public double subtractBalance(long accountId, double amount, Connection connection) {
        return executeBalanceUpdate(accountId, amount * -1, connection);
    }

    public double addBalance(long accountId, double amount, Connection connection) {
        return executeBalanceUpdate(accountId, amount, connection);
    }

    private double executeBalanceUpdate(long accountId, double amount, Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(BALANCE_UPDATE_SQL);
            preparedStatement.setDouble(1, amount);
            preparedStatement.setLong(2, accountId);
            int i = preparedStatement.executeUpdate();
            if (i == 0) {
                throw new AccountNotFoundException("Account with id: " + accountId +" not found");
            }
            else if (i == 1) {
                return getBalance(accountId, connection);
            } else {
                throw new DataSourceSqlException("Database inconsistency detected, there is more then 1 record with account id: "+accountId);
            }
        } catch (SQLException ex) {
            logger.error("SQL exception", ex);
            throw new DataSourceSqlException(ex);
        }
    }

    public double createAccount(long accountId, double amount, Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE_BALANCE_SQL);
            preparedStatement.setLong(1, accountId);
            preparedStatement.setDouble(2, amount);
            int i = preparedStatement.executeUpdate();
            if (i == 1) {
                return getBalance(accountId, connection);
            } else {
                throw new DataSourceSqlException("Couldn't create new record for account with id: " +  accountId);
            }
        } catch (SQLException ex) {
            logger.error("SQL exception", ex);
            throw new DataSourceSqlException(ex);
        }
    }

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
}
