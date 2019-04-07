package lt.gzeskas.payment.service.validator;

import lt.gzeskas.payment.domain.TransferRequest;
import lt.gzeskas.payment.web.servlet.transfer.exception.ValidationException;

public class TransferRequestValidator {

    public void validate(TransferRequest transferRequest) {
        if (transferRequest.getAccountFrom() <= 0) {
            throw new ValidationException("Account from field must be greater then 0");
        }

        if (transferRequest.getAccountTo() <= 0) {
            throw new ValidationException("Account from field must be greater then 0");
        }

        if (transferRequest.getAmount() <= 0.0) {
            throw new ValidationException("Transfer amount should be bigger then 0.00");
        }
    }

}
