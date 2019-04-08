package lt.gzeskas.payment.service;

import lt.gzeskas.payment.datasource.repository.AccountBalanceRepository;
import lt.gzeskas.payment.datasource.DatabaseConnectionManager;
import lt.gzeskas.payment.datasource.repository.PaymentTransactionRepository;
import lt.gzeskas.payment.domain.MoneyTransferResponse;
import lt.gzeskas.payment.domain.MoneyTransferRequest;
import lt.gzeskas.payment.domain.exception.AccountNotFoundException;
import lt.gzeskas.payment.domain.exception.DataSourceSqlException;
import lt.gzeskas.payment.domain.exception.NotEnoughMoneyException;
import lt.gzeskas.payment.service.validator.MoneyTransferRequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class MoneyTransferService {
    private static final Logger logger = LoggerFactory.getLogger(MoneyTransferService.class);
    private final DatabaseConnectionManager databaseConnectionManager;
    private final PaymentTransactionRepository transactionRepository = new PaymentTransactionRepository();
    private final AccountBalanceRepository balanceRepository = new AccountBalanceRepository();
    private final MoneyTransferRequestValidator validator = new MoneyTransferRequestValidator();

    public MoneyTransferService(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }

    /**
     * throws NotEnoughMoneyException, AccountNotFoundException, ValidationException, DataSourceSqlException
     */
    public MoneyTransferResponse transfer(MoneyTransferRequest moneyTransferRequest) {
        validator.validate(moneyTransferRequest);
        Connection connection = databaseConnectionManager.getConnection();
        try {
            connection.setAutoCommit(false);
            final String transactionUUID = UUID.randomUUID().toString();
            double balance = balanceRepository.subtractBalance(moneyTransferRequest.getAccountFrom(), moneyTransferRequest.getAmount(), connection);
            if (balance - moneyTransferRequest.getAmount() < 0.0) {
                throw new NotEnoughMoneyException(moneyTransferRequest.getAccountFrom());
            }
            balanceRepository.addBalance(moneyTransferRequest.getAccountTo(), moneyTransferRequest.getAmount(), connection);
            transactionRepository.addToAccount(transactionUUID,
                    moneyTransferRequest.getAccountTo(),
                    moneyTransferRequest.getAmount(),
                    connection
            );
            transactionRepository.subtractFromAccount(transactionUUID,
                    moneyTransferRequest.getAccountFrom(),
                    moneyTransferRequest.getAmount(),
                    connection
            );
            connection.commit();
            return new MoneyTransferResponse(transactionUUID);
        } catch (SQLException e) {
            logger.error("Received SQL error.", e);
            rollbackTransaction(connection);
            throw new DataSourceSqlException(e);
        }
        catch (NotEnoughMoneyException | AccountNotFoundException e) {
            rollbackTransaction(connection);
            throw e;
        }
    }

    private void rollbackTransaction(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            logger.error("Couldn't rollback transaction", e);
            throw new DataSourceSqlException(e);
        }
    }
}
