package app.dictionaries;

import app.AppMain;
import app.dictionaries.utilities.Dictionary;
import app.dictionaries.utilities.DictionaryManagement;
import app.dictionaries.utilities.elements.Word;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.TreeSet;

public class DictionaryApplication {
    public static final String appTitle = "Professional Dictionary";

    public Dictionary dictionary;

    public static TreeSet<Word> markedWords = new TreeSet<>();

    public static ArrayList<String> historyData = new ArrayList<>();

    private static DictionaryApplication instance = new DictionaryApplication();

    public static boolean isGoodConnection = false;

    public static boolean isChanged = false;

    /**
     * Default constructor
     */
    private DictionaryApplication() {
        DictionaryManagement.doNetworkChecker();

        dictionary = new Dictionary();

        DictionaryManagement.insertFromFile(this.dictionary, AppMain.pathToDirectory + "resources/data/dictionaries.dict");

        new Thread(() -> {
            DictionaryApplication.markedWords = DictionaryManagement.insertFromFile(DictionaryApplication.markedWords, this.dictionary, AppMain.pathToDirectory + "resources/data/markedWords.dict");
            DictionaryApplication.historyData = DictionaryManagement.insertFromFile(AppMain.pathToDirectory + "resources/data/user.history");
        }).start();

        DictionaryManagement.speechSettings();
    }

    /**
     * Get instance singleton
     *
     * @return instance
     */
    public static DictionaryApplication getInstance() {
        return instance;
    }

    /**
     * Dictionary start function using JavaFX
     */
    public void start(Stage primaryStage) throws Exception {
        StateManager.getInstance().primaryStage = primaryStage;

        primaryStage.setTitle(DictionaryApplication.appTitle);
        primaryStage.setResizable(false);

        primaryStage.getIcons().add(new Image("file:" + AppMain.pathToDirectory + "resources/UI/MainState/images/icon.png"));
        primaryStage.setScene(StateManager.getInstance().getCurrentState().getStateScene());
        primaryStage.show();
    }
}
