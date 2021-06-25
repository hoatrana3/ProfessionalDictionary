package app;

import app.dictionaries.DictionaryCommandLine;
import app.dictionaries.utilities.DictionaryManagement;

import java.io.File;
import java.util.Scanner;

/**
 * app.CommandMain
 * <p>
 * This is main class of commandline version
 */
public class CommandMain {
    public static final Scanner scnCommand = new Scanner(System.in); // Scanner for whole project to use System.in, its better than create many Scanner everywhere
    public static final String pathToDirectory = System.getProperty("user.dir") + ("/src/main/").replaceAll("/",File.separator); // Full path to the dictionaryproject_int2204_21_n1 folder

    /**
     * Clean function of application
     */
    public static void clean() {
        if (DictionaryCommandLine.getInstance().isChanged)
            DictionaryManagement.dictionaryExportToFile(DictionaryCommandLine.getInstance().dictionary, pathToDirectory + ("resources/data/dictionaries.cmddict").replaceAll("/", File.separator));

        scnCommand.close();

        System.out.println("Closing commandline dictionary...");

        System.exit(0);
    }

    /**
     * Main function
     *
     * @param args list of parameters
     */
    public static void main(String[] args) throws Exception {
        //////////////// Commandline dictionary ///////////////

        DictionaryCommandLine.getInstance().run();

        CommandMain.clean();
    }
}
