package lt.gzeskas.payment.domain.exception;

public class NotEnoughMoneyException extends RuntimeException {
    public NotEnoughMoneyException(long accountId) {
        super("Account with id: " + accountId + " doesn't have enough money for transfer.");
    }
}
