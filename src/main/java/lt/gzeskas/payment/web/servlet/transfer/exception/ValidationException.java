package lt.gzeskas.payment.web.servlet.transfer.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
