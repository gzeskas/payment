package lt.gzeskas.payment.service.validator;

import lt.gzeskas.payment.domain.MoneyTransferRequest;
import lt.gzeskas.payment.web.servlet.transfer.exception.ValidationException;

public class MoneyTransferRequestValidator {

    public void validate(MoneyTransferRequest moneyTransferRequest) {
        if (moneyTransferRequest.getAccountFrom() <= 0) {
            throw new ValidationException("Account from field must be greater then 0");
        }

        if (moneyTransferRequest.getAccountTo() <= 0) {
            throw new ValidationException("Account from field must be greater then 0");
        }

        if (moneyTransferRequest.getAmount() <= 0.0) {
            throw new ValidationException("Transfer amount should be bigger then 0.00");
        }
    }

}
