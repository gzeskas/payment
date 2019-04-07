package lt.gzeskas.payment.service;

import lt.gzeskas.payment.datasource.AccountBalanceRepository;
import lt.gzeskas.payment.datasource.DatabaseConnectionManager;
import lt.gzeskas.payment.datasource.PaymentTransactionRepository;
import lt.gzeskas.payment.domain.TransactionStatus;
import lt.gzeskas.payment.domain.TransferRequest;
import lt.gzeskas.payment.exception.AccountNotFoundException;
import lt.gzeskas.payment.exception.DataSourceSqlException;
import lt.gzeskas.payment.exception.NotEnoughMoneyException;
import lt.gzeskas.payment.service.validator.TransferRequestValidator;
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
    private final TransferRequestValidator validator = new TransferRequestValidator();

    public MoneyTransferService(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }

    /**
     * throws NotEnoughMoneyException, AccountNotFoundException, ValidationException, DataSourceSqlException
     */
    public TransactionStatus transfer(TransferRequest transferRequest) {
        validator.validate(transferRequest);
        Connection connection = databaseConnectionManager.getConnection();
        try {
            connection.setAutoCommit(false);
            final String transactionUUID = UUID.randomUUID().toString();
            double balance = balanceRepository.subtractBalance(transferRequest.getAccountFrom(), transferRequest.getAmount(), connection);
            if (balance - transferRequest.getAmount() < 0.0) {
                throw new NotEnoughMoneyException("Account with id: " + transferRequest.getAccountFrom()+ " doesn't have enough money for transfer.");
            }
            balanceRepository.addBalance(transferRequest.getAccountTo(), transferRequest.getAmount(), connection);
            transactionRepository.addToAccount(transactionUUID,
                    transferRequest.getAccountTo(),
                    transferRequest.getAmount(),
                    connection
            );
            transactionRepository.subtractFromAccount(transactionUUID,
                    transferRequest.getAccountFrom(),
                    transferRequest.getAmount(),
                    connection
            );
            connection.commit();
            return new TransactionStatus(transactionUUID);
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
