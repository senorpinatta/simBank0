import java.util.ArrayList;

/**
 * Created by benji on 10/18/2016.
 *
 * This class is the parent class for all the State objects.
 **/
public abstract class CommandManager {
    protected ArrayList<String> transactions;
    public static final int spacing = 21;
    public static final String format = "%" + spacing + "s -> ";

    // Default Constructor for the CommandManagerClass
    public CommandManager(ArrayList<String> transactions) {
        this.transactions = transactions;
    }

    // This method performs the required functionality based on which state object it is
    // It returns an integer code which refers to which state it should be changed to
    // It returns 0 if no change of state is needed
    public abstract int handleCommand(String line);

}
