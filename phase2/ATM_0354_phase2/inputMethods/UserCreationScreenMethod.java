package ATM_0354_phase2.inputMethods;

import ATM_0354_phase2.ATM;
import ATM_0354_phase2.BankManager;
import ATM_0354_phase2.InputMethod;
import ATM_0354_phase2.Main;

import java.util.Scanner;
import java.util.regex.Pattern;

public class    UserCreationScreenMethod implements InputMethod {
    @Override
    public String run(Scanner in) {
        System.out.println("========== User Creation ==========");
        while (true) {
            System.out.println("What type of account are you creating?\nA. User\nB. Employee");
            System.out.println(">");
            String type = in.nextLine();
            while(true){
                if (type.equalsIgnoreCase("user") || type.equalsIgnoreCase("a")){
                    type = "User";
                    break;
                }
                else if (type.equalsIgnoreCase("employee") || type.equalsIgnoreCase("b")){
                    type = "BankEmployee";
                    break;
                }
                else{
                    System.out.println("That is not a valid type.");
                    System.out.println("Please enter\nA. User\nB. Employee");
                    System.out.println(">");
                    type = in.nextLine();
                }
            }
            System.out.println("What is the new user's username? Use only alphanumeric characters.");
            System.out.print(">");
            String username = in.nextLine();
            Pattern p = Pattern.compile("^[a-zA-Z0-9]*$");

            while (!p.matcher(username).matches() || Main.atm.usernameExists(username)) {
                if (!p.matcher(username).matches()) {
                    System.out.println("Invalid character in username. Please only use alphanumeric characters (A-Z, a-z, 0-9)\n Please enter a different username");
                    System.out.print(">");
                    username = in.nextLine();
                } else if (Main.atm.usernameExists(username)) {
                    System.out.println("That username already exists or is invalid. \n Please enter a different username.");
                    System.out.print(">");
                    username = in.nextLine();
                }
            }

            System.out.println("That is a valid username.");
            System.out.println("What is the new user's password? Use only alphanumeric characters.");
            System.out.print(">");
            String password = in.nextLine();
            while (!p.matcher(password).matches()) {
                System.out.println("Invalid character in password. Please only use alphanumeric characters (A-Z, a-z, 0-9)\n Please enter a different username");
                System.out.print(">");
                password = in.nextLine();
            }
            Main.atm.createPerson(type, username, password, null);
            System.out.println("Would you like to create another user? (yes/no)");
            System.out.print(">");
            boolean anotherUser = in.nextLine().equals("yes");
            if (!anotherUser) break;
        }
        if(Main.atm.getCurUser() instanceof BankManager)
            return "BankManagerOptions";
        else
            return "BankEmployeeOptions";
    }
}
