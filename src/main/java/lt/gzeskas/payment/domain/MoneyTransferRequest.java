package lt.gzeskas.payment.domain;

public class MoneyTransferRequest {
    private final long accountFrom;
    private final long accountTo;
    private final double amount;

    public MoneyTransferRequest(long accountFrom, long accountTo, double amount) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    public long getAccountFrom() {
        return accountFrom;
    }

    public long getAccountTo() {
        return accountTo;
    }

    public double getAmount() {
        return amount;
    }
}
