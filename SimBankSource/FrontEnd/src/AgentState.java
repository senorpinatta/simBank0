import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by benji on 10/18/2016.
 *
 * This Class represents the Agent state.
 * It allows the commands withdraw, deposit, transfer, create, and delete.
 *
 */
public class AgentState extends LoggedInState {
    // The most money in cents that can be used in an operation
    private static final int UPPER = 99999999;
    // The least amount of money that can be used in an operation
    private static final int LOWER = 0;

    //Default Constructor of the AgentState class. Uses super to call the constructor of
    //the parent class, enabling late binding.
    public AgentState(ArrayList<String> transactions, ArrayList<String> validAccounts, ArrayList<String> masterTransactions, Scanner keyboard) {
        super(transactions, validAccounts, masterTransactions, keyboard);
    }


    //"Handles" user input. If input equals one of the available functions, that function
    //is called and the result of that function is returned to indicate to the frontEnd
    //what action to perform next. If user input does not match a valid function, 0 is returned.
    @Override
    public int handleCommand(String line) {
        int stateIndex = 0;
        if(line.equals("logout"))
            stateIndex = 4;
        if(line.equals("deposit"))
            stateIndex = deposit(LOWER, UPPER);
        if(line.equals("withdraw"))
            stateIndex = withdraw(LOWER, UPPER);
        if(line.equals("transfer"))
            stateIndex = transfer(LOWER, UPPER);
        if(line.equals("create"))
            stateIndex = create();
        if(line.equals("delete"))
            stateIndex = delete();


        return stateIndex;
    }

    //Deposit, withdraw, and transfer call their respective parent's method, filling in parameters
    //(Upper/Lower bounds) appropriate to the agent state.
    public int deposit() {
        return deposit(LOWER, UPPER);
    }

    public int withdraw() {
        return withdraw(LOWER, UPPER);
    }

    public int transfer() {
        return transfer(LOWER, UPPER);
    }


    //Unique to the agent state, the create method takes two parameters, an account number
    //and name, and "creates" or adds a new account to the accounts file. This function
    //calls the accountCheck method found in the parent class to ensure that the account
    //number does not already exist and includes if clauses that ensure the parameters
    //supplied are valid.
    public int create() {
        boolean flag = true;
        String line;
        int accountNumber = 0;
        String name = null;
        while (flag) {
            try {
                System.out.println("Enter the account Number: ");
                line = keyboard.nextLine();
                System.out.printf("You entered: %s ", line);
                if (!accountCheck(line)) {
                    System.out.println("Account is valid");
                    if(line.length() == 8 && line.charAt(0) != '0') {
                        accountNumber = Integer.parseInt(line);
                    }
                    else {
                        System.out.println("Error");
                        return 0;
                    }
                }
                else {
                    System.out.println("Error: Account Already Exists");
                    return 0;
                }
                System.out.println("Enter the account Name: ");
                line = keyboard.nextLine();
                if (isValidName(line))
                    name = line;
                else {
                    System.out.println("Error");
                    return 0;
                }
                flag = false;
            } catch (NumberFormatException e) {
                System.out.println("Error");
                return 0;
            }
        }
        transactions.add(String.format("CR %d 00000000 000 %s", accountNumber, name));
        // end session because of spec. of create see lecture 5 in CISC 327 notes
        // "No new transactions should be accepted in this session
        return 0;
    }


    //Unique to the agent state, the delete method takes two parameters, an account number
    //and name, and "deletes" or removes an existing account from the accounts file. This
    //function calls the accountCheck method found in the parent class to check that the
    //account number exists and includes if clauses that check that the name supplied is valid.
    public int delete() {
        boolean flag = true;
        String line;
        int accountNumber = 0;
        String name = null;
        while (flag) {
            try {
                System.out.println("Enter the account Number: ");
                line = keyboard.nextLine();
                if (accountCheck(line)) {
                    accountNumber = Integer.parseInt(line);
                }
                else {
                    System.out.println("Error");
                    return 0;
                }
                System.out.println("Enter the account Name: ");
                line = keyboard.nextLine();
                if (isValidName(line))
                    name = line;
                else {
                    System.out.println("Error");
                    return 0;
                }
                flag = false;
            } catch (NumberFormatException e) {
                // do nothing one of the inputs was bad
                return 0;
            }
        }
        transactions.add(String.format("DL %d 00000000 000 %s", accountNumber, name));
        return 0;
    }

    private boolean isValidName(String name) {
        if (name.length() <= 30                      &&
                name.length() >= 3                       &&
                name.matches("[A-Za-z0-9 ]+")            &&
                name.charAt(0) != ' '                    &&
                name.charAt(name.length() - 1) != ' ')
            return true;
        else
            return false;
    }
}