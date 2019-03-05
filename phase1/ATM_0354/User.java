package ATM_0354;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;


public class User extends Person {

    private ArrayList<Account> accounts;
    private Date creationDate;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private int accountID;
    public static ATM atm;

    public User(String username, String password) {
        super(username, password);
        accounts = new ArrayList<>();
        //accountID = 0;
        creationDate = new Date();
    }

    public String getCreationDate() {
        return dateFormat.format(creationDate);
    }

    public Account getAccount(int accountId) {
        for (Account account : this.accounts) {
            if (account.getId() == accountId) return account;
        } return null;
    }

    public int getNumAccounts(){
        return this.accounts.size();
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    public void sendTransaction(int accountNum, String userTo, BigDecimal value, boolean isBill) {
        Account account;
        try {
            account = this.accounts.get(accountNum);
        }
        catch (IndexOutOfBoundsException e) {
            System.out.println("This account does not exist");
            return;
        }
        if (!atm.usernameExists(userTo)) {
            return;
        }

        account.addTransaction(new Transaction(this.getUsername(), userTo, value, isBill));
    }

    public void recieveTransaction(int accountNum, String userFrom, BigDecimal value, boolean isBill) {
        Account account;
        try {
            account = this.accounts.get(accountNum);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("This account does not exist");
            return;
        }
        account.addTransaction(new Transaction(userFrom, this.getUsername(), value, isBill));
    }

    public String getSummary() {
        // Use a StringBuilder instead of String when we are mutating a string is better practice
        StringBuilder summary = new StringBuilder();
        for (Account account: this.accounts) {
            summary.append(account.toString());
            summary.append("\n");
        }
        return summary.toString();
    }

    public Transaction getLastTransaction(int accountId) {
        Account account = this.getAccount(accountId);
        if (account == null) {
            System.out.println("Account with id " + accountId + " doesn't exist ");
            return null;
        } else {
            Transaction transaction = account.getLastTransaction();
            if (transaction == null) {
                System.out.println("User could not get last transaction from account");
                return null;
            } return transaction;
        }
    }

    public LocalDateTime getAccountDate(int accountId) {
        Account account = this.getAccount(accountId);
        if (account == null) {
            System.out.println("Account does not exist");
            return null;
        } return account.getDateOfCreation();
    }

    public BigDecimal getAccountTotal() {
        BigDecimal total = new BigDecimal(0);
        for (Account account: this.accounts) {
            if (account instanceof AssetAccount) {
                total = total.add(account.getBalance().setScale(2, BigDecimal.ROUND_HALF_UP));
            } else if (account instanceof DebtAccount) {
                total = total.subtract(account.getBalance().setScale(2, BigDecimal.ROUND_HALF_UP));
            }
        }
        return total;
    }
}
