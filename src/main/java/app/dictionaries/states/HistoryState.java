package app.dictionaries.states;

import app.AppMain;
import app.dictionaries.DictionaryApplication;
import app.dictionaries.StateManager;
import app.dictionaries.states.general.AddDialog;
import app.dictionaries.states.general.WordDialog;
import app.dictionaries.utilities.Dictionary;
import app.dictionaries.utilities.DictionaryManagement;
import app.dictionaries.utilities.elements.Algorithms;
import app.dictionaries.utilities.elements.Effects;
import app.dictionaries.utilities.elements.Word;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class HistoryState extends BaseState {
    private static HistoryState instance = new HistoryState();

    @FXML
    private VBox vbxContain;

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXTextField txtFSearch;

    @FXML
    private JFXListView<String> lvwHistory;

    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnRevert;

    @FXML
    private AnchorPane apHistory;

    /**
     * Default constructor
     */
    public HistoryState() {
        super();
    }

    /**
     * Get instance singleton
     *
     * @return instance
     */
    public static HistoryState getInstance() {
        return instance;
    }

    /**
     * Override function init
     *
     * @throws Exception
     */
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ////////// History view //////////
        ArrayList<String> cuttedHistoryList = cutHistoryData(DictionaryApplication.historyData);

        lvwHistory.setItems(FXCollections.observableArrayList(cuttedHistoryList));

        Label placeHolder = new Label("Nothing in history...");
        placeHolder.setStyle("-fx-text-fill: White");

        lvwHistory.setPlaceholder(placeHolder);

        FilteredList<String> filter = new FilteredList<>(FXCollections.observableArrayList(cuttedHistoryList), null);


        ///////// Key event for all ///////////
        apHistory.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case ESCAPE:
                    if (!txtFSearch.getText().isEmpty()) txtFSearch.setText("");
                    else StateManager.getInstance().changeState(null, null);

                    break;
                case DOWN:
                    txtFSearch.requestFocus();
                    break;
            }
        });

        txtFSearch.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case DOWN:
                    lvwHistory.requestFocus();
                    lvwHistory.getSelectionModel().select(0);
                    break;
                case ESCAPE:
                    apHistory.requestFocus();
                    break;
                default:
                    txtFSearch.end();

                    if (!txtFSearch.getText().isEmpty()) {
                        filter.setPredicate(p -> p.toLowerCase().contains(txtFSearch.getText().toLowerCase()));
                        lvwHistory.setItems(filter);
                    }

                    break;
            }
        });

        lvwHistory.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case UP:
                    if (lvwHistory.getSelectionModel().getSelectedIndex() <= 0) txtFSearch.requestFocus();
                    break;
                case DOWN:
                    if (lvwHistory.getSelectionModel().getSelectedIndex() >= DictionaryApplication.historyData.size() - 1)
                        btnBack.requestFocus();
                    break;
                case ENTER:
                    revertAction(lvwHistory);
                case ESCAPE:
                    apHistory.requestFocus();
                    break;
            }
        });

        btnBack.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case ENTER:
                    try {
                        StateManager.getInstance().changeState(null, null);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    break;
            }
        });

        btnClear.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case ENTER:
                    DictionaryApplication.historyData.clear();
                    StateManager.getInstance().reloadState();
                    break;
            }
        });

        btnRevert.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case ENTER:
                    revertAction(lvwHistory);
                    break;
            }
        });

        ////////// Add rippler //////////
        apHistory.getChildren().add(Effects.makeRippler(vbxContain, Paint.valueOf("#FFAC6B"), JFXRippler.RipplerMask.RECT));
    }

    /**
     * Override function getVariables
     */
    public void getVariables(Parent root) {
        apHistory = (AnchorPane) root;
        lvwHistory = (JFXListView<String>) root.lookup("#lvwHistory");
    }

    /**
     * Override function enter
     */
    @Override
    public void enter() {
        Parent root = null;

        try {
            root = FXMLLoader.load(new URL("file:" + AppMain.pathToDirectory + ("resources/UI/HistoryState/HistoryUI.fxml").replaceAll("/", File.separator)));
            if (root.getStylesheets().isEmpty())
                root.getStylesheets().add("file:" + AppMain.pathToDirectory + ("resources/UI/HistoryState/HistoryStyle.css").replaceAll("/", File.separator));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.stateScene = new Scene(root);

        getVariables(root);
    }

    /**
     * Override function control
     *
     * @param event event to handle
     */
    @Override
    public void control(ActionEvent event) {
        btnBack.setOnMouseClicked(e -> {
            try {
                StateManager.getInstance().changeState(null, null);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        btnClear.setOnMouseClicked(e -> {
            DictionaryApplication.historyData.clear();
            StateManager.getInstance().reloadState();
        });

        btnRevert.setOnMouseClicked(e -> revertAction(lvwHistory));
    }

    /**
     * Cut history data to get word's target in it
     *
     * @param historyData history data
     * @return
     */
    private static ArrayList<String> cutHistoryData(ArrayList<String> historyData) {
        ArrayList<String> resultList = new ArrayList<>();

        for (String data : historyData) {
            String result = "";

            try {
                result = data.substring(0, data.indexOf("||"));

                data = data.substring(data.indexOf("||") + 2);

                result = result + "\"" + Algorithms.standardlize(data.substring(0, data.indexOf("||"))) + "\"";
                result = result.replaceAll("\\|\\|", "-");
            } catch (IndexOutOfBoundsException ioobe) {
                System.out.println("Failed to cut string : " + ioobe);
            }

            if (!result.isEmpty()) resultList.add(result);
        }

        return resultList;
    }

    /**
     * Revert action for revert button
     *
     * @param lvwHistory list of history
     */
    private static void revertAction(JFXListView<String> lvwHistory) {
        String current = lvwHistory.getSelectionModel().getSelectedItem();
        if (current != null) {
            DictionaryApplication.isChanged = true;

            if (current.startsWith("Added")) {
                System.out.println("---------------------\nReverting added word...");

                Word wordAdded = Algorithms.getWordInDictionary(DictionaryApplication.getInstance().dictionary, current.substring(current.indexOf("\"") + 1, current.lastIndexOf("\"")));

                if (wordAdded != null)
                    DictionaryApplication.getInstance().dictionary.getWordsContainer().remove(wordAdded);

                DictionaryApplication.historyData.remove(lvwHistory.getSelectionModel().getSelectedIndex());

                StateManager.getInstance().reloadState();
            } else if (current.startsWith("Removed")) {
                System.out.println("---------------------\nReverting removed word...");

                int index = lvwHistory.getSelectionModel().getSelectedIndex();

                String data = DictionaryApplication.historyData.get(index);

                data = data.substring(data.indexOf("||") + 2);

                DictionaryApplication.getInstance().dictionary.addWord(new Word(data));

                DictionaryApplication.historyData.remove(lvwHistory.getSelectionModel().getSelectedIndex());

                StateManager.getInstance().reloadState();
            }
        }
    }
}
