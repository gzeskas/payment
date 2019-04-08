package lt.gzeskas.payment.datasource;

import lt.gzeskas.payment.configuration.datasource.DatabaseConfiguration;
import lt.gzeskas.payment.datasource.repository.PaymentTransactionRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentTransactionRepositoryTest {
    private static Connection connection;
    private final PaymentTransactionRepository paymentTransactionRepository = new PaymentTransactionRepository();
    private final TestDatabaseOperations testDatabaseOperations = new TestDatabaseOperations();

    @BeforeAll
    static void init() {
        connection = new DatabaseConnectionManager(DatabaseConfiguration.h2EmbededDatabaseConfiguration()).getConnection();
    }

    @AfterAll
    static void tearDown(){
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        testDatabaseOperations.clearTransactionsTable(connection);
    }

    @Test
    void shouldBeAbleToAddMoneyToAccount() {
        final String uuid = UUID.randomUUID().toString();
        final long accountId = 12L;
        final double amount = 100.0;
        paymentTransactionRepository.addToAccount(uuid, accountId, amount, connection);
        double balance = paymentTransactionRepository.getBalance(accountId, connection);
        assertEquals(amount, balance);
    }

    @Test
    void shouldCorrectlyCalculateBalanceForAccount() {
        final String uuid = UUID.randomUUID().toString();
        final long accountId = 12L;
        final double depositAmount = 100.0;
        final double creditAmount = 50.0;
        paymentTransactionRepository.addToAccount(uuid, accountId, depositAmount, connection);
        paymentTransactionRepository.subtractFromAccount(uuid, accountId, creditAmount, connection);
        double balance = paymentTransactionRepository.getBalance(accountId, connection);
        assertEquals(depositAmount - creditAmount, balance);
    }

    @Test
    void balanceShouldBeZeroWhenAccountDoesNotExists() {
        double balance = paymentTransactionRepository.getBalance(1L, connection);
        assertEquals(0.0, balance);
    }

    @Test
    void shouldBeAbleToReturnNegativeAccountBalance() {
        final String uuid = UUID.randomUUID().toString();
        final long accountId = 12L;
        final double creditAmount = 50.0;
        paymentTransactionRepository.subtractFromAccount(uuid, accountId, creditAmount, connection);
        double balance = paymentTransactionRepository.getBalance(accountId, connection);
        assertEquals(creditAmount * -1, balance);
    }

}