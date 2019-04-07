package lt.gzeskas.payment.web.servlet.transfer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MoneyTransferApiRequest {
    private final long accountFrom;
    private final long accountTo;
    private final double amount;

    @JsonCreator
    public MoneyTransferApiRequest(@JsonProperty("accountFrom") long accountFrom,
                                   @JsonProperty("accountTo") long accountTo,
                                   @JsonProperty("amount") double amount) {
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

    @Override
    public String toString() {
        return "MoneyTransferApiRequest{" +
                "accountFrom=" + accountFrom +
                ", accountTo=" + accountTo +
                ", amount=" + amount +
                '}';
    }
}
