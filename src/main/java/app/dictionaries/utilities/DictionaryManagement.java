package app.dictionaries.utilities;

import app.AppMain;
import app.CommandMain;
import app.dictionaries.DictionaryApplication;
import app.dictionaries.DictionaryCommandLine;
import app.dictionaries.states.general.WordDialog;
import app.dictionaries.utilities.elements.Algorithms;
import app.dictionaries.utilities.elements.Word;
import app.dictionaries.utilities.elements.RelatedWord;
import app.dictionaries.utilities.elements.AudioPlayer;

import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * DictionaryManagement
 */
public class DictionaryManagement {
    /**
     * Insert dictionary from input
     *
     * @param dictionary target dictionary
     * @return dictionary after insert new words
     */
    public static Dictionary insertFromCommandline(Dictionary dictionary) {
        System.out.println("-----------------------------------------------------------------");
        System.out.print("How many words you want to add into this dictionary ? ");

        int ammount = (CommandMain.scnCommand.hasNextInt()) ? CommandMain.scnCommand.nextInt() : 0;
        CommandMain.scnCommand.nextLine();

        System.out.println("Please add full details of your words below : ");
        for (int index = 0; index < ammount; ++index) {
            Word newWord = new Word();

            System.out.print("Word number " + (index + 1) + "\n\tName : ");
            String target = (CommandMain.scnCommand.hasNextLine()) ? CommandMain.scnCommand.nextLine() : "";
            target = Algorithms.standardlize(target);
            newWord.setTarget(Algorithms.standardlize(target));

            System.out.print("\tExplanation : ");
            String explain = (CommandMain.scnCommand.hasNextLine()) ? CommandMain.scnCommand.nextLine() : "";
            explain = Algorithms.standardlize(explain);
            newWord.setExplain(explain);

            dictionary.addWord(newWord);
        }

        System.out.println("Thanks for adding new words...");

        dictionaryExportToFile(DictionaryCommandLine.getInstance().dictionary, AppMain.pathToDirectory + "resources/data/dictionaries.cmddict");

        System.out.println("-----------------------------------------------------------------");
        return dictionary;
    }

    /**
     * Remove from command line user input
     *
     * @param dictionary dictionary to remove word
     * @return dictionary after remove word
     */
    public static Dictionary removeFromCommandLine(Dictionary dictionary) {
        System.out.println("-----------------------------------------------------------------");
        System.out.print("What word do you want to remove? ");

        String target = (CommandMain.scnCommand.hasNext()) ? CommandMain.scnCommand.next() : "";

        Word word = Algorithms.getWordInDictionary(dictionary, target);

        if (word != null) {
            try {
                dictionary.getWordsContainer().remove(word);

                System.out.println("Sucessfully remove word " + target);

                dictionaryExportToFile(DictionaryCommandLine.getInstance().dictionary, AppMain.pathToDirectory + "resources/data/dictionaries.cmddict");
            } catch (Exception e) {
                System.out.println("Failed to remove word :" + e);
            }
        } else System.out.println("Your word " + target + " does not exist in dictionary!");

        System.out.println("-----------------------------------------------------------------");
        return dictionary;
    }

    /**
     * Image scan and translate in commandline version
     */
    public static void ImgScanAndTransCommandline() {
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Please give us full path to your image in the computer below: ");

        String path = (CommandMain.scnCommand.hasNext()) ? CommandMain.scnCommand.next() : "";

        System.out.println("Checking for image...");

        File file = new File(path);

        if (file.exists()) {
            System.out.println("File exist, scanning...");

            String result = "";

            try {
                result = Algorithms.getDocumentFromImage(path);

                result = Algorithms.callUrlAndParseResult("en", "vi", result);

                System.out.println("Your image scanned in Vietnamese : \n" + result);
            } catch (Exception e) {
                System.out.println("Sorry, we failed scanning and translating your image...");
            }

        } else System.out.println("Your file does not exist or you gave a wrong path...");
        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Insert dictionary from file resource
     *
     * @param dictionary target dictionary
     * @param path       path to file resource
     * @return dictionary after insert new words
     */
    public static Dictionary insertFromFile(Dictionary dictionary, String path) {
        path = path.replace("/", File.separator);

        System.out.println("-----------------------------------------------------------------");
        System.out.println("Reading words from source file : " + path);

        Scanner scn = null;

        try {
            scn = new Scanner(new File(path), "UTF-8");

            while (scn.hasNextLine()) {
                String currentLine = (scn.hasNextLine()) ? scn.nextLine() : "";

                if (!path.endsWith(".cmddict")) dictionary.addWord(new Word(Algorithms.standardlize(currentLine)));
                else {
                    String cutTarget = Algorithms.standardlize(currentLine.substring(0, currentLine.indexOf("\t")));
                    String cutExplain = Algorithms.standardlize(currentLine.substring(currentLine.indexOf("\t")));

                    dictionary.addWord(cutTarget, cutExplain);
                }
            }
        } catch (Exception ioe) {
            System.out.println("Failed to load source file : " + ioe);
        } finally {
            if (scn != null) scn.close();
            System.out.println("Finished reading file.");
        }

        System.out.println("There are total : " + dictionary.getWordsContainer().size() + " word(s) in dictionary!");
        System.out.println("-----------------------------------------------------------------");

        return dictionary;
    }

    /**
     * Insert a collection from file and use dictionary to find elements which suits the value of lines in file
     *
     * @param collection collection input
     * @param dictionary dictionary to use
     * @param path       path to file
     * @return new collection - treeset which contains all words which is in the file
     */
    public static TreeSet<Word> insertFromFile(TreeSet<Word> collection, Dictionary dictionary, String path) {
        path = path.replace("/", File.separator);

        ArrayList<Word> list = new ArrayList<>(dictionary.getWordsContainer());

        System.out.println("-----------------------------------------------------------------");
        System.out.println("Reading words from source file : " + path);

        Scanner scn = null;

        try {
            scn = new Scanner(new File(path), "UTF-8");

            while (scn.hasNextLine()) {
                String currentLine = (scn.hasNextLine()) ? scn.nextLine() : "";

                collection.add(list.get(Collections.binarySearch(list, new Word(currentLine, null, null), Comparator.comparing(Word::getTarget))));
            }
        } catch (Exception ioe) {
            System.out.println("Failed to load source file : " + ioe);
        } finally {
            if (scn != null) scn.close();
            System.out.println("Finished reading file.");
        }

        System.out.println("-----------------------------------------------------------------");

        collection.remove(new Word("A", null, null));

        return collection;
    }


    /**
     * Insert all line from a file in a list of string
     *
     * @param path path to file
     * @return new collection - treeset which contains all words which is in the file
     */
    public static ArrayList<String> insertFromFile(String path) {
        path = path.replace("/", File.separator);

        ArrayList<String> list = new ArrayList<>();

        System.out.println("-----------------------------------------------------------------");
        System.out.println("Reading words from source file : " + path);

        Scanner scn = null;

        try {
            scn = new Scanner(new File(path), "UTF-8");

            while (scn.hasNextLine()) {
                String currentLine = (scn.hasNextLine()) ? scn.nextLine() : "";

                if (!currentLine.isEmpty()) list.add(currentLine);
            }
        } catch (Exception ioe) {
            System.out.println("Failed to load source file : " + ioe);
        } finally {
            if (scn != null) scn.close();
            System.out.println("Finished reading file.");
        }

        System.out.println("-----------------------------------------------------------------");

        return list;
    }

    /**
     * Fuzzy Search
     *
     * @param _word      word to search
     * @param fuzzyness  fuzzyness
     * @param dictionary target diciontary
     */
    public static ArrayList<String> doFuzzySearch(String _word, double fuzzyness, Dictionary dictionary) {
        ArrayList<String> listSuit = new ArrayList<>();

        System.out.println("Did you mean? ");

        PriorityQueue<RelatedWord> priorityQueue = new PriorityQueue<RelatedWord>(5);

        for (Word word : dictionary.getWordsContainer()) {
            // Calculate the Levenshtein-distance:
            int levenshteinDistance = Algorithms.getLevenshteinDistance(word.getTarget(), _word);

            // length() of the longer String:
            int length = Math.max(word.getTarget().length(), _word.length());

            // Calculate the score:
            double score = 1.0 - (double) levenshteinDistance / length;

            // Match?
            if (score > fuzzyness) {
                priorityQueue.add(new RelatedWord(score, word.getTarget()));
                System.out.println("--> " + word.getTarget() + " - " + word.getExplain());
            }
        }

        while (!priorityQueue.isEmpty()) listSuit.add(priorityQueue.poll().getTarget());

        return listSuit;
    }

    /**
     * Fixed a word in dictionary
     *
     * @param dictionary dictionary to fix
     * @return dictionary after fix
     */
    public static Dictionary fixWord(Dictionary dictionary) {
        System.out.println("-----------------------------------------------------------------");
        System.out.print("What word you want to fix? ");

        String target = (CommandMain.scnCommand.hasNextLine()) ? CommandMain.scnCommand.nextLine() : "";

        Word word = Algorithms.getWordInDictionary(dictionary, target);

        System.out.println();

        if (word != null) {
            try {
                dictionary.getWordsContainer().remove(word);

                System.out.print("What is the target of the word? ");

                String targetFix = (CommandMain.scnCommand.hasNextLine()) ? CommandMain.scnCommand.nextLine() : "";

                System.out.println();

                System.out.print("What is its explanation? ");

                String explainFix = (CommandMain.scnCommand.hasNextLine()) ? CommandMain.scnCommand.nextLine() : "";

                dictionary.addWord(new Word(targetFix, explainFix));

                dictionaryExportToFile(DictionaryCommandLine.getInstance().dictionary, AppMain.pathToDirectory + "resources/data/dictionaries.cmddict");

                System.out.println("Thanks for your editing...");
            } catch (Exception e) {
                System.out.println("Failed to remove word :" + e);
            }
        } else System.out.println("Your word " + target + " does not exist in dictionary!");

        System.out.println("-----------------------------------------------------------------");

        return dictionary;
    }

    /**
     * Lookup function for dictionary
     *
     * @param dictionary target dictionary
     */
    public static void dictionaryLookUp(Dictionary dictionary) {
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Dictionary's looking up service!");
        System.out.print("Please give us the word you want to find its meaning in Vietnamese : ");

        String wordToFind = (CommandMain.scnCommand.hasNextLine()) ? CommandMain.scnCommand.nextLine() : "";

        wordToFind = Algorithms.standardlize(wordToFind);

        System.out.println("Looking up for \"" + wordToFind + "\" in dictionary...\n");

        ArrayList<Word> list = new ArrayList<>(dictionary.getWordsContainer()); // Convert TreeSet into ArrayList to use search of Collections

        int indexOfWord = Algorithms.lookUpInList(list, wordToFind);

        if (indexOfWord >= 0) {
            System.out.printf("Word :\t%s\t---\t%s\n", wordToFind, list.get(indexOfWord).getExplain());

            System.out.println("------------------------------------------");
        } else {
            System.out.println("Sorry, we can't find this word in dictionary!");
            dictionarySearcher(dictionary, wordToFind);
        }

        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Searcher related words in dictionary
     *
     * @param dictionary target dictionary
     * @param wordInput  word to find relations
     */
    public static void dictionarySearcher(Dictionary dictionary, String wordInput) {
        System.out.println("-----------------------------------------------------------------");
        TreeSet<Word> filteredTree = Algorithms.filteredTree(dictionary.getWordsContainer(), (wordInput + "\\w*"));

        if (filteredTree.size() > 0) {
            System.out.println("All related words : \n");

            for (Word word : filteredTree) {
                System.out.printf("--->\t%s\t---\t%s\n", word.getTarget(), word.getExplain());
            }
        } else doFuzzySearch(wordInput, 0.5, dictionary);
    }

    /**
     * Export all words of current dictionary into file
     *
     * @param dictionary this dicitonary
     * @param path       path to target file
     */
    public static void dictionaryExportToFile(Dictionary dictionary, String path) {
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Exporting dictionary's data into file : " + path);
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new File(path));

            for (Word word : dictionary.getWordsContainer()) {
                if (!path.endsWith(".cmddict")) printWriter.println(word.toData());
                else {
                    printWriter.println(word.getTarget() + "\t" + word.getExplain());
                }
            }
        } catch (IOException ioe) {
            System.out.println("Failed to write new data into file : " + ioe);
        } finally {
            if (printWriter != null) printWriter.close();
            System.out.println("Finished exporting to file...");
        }

        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Export a list string to file
     *
     * @param list list string to export
     * @param path path to destination file
     */
    public static void dictionaryExportToFile(ArrayList<String> list, String path) {
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Exporting new word into file : " + path);
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new File(path));

            for (String string : list) printWriter.println(string);
        } catch (IOException ioe) {
            System.out.println("Failed to write new data into file : " + ioe);
        } finally {
            if (printWriter != null) printWriter.close();
            System.out.println("Finished exporting to file...");
        }

        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Export all words' targets in a treeset into file
     *
     * @param collection collection to export
     * @param path       path to target file
     */
    public static void dictionaryExportToFile(TreeSet<Word> collection, String path) {
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Exporting dictionary's data into file : " + path);
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new File(path));

            for (Word word : collection) {
                printWriter.println(word.getTarget());
            }
        } catch (IOException ioe) {
            System.out.println("Failed to write new data into file : " + ioe);
        } finally {
            if (printWriter != null) printWriter.close();
            System.out.println("Finished exporting to file...");
        }

        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Export all words of current dictionary into file with path from user's input
     *
     * @param dictionary this dicitonary
     */
    public static void dictionaryExportToFile(Dictionary dictionary) {
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Give us full path to destination file below\nOr just a name of file, we will export it in \"" + AppMain.pathToDirectory + "resources/data/\" with that name");
        System.out.print("Enter name of file here (full path or name only) : ");

        String path = CommandMain.scnCommand.nextLine();

        if (path.indexOf(File.separatorChar) != -1) dictionaryExportToFile(dictionary, path);
        else dictionaryExportToFile(dictionary, AppMain.pathToDirectory + "resources/data/" + path);

        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Translate the text using Google traslate server
     * <p>
     * Need to update to show error message
     */
    public static void textTranslator() throws Exception {
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Text translator service!");
        System.out.print("Please give us the sentence you want to find its meaning in Vietnamese : ");
        String wordToFind = (CommandMain.scnCommand.hasNextLine()) ? CommandMain.scnCommand.nextLine() : "";
        System.out.println("Looking up for meaning of your sentence...\n");
        String word = Algorithms.callUrlAndParseResult("en", "vi", wordToFind);
        System.out.println(word);
        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Set speech to run for word dialog
     */
    public static void speechSettings() {
        WordDialog.setSpeechRun(() -> {
            AudioPlayer.getInstance().play();
        });
    }

    /**
     * Set value network status to WordDialog
     */
    public static void doNetworkChecker() {
        new Thread(() -> {
            DictionaryApplication.isGoodConnection = Algorithms.checkNetwork();
        }).start();
    }

    /**
     * Create alert
     *
     * @param type    type of alert
     * @param title   title of alert
     * @param content content of alert
     * @param isWait  is a waiting alert
     */
    public static void createAlert(Alert.AlertType type, String title, String content, boolean isWait) {
        Alert alert = new Alert(type);
        alert.setTitle(title);

        alert.setContentText(content);
        if (isWait) alert.showAndWait();
        else alert.show();
    }


    ////////// Inner classes and interfaces ///////////
    public interface Runner {
        void run();
    }

    public interface Tester {
        boolean test();
    }
}
