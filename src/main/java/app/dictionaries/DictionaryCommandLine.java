package app.dictionaries;

import app.CommandMain;
import app.dictionaries.utilities.Dictionary;
import app.dictionaries.utilities.DictionaryManagement;
import app.dictionaries.utilities.elements.Algorithms;
import app.dictionaries.utilities.elements.Word;

/**
 * DictionaryCommandLine
 */
public class DictionaryCommandLine {
    public Dictionary dictionary;

    private static DictionaryCommandLine instance = new DictionaryCommandLine();

    private boolean isRunning;

    public boolean isChanged;

    /**
     * Default constructor
     */
    private DictionaryCommandLine() {
        dictionary = new Dictionary();
        isRunning = true;
        isChanged = false;

        DictionaryManagement.insertFromFile(this.dictionary, CommandMain.pathToDirectory + "resources/data/dictionaries.cmddict");
    }

    /**
     * Get instance singleton
     *
     * @return instance
     */
    public static DictionaryCommandLine getInstance() {
        return instance;
    }

    /**
     * Dictionary run function
     */
    public void run() throws Exception {
        while (isRunning) {
            for (int i = 0; i < 200; ++i) System.out.println(); // Just a trick to fake clear screen

            System.out.println("--------------------------------------------------------------------");
            System.out.println("\t\t\tPROFESSIONAL DICTIONARY BY @DOUBLEH - VERSION COMMANDLINE");
            System.out.println("--------------------------------------------------------------------");
            System.out.println("For using, choose one of these options below : \n\t" +
                    "A - Add word into dictionary\n\t" +
                    "R - Remove word from dictionary\n\t" +
                    "S - Show all dictionary\n\t" +
                    "L - Look up a word\n\t" +
                    "E - Export dictionary to file\n\t" +
                    "F - Fix word in dictionary\n\t" +
                    "T - Translate document\n\t" +
                    "I - Scan image and translate to Vietnamese\n\t" +
                    "Q - Quit dictionary");
            System.out.println("--------------------------------------------------------------------");
            System.out.print("\nYour option is : ");

            String options = (CommandMain.scnCommand.hasNext()) ? CommandMain.scnCommand.next() : "";
            options = Algorithms.standardlize(options);
            CommandMain.scnCommand.nextLine();

            switch (options) {
                case "q":
                case "Q":
                    isRunning = false;
                    break;
                case "s":
                case "S":
                    this.showAllWords();
                    break;
                case "l":
                case "L":
                    DictionaryManagement.dictionaryLookUp(this.dictionary);
                    break;
                case "a":
                case "A":
                    DictionaryManagement.insertFromCommandline(this.dictionary);
                    isChanged = true;
                    break;
                case "r":
                case "R":
                    DictionaryManagement.removeFromCommandLine(this.dictionary);
                    isChanged = true;
                    break;
                case "e":
                case "E":
                    DictionaryManagement.dictionaryExportToFile(this.dictionary);
                    break;
                case "f":
                case "F":
                    DictionaryManagement.fixWord(this.dictionary);
                    break;
                case "t":
                case "T":
                    DictionaryManagement.textTranslator();
                    break;
                case "i":
                case "I":
                    DictionaryManagement.ImgScanAndTransCommandline();
                    break;
                default:
                    System.out.println("Your option does not exist...");
                    break;
            }

            if (!isRunning) break;

            System.out.print("Continue using dictionary? (Yes or anything for No) : ");
            options = (CommandMain.scnCommand.hasNext()) ? CommandMain.scnCommand.next() : "";
            options = Algorithms.standardlize(options);
            CommandMain.scnCommand.nextLine();

            switch (options) {
                case "Yes":
                case "Y":
                    continue;
                default:
                    isRunning = false;
                    break;
            }
        }
    }

    /**
     * Show all words in dictionary onto commandline
     */
    private void showAllWords() {
        int index = 0;

        System.out.println("---------------------------------------------------------------------------");
        System.out.println("All available words in this dictionary now : \n");

        //System.out.println("No\t\t| English\t\t\t| Vietnamese");
        System.out.printf("%-10s| %-20s| %-20s\n", "No", "English", "Vietnamese");
        System.out.println("---------------------------------------------------------------------------");
        for (Word word : dictionary.getWordsContainer()) {
            System.out.printf("%-10d| %-20s| %-20s\n", (index + 1), word.getTarget(), word.getExplain());
            index++;
        }

        System.out.println("---------------------------------------------------------------------------");
    }

    /**
     * Basic function of dictionary
     */
    public void dictionaryBasic() {
        DictionaryManagement.insertFromCommandline(this.dictionary);
        showAllWords();
    }

    /**
     * Advanced function of dictionary
     */
    public void dictionaryAdvanced() {
        this.showAllWords();
        DictionaryManagement.dictionaryLookUp(this.dictionary);
    }
}
