package lt.gzeskas.payment.domain;

public class MoneyTransferResponse {
    private final String transactionUUID;

    public MoneyTransferResponse(String transactionUUID) {
        this.transactionUUID = transactionUUID;
    }

    public String getTransactionUUID() {
        return transactionUUID;
    }
}
