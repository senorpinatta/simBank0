import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
        /*
        * This Class controls the front end for SimBank
        * It's main function is infinitely looping:
        *    Get text input
        *    Process that input (if its a valid command do some operation)
        *    update the state (if necessary)
        */

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class FrontEnd {
    // The end of service line as a constant
    private static final String EOSLine = "ES 00000000 00000000 000 ***";
    // An object representation of the State of the program
    CommandManager commMan;
    // A list of valid accounts
    private ArrayList<String> validAccounts;
    // The record of all transactions for the day
    private ArrayList<String> masterTransactions;
    // The record for all the transactions between a login and a logout
    private ArrayList<String> temporaryTransactions;
    // The name of the file containing the valid account numbers
    private String accFile;
    // The name of the file that the Transaction Summary will be written to
    private String TSFile;

    // The constructor for the FrontEnd object accepts:
    // accFile - the name of the file containing the list of valid accounts
    // TSFile - the name of the file that the Transaction summary will be written to
    public FrontEnd(String accFile, String TSFile) {
        this.accFile = accFile;
        this.TSFile = TSFile;
        commMan = new LoggedOutState(temporaryTransactions);
        validAccounts = null;
        masterTransactions  = new ArrayList<>();
        temporaryTransactions = new ArrayList<>();
        Scanner keyboard = new Scanner(System.in);
        String line;
        int stateIndex;
        // Infinite loop. We don't want the front end to stop accepting input;
        while(true) {
            printPrompt();
            line = keyboard.nextLine();
            stateIndex = commMan.handleCommand(line.trim());
            // Special Exit condition only reachable from LoggedOutState if you enter "Terminate"
            // Implemented to assist rapid testing.
            if (stateIndex == -1) {
                System.out.println("Front end Terminated");
                break;
            }
            updateCommMan(stateIndex);
        }
    }

    /* This method is in charge of controls what "state" the Front end is in
    * It uses a code supplied to determine what state to change to
    * 0 - > no change in state
    * 1 - > change to LoggedInState
    * 2 - > change to AtmState
    * 3 - > change to AgentState
    * 4 - > change to LoggedOutState
    */
    private void updateCommMan(int stateIndex) {
        if (stateIndex == 1) { // we have logged in reads in accounts file
            validAccounts = getAccountsList(accFile);
            commMan = new LoggedInState(temporaryTransactions, validAccounts, masterTransactions);
        }
        if (stateIndex == 2)
            commMan = new AtmState(temporaryTransactions, validAccounts, masterTransactions);
        if (stateIndex == 3)
            commMan = new AgentState(temporaryTransactions, validAccounts, masterTransactions);
        if (stateIndex == 4) { // We must have logged out to get here so the users session must be over
            // write the temporaryTransactions ArrayList to the master list to keep track of the daily transactions
            masterTransactions.addAll(temporaryTransactions);
            // Write the Transaction Summary File to a file with the name stored in the TSFFile attribute
            writeTransSumFile();
            // clear temporaryTransactions
            temporaryTransactions = new ArrayList<>();
            // Change to the logged out State
            commMan = new LoggedOutState(masterTransactions);
        }
    }


    // This method reads from the file with the supplied name and returns an ArrayList of the lines in that file.
    // It is used to read in the list of valid accounts.
    private ArrayList<String> getAccountsList(String fileName) {
        String line;
        ArrayList<String> accountsList = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                accountsList.add(line);
            }
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        return accountsList;
    }

    // Writes the transaction summary file to a file with the name stored in the TSFile attribute
    private void writeTransSumFile() {
        try {
            FileWriter fileWriter = new FileWriter(TSFile);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            for (String line : temporaryTransactions)
                writer.write(line + "\n");
            writer.write(EOSLine);
            writer.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void printPrompt() {
        int spacing = 21;
        String format = "%" + spacing + "s -> ";
        if (commMan instanceof AgentState) {
            System.out.printf(format, "Agent");
        }
        else {
            if (commMan instanceof AtmState) {
                System.out.printf(format, "ATM");
            }
            else {
                if (commMan instanceof LoggedInState) {
                    System.out.printf(format, "Select atm or agent");
                }
                else {
                    if (commMan instanceof LoggedOutState) {
                        System.out.printf(format, "Please Login");
                    }
                }
            }
        }

    }
}
