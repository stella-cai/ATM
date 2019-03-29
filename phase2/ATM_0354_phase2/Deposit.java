package ATM_0354_phase2;

import com.sun.istack.internal.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Deposit extends Transaction {
    public Deposit (@NotNull Account accountTo, BigDecimal value) {
        super(null, accountTo, value, LocalDateTime.now());
    }

    public Deposit (@NotNull Account accountTo, BigDecimal value, LocalDateTime date) {
        super(null, accountTo, value, date);
    }

    @Override
    public void process(){
        if(!getAccountTo().canTransferIn()){
            System.out.println("Account unable to transfer money in.");
            return;
        }
        try{
            getAccountTo().transferMoneyIn(getValue());
            getAccountTo().addTransaction(this);
        }
        catch(MoneyTransferException e){
            System.out.println(e.toString());
        }
    }
    @Override
    public String toString(){
        return String.format("%s: $%f was deposited into %s's account number %d", this.getClass().getSimpleName(),
                this.getValue().doubleValue(), this.getAccountTo().getUsername(), this.getAccountTo().getId());
    }

    @Override
    public void undo() {
        getAccountTo().forceTransferOut(getValue());
        getAccountTo().undoTransaction(this);
    }

    @Override
    public String serialize() {
        return String.join(",",
                this.getClass().getSimpleName(), ((Integer) this.getAccountTo().getId()).toString(),
                this.getValue().toString(), this.getDate().toString());
    }

    @Override
    public String view() {
        return String.format("Deposit of %f into %s's account %d", this.getValue().doubleValue(), this.getAccountTo().getUsername(), this.getAccountTo().getId());
    }
}
