package lt.gzeskas.payment.service;

import lt.gzeskas.payment.datasource.AccountBalanceRepository;
import lt.gzeskas.payment.datasource.DatabaseConnectionManager;
import lt.gzeskas.payment.datasource.TestDatabaseOperations;
import lt.gzeskas.payment.datasource.configuration.DatabaseConfiguration;
import lt.gzeskas.payment.db.init.DatabaseSchemaInitializer;
import lt.gzeskas.payment.domain.TransferRequest;
import lt.gzeskas.payment.exception.AccountNotFoundException;
import lt.gzeskas.payment.exception.NotEnoughMoneyException;
import lt.gzeskas.payment.web.servlet.transfer.exception.ValidationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTransferServiceTest {
    private static final DatabaseConnectionManager connectionManager = new DatabaseConnectionManager(DatabaseConfiguration.h2EmbededDatabaseConfiguration());
    private final TestDatabaseOperations testDatabaseOperations = new TestDatabaseOperations();
    private static final DatabaseSchemaInitializer databaseSchemaInitializer = new DatabaseSchemaInitializer();
    private final MoneyTransferService moneyTransferService = new MoneyTransferService(connectionManager);
    private final AccountBalanceRepository accountBalanceRepository = new AccountBalanceRepository();

    @BeforeAll
    static void init() {
        databaseSchemaInitializer.init(connectionManager.getConnection());
    }

    @BeforeEach
    void setUp() {
        testDatabaseOperations.clearBalanceTable(connectionManager.getConnection());
        testDatabaseOperations.clearTransactionsTable(connectionManager.getConnection());
    }

    @Test
    void shouldNotBeAbleToMakeTransferIfAccountNotExists() {
        final long accountFrom = 12123L;
        final long accountTo = 121231233L;
        final double amount = 100.0;
        assertThrows(AccountNotFoundException.class, () -> moneyTransferService.transfer(new TransferRequest(accountFrom, accountTo, amount)));
    }

    @Test
    void shouldNotBeAbleToMakeTransferWhenAccountBalanceIsToLow() throws Exception {
        final long accountFrom = 12123L;
        final long accountTo = 121231233L;
        final double initialAmount = 50.0;
        final double amount = 100.0;
        Connection connection = connectionManager.getConnection();
        accountBalanceRepository.createAccount(accountFrom, initialAmount, connection);
        assertThrows(NotEnoughMoneyException.class, () -> moneyTransferService.transfer(new TransferRequest(accountFrom, accountTo, amount)));
    }

    @Test
    void shouldBeAbleToMakeTransferWhenAccountBalanceIsSufficient() throws Exception {
        final long accountFrom = 12123L;
        final long accountTo = 121231233L;
        final double initialAmount = 100.0;
        final double transferAmount = 50.0;
        Connection connection = connectionManager.getConnection();
        accountBalanceRepository.createAccount(accountFrom, initialAmount, connection);
        accountBalanceRepository.createAccount(accountTo, 0.0, connection);
        moneyTransferService.transfer(new TransferRequest(accountFrom, accountTo, transferAmount));
        double balanceAfterWithDraw = accountBalanceRepository.getBalance(accountFrom, connection);
        double balanceAfterAdd = accountBalanceRepository.getBalance(accountTo, connection);
        assertEquals(transferAmount, balanceAfterAdd);
        assertEquals(initialAmount - transferAmount, balanceAfterWithDraw);
    }

    @Test
    void shouldThrowValidationExceptionWhenTransferAmountIsBellowZero() throws Exception {
        final long accountFrom = 12123L;
        final long accountTo = 121231233L;
        final double amount = -100.0;
        assertThrows(ValidationException.class, () -> moneyTransferService.transfer(new TransferRequest(accountFrom, accountTo, amount)));
    }

}