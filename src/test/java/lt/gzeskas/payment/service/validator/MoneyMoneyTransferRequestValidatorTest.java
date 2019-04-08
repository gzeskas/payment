package lt.gzeskas.payment.service.validator;

import lt.gzeskas.payment.domain.MoneyTransferRequest;
import lt.gzeskas.payment.web.servlet.transfer.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoneyMoneyTransferRequestValidatorTest {

    private final MoneyTransferRequestValidator requestValidator = new MoneyTransferRequestValidator();

    @Test
    void shouldThrowExceptionWhenAmountIsEqualsOrBellowZero() {
        assertThrows(ValidationException.class, () -> {
            requestValidator.validate(new MoneyTransferRequest(1L, 2L, 0.0));
        });
        assertThrows(ValidationException.class, () -> {
            requestValidator.validate(new MoneyTransferRequest(1L, 2L, -0.1));
        });
    }

    @Test
    void shouldThrowExceptionWhenAccountFromOrAccountToIsLowerOrEqualsToZero() {
        assertThrows(ValidationException.class, () -> {
            requestValidator.validate(new MoneyTransferRequest(0, 2L, 0.1));
        });
        assertThrows(ValidationException.class, () -> {
            requestValidator.validate(new MoneyTransferRequest(1L, 0, 0.1));
        });
        assertThrows(ValidationException.class, () -> {
            requestValidator.validate(new MoneyTransferRequest(-1L, 2L, 0.1));
        });
        assertThrows(ValidationException.class, () -> {
            requestValidator.validate(new MoneyTransferRequest(  1L, -2L, 0.1));
        });
    }

    @Test
    void shouldNotThrowExceptionWhenAmountIssBiggerThenZero() {
        requestValidator.validate(new MoneyTransferRequest(1L, 2L, 0.1));
        assertTrue(true);
    }
}