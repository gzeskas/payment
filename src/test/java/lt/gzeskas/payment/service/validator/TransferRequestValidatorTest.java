package lt.gzeskas.payment.service.validator;

import lt.gzeskas.payment.domain.TransferRequest;
import lt.gzeskas.payment.web.servlet.transfer.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferRequestValidatorTest {

    private final TransferRequestValidator requestValidator = new TransferRequestValidator();

    @Test
    void shouldThrowExceptionWhenAmountIsEqualsOrBellowZero() {
        assertThrows(ValidationException.class, () -> {
            requestValidator.validate(new TransferRequest(1L, 2L, 0.0));
        });
        assertThrows(ValidationException.class, () -> {
            requestValidator.validate(new TransferRequest(1L, 2L, -0.1));
        });
    }

    @Test
    void shouldThrowExceptionWhenAccountFromOrAccountToIsLowerOrEqualsToZero() {
        assertThrows(ValidationException.class, () -> {
            requestValidator.validate(new TransferRequest(0, 2L, 0.1));
        });
        assertThrows(ValidationException.class, () -> {
            requestValidator.validate(new TransferRequest(1L, 0, 0.1));
        });
        assertThrows(ValidationException.class, () -> {
            requestValidator.validate(new TransferRequest(-1L, 2L, 0.1));
        });
        assertThrows(ValidationException.class, () -> {
            requestValidator.validate(new TransferRequest(  1L, -2L, 0.1));
        });
    }

    @Test
    void shouldNotThrowExceptionWhenAmountIssBiggerThenZero() {
        requestValidator.validate(new TransferRequest(1L, 2L, 0.1));
        assertTrue(true);
    }
}