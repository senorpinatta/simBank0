import java.util.ArrayList;
import java.util.Scanner;
/**
 * Created by benji on 10/18/2016.
 *
 * This class represents the ATM state
 *
 * It allows for the commands deposit, withdraw, and transfer.
 */
public class AtmState extends LoggedInState {

    // The most money in cents that can be used in an operation
    private static final int UPPER = 100000;
    // The least amount of money that can be used in an operation
    private static final int LOWER = 0;

    //Default Constructor of the AtmState class. Uses super to call the constructor of
    //the parent class, enabling late binding.
    public AtmState(ArrayList<String> transactions, ArrayList<String> validAccounts, ArrayList<String> masterTransactions, Scanner keyboard) {
        super(transactions, validAccounts, masterTransactions, keyboard);
    }

    //"Handles" user input. If input equals one of the available functions, that function
    //is called and the result of that function is returned to indicate to the frontEnd
    //what action to perform next. If user input does not match a valid function, 0 is returned.
    public int handleCommand(String line) {
        int stateIndex = 0;
        if (line.equals("logout"))
            stateIndex = 4;
        if(line.equals("deposit"))
            stateIndex = deposit(LOWER, UPPER);
        if(line.equals("withdraw"))
            stateIndex = withdraw(LOWER, UPPER);
        if(line.equals("transfer"))
            stateIndex = transfer(LOWER, UPPER);
        if(line.equals("create") || line.equals("delete"))
            System.out.println("Error");

        return stateIndex;
    }

    //Deposit, withdraw, and transfer call their respective parent's method, filling in parameters
    //(Upper/Lower bounds) appropriate to the atm state.
    public int deposit() {
        return deposit(LOWER, UPPER);
    }

    public int withdraw() {
        return withdraw(LOWER, UPPER);
    }

    public int transfer() {
        return transfer(LOWER, UPPER);
    }
}
