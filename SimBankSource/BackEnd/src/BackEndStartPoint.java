/**
 * Created by benji on 11/15/2016.
 */
/* A starting point for the back end requires 3 command line arguments
 * 1 the path to the merged transaction summary file
 * 2 the path to the Master Accounts List
 * 3 the name it will save the valid Accounts List under
 */
public class BackEndStartPoint {
    public static void main(String[] args) {
        // 0 -> MAF 1 -> VAF, 2 -> MergedTSF
        BackEnd bE = new BackEnd(args[0], args[1], args[2]);
    }
//    public static void main(String[] args) {
//        // takes any number of command line arguments >= 3 reads the 3rd and up into a String[]
//        // this is so as many TSF's can be supplied and all will be merged into one file for the back end to process.
//        String[] tsfNames = new String[args.length - 2];
//        for (int i = 2; i < args.length; i++) {
//            tsfNames[i - 2] = args[i];
//        }
//        BackEnd backEnd = new BackEnd(args[0], args[1], tsfNames);
//    }
}

