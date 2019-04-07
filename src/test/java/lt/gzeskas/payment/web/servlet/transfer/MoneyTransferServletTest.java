package lt.gzeskas.payment.web.servlet.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import lt.gzeskas.payment.PaymentApplication;
import lt.gzeskas.payment.configuration.ApplicationConfiguration;
import lt.gzeskas.payment.configuration.HttpServerConfiguration;
import lt.gzeskas.payment.datasource.AccountBalanceRepository;
import lt.gzeskas.payment.datasource.DatabaseConnectionManager;
import lt.gzeskas.payment.datasource.TestDatabaseOperations;
import lt.gzeskas.payment.datasource.configuration.DatabaseConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTransferServletTest {
    private static PaymentApplication paymentApplication;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private AccountBalanceRepository accountBalanceRepository = new AccountBalanceRepository();
    private TestDatabaseOperations testDatabaseOperations = new TestDatabaseOperations();

    @BeforeAll
    static void setup() {
        paymentApplication = new PaymentApplication(new ApplicationConfiguration(
                DatabaseConfiguration.h2EmbededDatabaseConfiguration(),
                new HttpServerConfiguration(8090, "/api/")
        ));
        paymentApplication.start();
    }

    @AfterAll
    static void tearDown() {
        paymentApplication.stop();
    }

    @BeforeEach
    void setUp() {
        testDatabaseOperations.clearTransactionsTable(paymentApplication.getDatabaseConnectionManager().getConnection());
        testDatabaseOperations.clearBalanceTable(paymentApplication.getDatabaseConnectionManager().getConnection());
    }

    @Test
    void shouldBeAbleToMakeTransferBetweenAccountsWithPositiveBalance() throws Exception {
        client.setReadTimeout(60, TimeUnit.MINUTES);
        final long fromId = 1L;
        final double fromInitial = 100.0;
        final long toId = 2L;
        final double toInitial = 0.0;
        final double amount = 2.5;
        Connection connection = paymentApplication.getDatabaseConnectionManager().getConnection();
        accountBalanceRepository.createAccount(fromId, 100.0, connection);
        accountBalanceRepository.createAccount(toId, 0.0, connection);
        final String moneyTransferRequestBody = "{\"accountFrom\": "+fromId+", \"accountTo\": "+toId+", \"amount\": "+amount+"}";
        Request request = new Request.Builder()
                .url("http://localhost:8090/api/v1/transfers/")
                .put(RequestBody.create(MediaType.parse("application/json"), moneyTransferRequestBody))
                .build();
        Response response = client.newCall(request).execute();
        assertTrue(response.isSuccessful());
        TransferStatusApiResponse apiResponse = objectMapper.readValue(response.body().string(), TransferStatusApiResponse.class);
        assertNotNull(apiResponse.getTransactionUUID());
        assertEquals(fromInitial - amount, accountBalanceRepository.getBalance(fromId, connection));
        assertEquals(toInitial + amount, accountBalanceRepository.getBalance(toId, connection));
    }

    @Test
    void shouldNotBeAbleToMakeTransferWithNegativeAmount() throws Exception {
        final String moneyTransferRequestBody = "{\"accountFrom\": 1, \"accountTo\": 2, \"amount\": -2.0}";
        Request request = new Request.Builder()
                .url("http://localhost:8090/api/v1/transfers/")
                .put(RequestBody.create(MediaType.parse("application/json"), moneyTransferRequestBody))
                .build();
        Response response = client.newCall(request).execute();
        assertEquals(406, response.code());
    }

    @Test
    void shouldNotBeAbleToMakeTransferWhenAccountBalanceHasLessMoneyThenTransferAmount() throws Exception {
        final long fromId = 1L;
        final double fromInitial = 100.0;
        accountBalanceRepository.createAccount(fromId, fromInitial, paymentApplication.getDatabaseConnectionManager().getConnection());
        final String moneyTransferRequestBody = "{\"accountFrom\": "+fromId+", \"accountTo\": 2, \"amount\": 200.0}";
        Request request = new Request.Builder()
                .url("http://localhost:8090/api/v1/transfers/")
                .put(RequestBody.create(MediaType.parse("application/json"), moneyTransferRequestBody))
                .build();
        Response response = client.newCall(request).execute();
        assertEquals(400, response.code());
    }
}