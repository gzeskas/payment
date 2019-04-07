package lt.gzeskas.payment.web.servlet.transfer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransferStatusApiResponse {
    public final String transactionUUID;

    @JsonCreator
    public TransferStatusApiResponse(@JsonProperty("transactionUUID") String transactionUUID) {
        this.transactionUUID = transactionUUID;
    }

    public String getTransactionUUID() {
        return transactionUUID;
    }
}
