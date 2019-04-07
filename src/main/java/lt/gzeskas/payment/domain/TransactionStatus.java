package lt.gzeskas.payment.domain;

public class TransactionStatus {
    private final String transactionUUID;

    public TransactionStatus(String transactionUUID) {
        this.transactionUUID = transactionUUID;
    }

    public String getTransactionUUID() {
        return transactionUUID;
    }
}
