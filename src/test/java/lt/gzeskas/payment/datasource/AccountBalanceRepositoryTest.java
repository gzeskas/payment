package lt.gzeskas.payment.datasource;

import lt.gzeskas.payment.datasource.configuration.DatabaseConfiguration;
import lt.gzeskas.payment.db.init.DatabaseSchemaInitializer;
import lt.gzeskas.payment.exception.AccountNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountBalanceRepositoryTest {

    private static final DatabaseConnectionManager connectionManager = new DatabaseConnectionManager(DatabaseConfiguration.h2EmbededDatabaseConfiguration());
    private final AccountBalanceRepository balanceRepository = new AccountBalanceRepository();
    private final TestDatabaseOperations testDatabaseOperations = new TestDatabaseOperations();
    private static final DatabaseSchemaInitializer databaseSchemaInitializer = new DatabaseSchemaInitializer();

    @BeforeAll
    static void init() {
        databaseSchemaInitializer.init(connectionManager.getConnection());
    }

    @BeforeEach
    void setUp() {
        testDatabaseOperations.clearBalanceTable(connectionManager.getConnection());
    }

    @Test
    void shouldCorrectlySubtractFromAccountBalance() {
        final long accountId =11223L;
        final double amount = 100.0;
        double initialBalance = balanceRepository.createAccount(accountId, 0.0, connectionManager.getConnection());
        double balance = balanceRepository.subtractBalance(accountId, amount, connectionManager.getConnection());
        assertEquals(initialBalance - amount, balance);
    }

    @Test
    void shouldCorrectlyAddMoneyToAccount() {
        final long accountId =11223L;
        final double amount = 100.0;
        double initialBalance = balanceRepository.createAccount(accountId, 0.0, connectionManager.getConnection());
        double balance = balanceRepository.addBalance(accountId, amount, connectionManager.getConnection());
        assertEquals(initialBalance + amount, balance);
    }

    @Test
    void shouldThrowExceptionWhenAddingMoneyToNotExistingAccount() {
        final long accountId = 11223L;
        final double amount = 100.0;
        assertThrows(AccountNotFoundException.class, () -> balanceRepository.addBalance(accountId, amount, connectionManager.getConnection()));
    }

    @Test
    void shouldBeAbleToCreateAccountWithInitialBalance() {
        final long accountId = 11223L;
        final double amount = 100.0;
        double balance = balanceRepository.createAccount(accountId, amount, connectionManager.getConnection());
        assertEquals(amount, balance);
    }
}