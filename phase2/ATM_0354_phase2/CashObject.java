package ATM_0354_phase2;

public class CashObject{

    private final int cashValue;
    private int count;

    public CashObject(int cashValue, int count){
        this.cashValue = cashValue;
        this.count = count;
    }

    public int getCashValue() { return cashValue; }

    public int getCount(){
        return count;
    }

    public void setCount(int count){
        this.count = count;
    }

    public boolean needsRestocking(){
        int restockLimit = 20;
        return count < restockLimit;
    }
}
