package ATM_0354;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private LocalDateTime date;

    private int accountIdFrom, accountIdTo;
    private BigDecimal value;
    private boolean isBill;

    public Transaction(int accountIdFrom, int accountIdTo, BigDecimal value, boolean isBill) {
        this.date = LocalDateTime.now();
        this.accountIdFrom = accountIdFrom;
        this.accountIdTo = accountIdTo;
        this.value = value;
        this.isBill = isBill;
    }

    public boolean getIsBill() {
        return isBill;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getAccountIdFrom() {
        return accountIdFrom;
    }

    public int getAccountIdTo() {
        return accountIdTo;
    }

    public BigDecimal getValue() {
        return value;
    }
}
