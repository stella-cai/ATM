package ATM_0354;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;


public class User extends Person {

    private ArrayList<Account> accounts;
    private AccountFactory accountFactory;
    private Date creationDate;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public static ATM atm;
    private Account primaryAccount;

    public User(String username, String password) {
        super(username, password);
        accounts = new ArrayList<>();
        accountFactory = new AccountFactory();
        Account account = accountFactory.createAccount("CHEQUINGACCOUNT");
        creationDate = new Date();
        primaryAccount = account;
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

    public void addAccount(String accountType) {
        Account account = accountFactory.createAccount(accountType);
        this.accounts.add(account);

        String path = "phase1/ATM_0354/Files/people.txt";
        BufferedReader reader;
        ArrayList<ArrayList<String>> lines = new ArrayList<>();
        File file = new File(path);
        try{
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while(line != null){
                ArrayList<String> entries = new ArrayList<>(Arrays.asList(line.split(",")));
                if(entries.get(1).equals(this.getUsername())){
                    entries.add(accountType);
                    entries.add("0");
                }
                lines.add(entries);
                line = reader.readLine();
            }
            reader.close();
        }
        catch (IOException e){
            System.out.println(e.toString());
            System.out.println("IOException when reading people.txt to add account.");
        }

        BufferedWriter writer;
        try{
            writer = new BufferedWriter(new FileWriter(file, false));
            for(ArrayList<String> line: lines){
                for(int i = 0; i < line.size(); i++){
                    String entry = line.get(i);
                    if(i < line.size() - 1)
                        writer.write(entry + ",");
                    else
                        writer.write(entry);
                }
                writer.newLine();
            }
        }
        catch(IOException e){
            System.out.println(e.toString());
            System.out.println("IOException when writing people.txt to add account.");
        }
    }

    public boolean setPrimary(int accountID) {
        for (Account account: this.accounts) {
            if (account instanceof ChequingAccount && account.getId() == accountID) {
                primaryAccount = account;
                return true;
            }
        }
        return false;
    }

    public Account getPrimaryAccount() {
        return primaryAccount;
    }

    public int getPrimaryAccountId () {
        return primaryAccount.getId();
    }

    public void sendTransaction(String toUsername, int fromAccountId, BigDecimal value) throws MoneyTransferException {
        // Check if username exists
        if (atm.userHandler.usernameExists(toUsername)) {
            //Check if the specified account exists
            Account account = getAccount(fromAccountId);
            if (account != null) {
                User toUser = (User) atm.userHandler.getUser(toUsername);
                // TODO: use users default deposit id
                Transaction transaction = new Transaction(fromAccountId, toUser.getPrimaryAccountId(), value, false);
                // Process transaction for sender first b/c most likely if a problem were to occur, it would be
                // from subtracting money from an account, not depositing money into an account
                account.processTransaction(transaction);
                toUser.receiveTransaction(transaction);
            } else throw new MoneyTransferException(fromAccountId + " is not an existing account id that this user has");
        } else throw new MoneyTransferException(toUsername + " is not an existing username");
    }

    public void receiveTransaction(Transaction transaction) throws MoneyTransferException {
        Account account = this.getPrimaryAccount();
        if (account != null) {
            account.processTransaction(transaction);
        } else System.out.println("Account to receive transaction was null"); // Should never happen
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

    public boolean verifyID(int id){
        for (Account account : accounts){
            if(account.getId() == id){
                return true;
            }
        }
        return false;
    }

    public void defaultTransferIn(BigDecimal amount){
        try{
            getPrimaryAccount().transferMoneyIn(amount);
        }
        catch(MoneyTransferException e){
            System.out.println("Money transfer exception when transferring between users. \n" +
                    "Why can't I transfer into my default deposit account?!");
        }

    }

    /* Writes to account_creation_requests.txt
     */
    public void requestAccount(String accountType){
        String filePath = "phase1/ATM_0354/Files/account_creation_requests.txt";
        if(!(new HashSet<>(Arrays.asList("credit card", "line of credit", "chequing", "savings")).contains(accountType))){
            System.out.println("Invalid account type for request!");
            return;
        }
        String accountRequest = this.getUsername() + "," + accountType + "," + dateFormat.toString() + "\n";
        File file = new File(filePath);
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(accountRequest);
            writer.newLine();
            writer.close();
        }
        catch(IOException e){
            System.out.println(e.toString());
            System.out.println("IOException when requesting account, writing to txt file.");
        }

    }
}
