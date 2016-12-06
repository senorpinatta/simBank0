import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by benji on 10/18/2016.
 * This class represents a LoggedInState
 * It allows the commands logout, atm, and agent which switch to the corresponding state
 * It also defines the withdraw, deposit and transfer methods with parameters for the upper
 * and lower bound which are inherited by both agent and atm which fill in the bounds
 */
public class LoggedInState extends CommandManager {
    protected ArrayList<String> validAccounts;
    private ArrayList<String> mastTrans;
    // Where we get the input opened by FrontEnd.
    protected Scanner keyboard;

    //The default constructor for the LoggedInState class. Takes, in order, the temporary
    //transactions list, the valid accounts list, and the master transactions list and the Scanner keyboard.
    public LoggedInState(ArrayList<String> transactions, ArrayList<String> validAccounts, ArrayList<String> masterTransactions, Scanner keyboard) {
        super(transactions);
        this.keyboard = keyboard;
        this.validAccounts = validAccounts;
        this.mastTrans = masterTransactions;
    }

    //"Handles" user input. Input equaling "atm" returns 2, "agent" returns 3, anything
    //else returns 0. 2 tells the front end to enter an atm state, and 3 an agent state.
    public int handleCommand(String line) {
        if (line.equals("atm"))
            return 2;
        if (line.equals("agent"))
            return 3;
        if (line.equals("logout"))
            return 4;
        else {
            System.out.println("Error");
            return 0;
        }
    }

    //Checks through the list of valid accounts to see if any match the supplied account number.
    protected boolean accountCheck(String accountNumber) {
        for (String validAccount : validAccounts)
            if(validAccount.equals(accountNumber))
                return true;
        return false;// we know none of the accounts match the one we entered so we reject the attempt
    }

    //The implementation of deposit, withdraw, and transfer methods are written here
    //since they're used by both atm and agent states. Lower and upper bounds specific
    //to atm and agent cases are passed in from their respective subclasses. For each of these
    //methods, the accountCheck method is called to ensure that the user specifies existing
    //account numbers, as well as the transactionSum method to enforce limits on withdraw/transfer
    //amounts. If the amount is greater than permitted by bounds, the transaction is cancelled. Both
    //successful and unsuccessful transaction attempts return 0.

    //Deposits an amount between lower and upper bounds into a valid account. The user
    //is required to enter an account number and an amount, both of which are checked
    //for validity, immediately returning 0 if invalid. If the checks are passed the appropriate
    //transaction code is written to the temporary transaction file, before returning 0.
    protected int deposit(int lowerBound, int upperBound) {
        String line;
        int total = 0;
        int accountNumber = 0;
        int amount = 0;
        try {
            System.out.printf(format, "Enter account Number");
            line = keyboard.nextLine();
            if (accountCheck(line) && ! accountDeleted(line))
                accountNumber = Integer.parseInt(line);

            else {
                System.out.println("Error");
                return 0;
            }
            System.out.printf(format, "Enter amount in cents");
            line = keyboard.nextLine();
            amount = Integer.parseInt(line);
            total = singleAccountSum(String.format("%d", accountNumber), "DE", upperBound) + amount;
            if (total < lowerBound || total > upperBound) {
                System.out.printf("Error total amount is not in [%d, %d]\n", lowerBound, upperBound);
                return 0;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error");
            return 0;
        }
        transactions.add(String.format("DE %d 00000000 %d ***", accountNumber, amount));
        return 0;
    }

    //Withdraws an amount between lower and upper bounds from a valid account. The user
    //is required to enter an account number and an amount, both of which are checked
    //for validity, immediately returning 0 if invalid. If the checks are passed the appropriate
    //transaction code is written to the temporary transaction file, before returning 0.
    protected int withdraw(int lowerBound, int upperBound) {
        String line;
        int total = 0;
        int accountNumber = 0;
        int amount = 0;
        try {
            System.out.printf(format, "Enter account Number");
            line = keyboard.nextLine();

            if (accountCheck(line) && ! accountDeleted(line)) {
                accountNumber = Integer.parseInt(line);
            }
            else {
                System.out.println("Error");
                return 0;
            }
            System.out.printf(format, "Enter amount in cents");
            line = keyboard.nextLine();
            amount = Integer.parseInt(line);
            total = singleAccountSum(String.format("%d", accountNumber), "WD", upperBound) + amount;
            if (total < lowerBound || total > upperBound) {
                System.out.printf("total amount is not in [%d, %d]\n", lowerBound, upperBound);
                return 0;
            }
        } catch (NumberFormatException e) {
            return 0;
        }
        transactions.add(String.format("WD %d 00000000 %d ***", accountNumber, amount));
        return 0;
    }


    //Transfers an amount between lower and upper bounds from one account to another. The user
    //is required to enter two account numbers and an amount, all of which are checked
    //for validity, immediately returning 0 if invalid. If the checks are passed the appropriate
    //transaction code is written to the temporary transaction file, before returning 0.
    protected int transfer(int lowerBound, int upperBound) {
        boolean flag = true;
        String line;
        int total = 0;
        int accountNumberFrom = 0;
        int accountNumberTo = 0;
        int amount = 0;
        try {
            System.out.println("Enter the account Number to withdraw from: ");
            line = keyboard.nextLine();
            if (accountCheck(line) && ! accountDeleted(line)) {
                flag = false;
                accountNumberFrom = Integer.parseInt(line);
            }
            else {
                System.out.println("Error");
                return 0;
            }

            System.out.println("Enter the account Number to deposit to: ");
            line = keyboard.nextLine();
            if (accountCheck(line) && ! accountDeleted(line)) {
                flag = false;
                accountNumberTo = Integer.parseInt(line);
            }
            else {
                System.out.println("Error");
                return 0;
            }

            System.out.printf(format, "Enter amount in cents");
            line = keyboard.nextLine();
            amount = Integer.parseInt(line);
            total = singleAccountSum(String.format("%d", accountNumberFrom), "WD", upperBound) + amount;
            total += sumTransfersOutOf(String.format("%d", accountNumberFrom), "TR", upperBound);
            if (total < lowerBound || total > upperBound) {
                System.out.printf("Error total amount is not in [%d, %d]\n", lowerBound, upperBound);
                return 0;
            }
            total = singleAccountSum(String.format("%d", accountNumberTo), "DE", upperBound) + amount;
            total += sumTransfersInto(String.format("%d", accountNumberTo), "TR", upperBound);
            if (total < lowerBound || total > upperBound)
                return 0;
        } catch (NumberFormatException e) {
            System.out.println("Error");
            return 0;
        }
        transactions.add(String.format("TR %d %d %d ***", accountNumberFrom, accountNumberTo, amount));
        return 0;
    }

    private int sumTransfersInto(String accNum, String transType, int upperBound) {
        return sumTransfers(accNum, transType, upperBound, 2);
    }

    private int sumTransfersOutOf(String accNum, String transType, int upperBound) {
        return sumTransfers(accNum, transType, upperBound, 1);
    }

    private int sumTransfers(String accNum, String transType, int upperBound, int index) {
        int sum = 0;
        if (upperBound == 99999999)
            return 0;
        else {
            sum += sumList(transactions, accNum, transType, index);
            sum += sumList(mastTrans, accNum, transType, index);
        }
        return sum;
    }


    //Called from transactionSum method. This method reads/parses the strings stored in a
    //transactions list to obtain the total amount that has been deposited/withdrawn from an account
    //in a session.
    private int sumList(ArrayList<String> trans, String accNum, String transType, int index) {
        int sum = 0;
        String[] parts;
        for (String line :  trans) {
            parts = line.split(" ");
            if (accNum.equals(parts[index]) && transType.equals(parts[0])) {
                sum += Integer.parseInt(parts[3]);
//				System.out.printf("match index: %d parts[index]: %s parts[0]: %s accNum %s transType: %s\n", index, parts[index], ); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            }
        }
        return sum;
    }


    //This method is called to enforce transaction limits for atm users. If the user is
    //an agent, it returns 0 to allow the transaction to continue. If the user is an agent,
    //It calls sumList to read the temporary & master transactions list and return the total
    //amount that would be deposited/withdrawn if this transaction continues.
    private int singleAccountSum(String accNum, String transType, int upperBound) {
        int sum = 0;
        if (upperBound == 99999999)
            return 0;
        else {
            sum += sumList(transactions, accNum, transType, 1);
            sum += sumList(mastTrans, accNum, transType, 1);
        }
        return sum;
    }

    // This method checks both the masterTransactions list and the temporaryTransactions List for a delete operation on the supplied account number
    private boolean accountDeleted(String accountNum) {
        return deletedCheckList(accountNum, mastTrans) || deletedCheckList(accountNum, transactions);

    }
    // This method checks a list for any lines with have deleted an account with the supplied account number
    private boolean deletedCheckList(String accountNum, ArrayList<String> list) {
        String[] parts;
        for (String line : list) {
            parts = line.split(" ");
            if (parts[0].equals("DL"))
                if (parts[1].equals(accountNum))
                    return true;
        }
        return false;
    }

}