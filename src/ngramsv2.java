import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 *
 */
public class ngramsv2 {

    // no more than this many input files need to be processed
    final static int MAX_INPUT_FILES = 100;

    // an array to hold Gutenberg corpus file names
    static String[] inputNames = new String[MAX_INPUT_FILES];

    static int fCount = 0;

    // Keeps track of character occurrence count
    private static int[] charOccurrArray = new int[26];

    /**
     * Initializes the character occurrences array to zero
     */
    private static void initCharArray() {
        // Initializing array
        for (int i = 0; i < charOccurrArray.length; i++)
            charOccurrArray[i] = 0;
    }

    /**
     * Loads all file names in the directory subtree into array 'inputFileNames'
     *
     * @param path
     */
    public static void listFilestoRead(final File path) {
        for (final File fEntry : path.listFiles()) {
            if (fEntry.isDirectory()) {
                listFilestoRead(fEntry);
            } else if (fEntry.getName().endsWith(".txt")) {
                inputNames[fCount++] = fEntry.getPath();
            }
        }
    }

    /**
     * This method is used to update the character occurrences in the
     * charOccurr array
     *
     * @param wrd String
     * @param alph String
     */
    private static void processCharactersInWord(String wrd, String alph) {
        for (int i = 0; i < wrd.length(); i++) {
            if (Character.isLetter(wrd.charAt(i))) {
                /* Updates the character occurrences in the charOccurrArray */
                charOccurrArray[alph.indexOf(wrd.charAt(i))]++;
            }
        }
    }

    /**
     * opnWordOutFile attempts to open the output file for writing words to.
     * If unsuccessful, the program will terminate.
     *
     * @param arg Array of Strings
     * @param pWriter PrintWriter
     */
    private static void opnWordOutFile(String[] arg, PrintWriter pWriter) {
        // Opening output file for writing words
        try {
            pWriter = new PrintWriter(arg[1], "UTF-8");
            System.out.println(arg[1] +
                    " successfully opened for writing words");
        } catch (IOException e) {
            System.err.println("Program terminated\n");
            System.exit(1);
        }
    }

    /**
     * opnCharOutFile attempts to open the output file to write the character
     * occurrences. If unsuccessful, the program will terminate.
     *
     * @param arg Array of Strings
     * @param pWrt PrintWriter
     */
    private static void opnCharOutFile(String[] arg, PrintWriter pWrt) {
        // Attempt to open output file to write char occurrences
        try {
            pWrt = new PrintWriter(arg[2], "UTF-8");
            System.out.println(arg[2] +
                    " successfully opened for writing character counts");
        } catch (IOException e) {
            System.err.println("Unable to open " + arg[2]
                    + " for writing character counts");
            System.err.println("Program terminating\n");
            System.exit(1);
        }
    }

    public static void main(String[] args) {

        // did the user provide correct number of command line arguments?
        // if not, print message and exit
        if (args.length != 3) {
            System.err.println("Number of command line arguments must be 3");
            System.err.println("You have given " + args.length +
                    " command line arguments");
            System.err.println("Incorrect usage. Program terminated");
            System.err.println("Correct usage: java Ngrams " +
                    "<path-to-input-files> <outfile-for-words>" +
                    " <outfile-for-char-counts");
            System.exit(1);
        }


        // A line of text extracted from a file.
        String line;

        // A word extracted from the 'line'
        String word;

        // Letter characters
        String alphabet = "abcdefghijklmnopqrstuvwxyz";

        // For writing extracted words to output file
        PrintWriter wrdWriter = null;

        // For writing characters and their occurrences to an output file
        PrintWriter charCountWriter = null;

        // Attempt to open output file to write words
        opnWordOutFile(args, wrdWriter);


        // extract input file name from command line arguments
        // this is the name of the file from the Gutenberg corpus
        String inputFileDirectoryName = args[0];
        System.out.println("Input files directory path name is: " +
                inputFileDirectoryName);

        Pattern wPattern = Pattern.compile("[a-zA-z]+");
        Matcher wMatcher;

        // Initialize character array
        initCharArray();

        // For efficiently reading characters from an input stream
        BufferedReader bReader = null;

        // Processing one file at a time
        for (int j = 0; j < fCount; j++) {
            try {

                bReader = new BufferedReader(new FileReader(inputNames[j]));

                /*
                As long as there is still lines to read in the file
                 */
                while ((line = bReader.readLine()) != null) {
                    wMatcher = wPattern.matcher(line);

                    while (wMatcher.find()) {

                        // Extracts word
                        word = line.substring(wMatcher.start(), wMatcher.end());

                        // Convert the words extracted to lowercase
                        word = word.toLowerCase();

                        // TODO include method to keep track of un/bi/tri grams



                        // Write word to file
                        wrdWriter.println(word);

                        // Process characters in the word
                        processCharactersInWord(word, alphabet);
                    }
                }
            } catch (IOException e) {
                System.err.println("File: " + inputNames[j] + " not found. " +
                        "Program terminated");
                System.exit(1);
            }
        }

        // Attempting to open character output file
        opnCharOutFile(args, charCountWriter);

        // Writes the character occurrences to the respective output file
        for (int i = 0; i < charOccurrArray.length; i++) {
            charCountWriter.println(alphabet.charAt(i) + "\t"
                    + charOccurrArray[i]);
        }

        // close buffered reader. gives error
        // needs a try ... catch block
        // br.close();

        try {
            bReader.close();
        } catch (IOException e) {
            System.err.println("BufferReader error");
            System.exit(1);
        }

        // Close output file 1
        wrdWriter.close();

        // Close output file 2
        charCountWriter.close();

    } // end main
}