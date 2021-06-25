package app;

import app.dictionaries.DictionaryApplication;
import app.dictionaries.utilities.DictionaryManagement;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;

/**
 * app.AppMain
 * <p>
 * This is main class of application UI version
 */
public class AppMain extends Application {
    public static final String pathToDirectory = System.getProperty("user.dir") + ("/src/main/").replaceAll("/", File.separator); // Full path to the dictionaryproject_int2204_21_n1 folder

    /**
     * This function is override start function in Application class of JavaFX
     * It is entry point for application, put everything you want to run in application dictionary here - Not the commandline dicitonary
     *
     * @param primaryStage first stage, it is the window of application - the very first parameter of start function
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        DictionaryApplication.getInstance().start(primaryStage);
    }

    /**
     * Clean function of application
     */
    public static void clean() {
        DictionaryManagement.dictionaryExportToFile(DictionaryApplication.markedWords, pathToDirectory + ("resources/data/markedWords.dict").replaceAll("/", File.separator));
        DictionaryManagement.dictionaryExportToFile(DictionaryApplication.historyData, pathToDirectory + ("resources/data/user.history").replaceAll("/", File.separator));

        if (DictionaryApplication.isChanged)
            DictionaryManagement.dictionaryExportToFile(DictionaryApplication.getInstance().dictionary, pathToDirectory + ("resources/data/dictionaries.dict").replaceAll("/", File.separator));

        System.out.println("Closing application dictionary...");

        System.exit(0);
    }

    /**
     * Main function
     *
     * @param args list of parameters
     */
    public static void main(String[] args) throws Exception {
        //////////////// Application dictionary ///////////////

        launch(args); // Call launch from JavaFX to run the application

        AppMain.clean();
    }
}
