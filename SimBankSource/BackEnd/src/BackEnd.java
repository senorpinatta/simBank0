/**
 * Created by benji on 11/8/2016.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.*;

//Transaction Summary File = TSF
//Master Accounts File = MAF
//Valid Accounts File = VAF

/* This class contains all the functionality for the back end of SimBank
 * It reads in the files
 * processes the data
 * and if no fatal errors are observed writes the new Master Accounts File and updated Valid Accounts List
 */
public class BackEnd {
    private ArrayList<String> transactions;
    private ArrayList<String> masterAccountsList;
    private String masterAccName;
    private String validAccountsName;
    // These two attributes were added for counting when statements have been executed
    private int[] statementCounter;
    private final int STATEMENT_NUM = 13;

    //BackEnds constructor performs all the functionality of the back end.
    //Reads in a merged TSF, a MAF, and a VAF.
    //For line in TSF, checks that all fields are legal, and that the transaction is valid.
    //If both conditions are met it performs the operation.
    //Writes results of completed transactions to new_ + masterAccFileName and the list of valid accounts to validAccountsFileName
    public BackEnd(String masterAccFileName, String validAccountsFileName, String mergedTSFileName) {
        // coverage part
        statementCounter = new int[STATEMENT_NUM];
        // end of coverage part
        masterAccName = masterAccFileName;
        validAccountsName = validAccountsFileName;
        transactions = new ArrayList<>();
        readIntoArray(mergedTSFileName, transactions);
        masterAccountsList = readIntoArray(masterAccFileName);
        String[] parts;
        try {
            for (String transaction : transactions) {
                // skips End of service lines
                if (transaction.equals("ES 00000000 00000000 000 ***"))
                    continue;
                // Throws Exception if there is a fatal error processing the transaction
                parts = process(transaction);
                if (isValidTransaction(parts)) {
                    statementCounter[9]++;
                    doOperation(parts);
                }
            }
            writeNewMaster();
            writeValidAccounts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Reads a text file and returns its contents as an ArrayList of strings. Called by BackEnd.
    private ArrayList<String> readIntoArray(String fileName) {
        ArrayList<String> lines = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader( new FileReader(fileName) );
            String line;
            boolean flag = true;
            while (flag) {
                line = reader.readLine();
                if (line == null) {
                    flag = false;
                }
                else {
                    lines.add(line);
                }
            }
            reader.close();
            return lines;
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException e) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        return null;
    }

    // A overloaded version of readInToArray to accommodate reading multiple files into the same array
    private void readIntoArray(String fileName, ArrayList<String> list) {
        list.addAll(readIntoArray(fileName));
    }

    // This method splits a transaction into it's parts and checks for fatal errors. Called by BackEnd.
    private String[] process(String transaction) throws Exception {
        String[] parts;
        if (transaction.length() > 60) {
            printFatalError("String is too long");
            throw new Exception();
        }
        parts = transaction.split(" ", 5);
        if (!nameIsValid(parts[4])) {
            printFatalError("Name is invalid");
            throw new Exception();
        }
        if (!numericFieldsValid(parts)) {
            printFatalError(String.format("invalid line: %s", transaction));
            throw new Exception();
        }
        return parts;
    }

    // Checks if the name is the right length, only contains valid characters and does not
    // have spaces at the start or the end.
    private boolean nameIsValid(String name) {
        if ((name.length() <= 30                      	&&
                name.length() >= 3                       	&&
                name.matches("[A-Za-z0-9 ]+")				&&
                name.charAt(name.length() - 1) != ' ' 	    &&
                name.charAt(0) != ' ')                      ||
                name.equals("***")) {
            return true;
        }
        else {
            return false;
        }
    }

    // Checks that the numeric Fields are valid
    // ie convert to ints, correct length, no leading 0 unless its a 00000000
    private boolean numericFieldsValid(String[] parts) {
        boolean result = true;
        try {
            String part;
            for (int i = 1; i < 4; i++) {
                part = parts[i];
                if (i < 3) {
                    if (! lengthExceptionCheck(part, 8, "00000000")) {
                        System.out.println("here");
                        return false;
                    }
                }
                else
                if (! lengthExceptionCheck(part, 3, "000"))
                    return false;
                Integer.parseInt(part);
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    // Checks that the numeric fields are of the right length and don't start with a 0 unless the are the
    // Exception case ie zeroed out.
    private boolean lengthExceptionCheck(String field, int length, String exception) {
        if (field.equals(exception))
            return true;
        if (field.length() != length || field.charAt(0) == 0)
            return false;
        else
            return true;
    }

    // Runs the required check based on the transaction
    // At this point we know that fields 1, 2, 3 are numeric. Field 0 is one of command
    // Codes and field 4 is a valid name
    private boolean isValidTransaction(String[] parts) {
        boolean isValid = false;
        switch (parts[0]) {
            case "CR":  statementCounter[0]++;
                isValid = createCheck(parts[1], parts[4]);
                statementCounter[8]++;
                break;
            case "DL": isValid = deleteCheck(parts[1], parts[4]);
                break;
            case "WD": isValid = withdrawCheck(parts[1], parts[3]);
                break;
            case "DE": isValid = depositCheck(parts[1], parts[3]);
                break;
            case "TR": isValid = transferCheck(parts[1], parts[2], parts[3]);
                break;
        }
        return isValid;
    }

    // checks that the create is valid
    // i.e. the account doesn't exist already
    private boolean createCheck(String accNum, String accName) {
        statementCounter[1]++;
        if (getAccIndexMAF(accNum) != -1) {
            statementCounter[2]++;
            printFailedConstraint("Account already exists");
            statementCounter[3]++;
            return false;
        }
        statementCounter[4]++;
        if (accName.equals("***") || accNum.equals("00000000")) {
            statementCounter[5]++;
            printFailedConstraint("invalid name or account number");
            statementCounter[6]++;
            return false;
        }
        else {
            statementCounter[7]++;
            return true;
        }
    }

    // Account must exist, balance of account must be 0, accName must match.
    private boolean deleteCheck(String accNum, String accName) {
        int index = getAccIndexMAF(accNum);
        if (index == -1) {
            printFailedConstraint("Account doesn't exist");
            return false;
        }
        String[] parts = masterAccountsList.get(index).split(" ", 3);
        if (!(Integer.valueOf(parts[1]) == 0)) {
            printFailedConstraint("Account not empty");
            return false;
        }
        if (!(parts[0].equals(parts[2]))) {
            printFailedConstraint("Account Name != Account Number");
            return false;
        }
        return true;
    }

    // Account must exist, withdrawal operation must not reduce account balance below 0.
    private boolean withdrawCheck(String accNum, String amount) {
        int index = getAccIndexMAF(accNum);
        if (index == -1) {
            printFailedConstraint("Account doesn't exist");
            return false;
        }
        String[] parts = masterAccountsList.get(index).split(" ", 3);
        if (Integer.valueOf(parts[1]) - Integer.valueOf(amount) < 0) {
            System.out.println(parts[1]);
            printFailedConstraint("Transaction would result in negative account balance.");
            return false;
        }
        return true;
    }

    // Account must exist, deposit operation must not fill account to a balance greater than 8 characters.
    private boolean depositCheck(String accNum, String amount) {
        int index = getAccIndexMAF(accNum);
        if (index == -1) {
            printFailedConstraint("Account doesn't exist");
            return false;
        }
        String[] parts = masterAccountsList.get(index).split(" ", 3);
        String potentialBalance = Integer.toString(Integer.valueOf(parts[1]) + Integer.valueOf(amount));
        String lineLength = parts[0] + potentialBalance + parts[2];
        if (lineLength.length() > 48) {
            printFailedConstraint("Transaction would overflow account capacity. $1,000,000.00 limit");
            return false;
        }
        return true;
    }

    // Returns true if both withdraw and deposit operations are valid.
    private boolean transferCheck(String toAccNum, String fromAccNum, String amount) {
        return withdrawCheck(fromAccNum, amount) && depositCheck(toAccNum, amount);
    }

    // Returns the index of the targetAccNum in master Accounts List, -1 if not found
    private int getAccIndexMAF(String targetAccNum) {
        String accNum;
        int counter = 0;
        for (String line : masterAccountsList) {
            accNum = line.substring(0, 8);
            if (accNum.equals(targetAccNum))
                return counter;
        }
        return -1;
    }

    // Called from backend. Reads the first 'part' of a transaction operation to determine
    // the kind of operation to be performed.
    private void doOperation(String[] parts) {
        statementCounter[10]++;
        switch (parts[0]) {
            case "CR": statementCounter[11]++;
                createOperation(parts[1], parts[4]);
                break;
            case "DL": deleteOperation(parts[1], parts[3], parts[4]);
                break;
            case "WD": withdrawOperation(parts[1], parts[3]);
                break;
            case "DE": depositOperation(parts[1], parts[3]);
                break;
            case "TR": transferOperation(parts[1], parts[2], parts[3]);
                break;
            default: printFatalError("Invalid transaction type");
        }
    }

    // Creates and adds an account to the MAF
    private void createOperation(String accNum, String accountName) {
        statementCounter[12]++;
        masterAccountsList.add(String.format("%s %s %s", accNum, "000", accountName));
    }

    // Removes an account from the MAF
    private void deleteOperation(String accNum, String accBalance, String accName) {
        int index = getAccIndexMAF(accNum);
        masterAccountsList.remove(index);
    }

    // Removes current account from MAF. Subtracts specified amount from specified account
    // and re-adds account to MAF.
    private void withdrawOperation(String accNum, String amount) {
        int index = getAccIndexMAF(accNum);
        String[] parts = masterAccountsList.get(index).split(" ", 3);
        int newBalance = Integer.valueOf(parts[1]) - Integer.valueOf(amount);
        masterAccountsList.remove(index);
        masterAccountsList.add(String.format("%s %s %s", accNum, Integer.toString(newBalance), parts[2]));
    }

    // Removes current account from MAF. Adds specified amount to specified account
    // and re-adds account to MAF.
    private void depositOperation(String accNum, String amount) {
        int index = getAccIndexMAF(accNum);
        String[] parts = masterAccountsList.get(index).split(" ", 3);
        String potentialBalance = Integer.toString(Integer.valueOf(parts[1]) + Integer.valueOf(amount));
        masterAccountsList.remove(index);
        masterAccountsList.add(String.format("%s %s %s", accNum, potentialBalance, parts[2]));
    }

    // Performs the withdraw operation and deposit operation on the appropriate accounts.
    private void transferOperation(String toAccNum, String fromAccNum, String amount) {
        withdrawOperation(fromAccNum, amount);
        depositOperation(toAccNum, amount);
    }

    // Logs to terminal any failed constraints.
    private void printFailedConstraint(String errorMessage) {
        System.out.println("Failed constraint: " + errorMessage);
    }

    // Logs to terminal a fatal constraint failure
    private void printFatalError(String errorMessage) {
        System.out.println("Fatal Error: " + errorMessage);
    }

    // Sorts MAF, writes accounts file.
    private void writeNewMaster() {
        Collections.sort(masterAccountsList, new Comparator<String>() {
            @Override
            public int compare(String line1, String line2) {
                return  line2.compareTo(line1);
            }
        });
        writeOutOfArray("new_" + masterAccName, masterAccountsList);
    }

    // Generates the valid accounts list by extracting just the account numbers from the Master Accounts File
    // and writes it a file with the name stored in validAccountsName
    private void writeValidAccounts() {
        ArrayList<String> validAccounts = new ArrayList<>();
        String accNum;
        for (String line : masterAccountsList) {
            accNum = line.substring(0, 8);
            validAccounts.add(accNum);
        }
        writeOutOfArray(validAccountsName, validAccounts);
    }

    // Writes the contents of the supplied arrayList to the supplied fileName
    private void writeOutOfArray(String fileName, ArrayList<String> list) {
        try {
            BufferedWriter writer = new BufferedWriter( new FileWriter(fileName) );
            for (String line : list) {
                writer.write(line + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error reading file '" + fileName + "'");
        }
    }

    public void printStatementArray() {
        for (int i = 0; i < statementCounter.length; i++) {
            if (statementCounter[i] == 0) {
                System.out.printf("Statement %d not executed\n", i);
            }
        }
    }
}