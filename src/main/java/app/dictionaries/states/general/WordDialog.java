package app.dictionaries.states.general;

import app.AppMain;
import app.dictionaries.DictionaryApplication;
import app.dictionaries.StateManager;
import app.dictionaries.states.BaseState;
import app.dictionaries.states.LoginState;
import app.dictionaries.utilities.Dictionary;
import app.dictionaries.utilities.DictionaryManagement;
import app.dictionaries.utilities.elements.Algorithms;
import app.dictionaries.utilities.elements.AudioPlayer;
import app.dictionaries.utilities.elements.Effects;
import app.dictionaries.utilities.elements.Word;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXToggleButton;
import com.voicerss.tts.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

/**
 * WordDialog
 */
public class WordDialog extends BaseState {
    public VBox root;

    private Word wordToShow;

    @FXML
    private Label lblTarget;

    @FXML
    private JFXTextArea txtAMore;

    @FXML
    private JFXToggleButton tglMark;

    @FXML
    private Label lblPronounce;

    public static DictionaryManagement.Runner speechRun = () -> {
    };

    public static boolean isOnWordDialog = false;

    /**
     * Constructor
     *
     * @param word initial word to show
     */
    public WordDialog(Word word) {
        wordToShow = word;
        enter();
    }

    /**
     * Override function init
     *
     * @throws Exception
     */
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * Override function getVariables
     */
    public void getVariables(Parent root) {
        lblTarget = (Label) root.lookup("#lblTarget");
        lblPronounce = (Label) root.lookup("#lblPronounce");
        txtAMore = (JFXTextArea) root.lookup("#txtAMore");

        tglMark = (JFXToggleButton) root.lookup("#tglMark");
    }

    /**
     * Override function enter
     */
    @Override
    public void enter() {
        try {
            root = FXMLLoader.load(new URL("file:" + AppMain.pathToDirectory + ("resources/UI/General/WordDialogUI.fxml").replaceAll("/", File.separator)));
            if (root.getStylesheets().isEmpty())
                root.getStylesheets().add("file:" + AppMain.pathToDirectory + ("resources/UI/General/WordDialogStyle.css").replaceAll("/", File.separator));
        } catch (Exception e) {
            e.printStackTrace();
        }

        root.setVisible(false);
        root.setDisable(true);

        getVariables(root);

        if (lblTarget != null) lblTarget.setText(wordToShow.getTarget());
        if (lblPronounce != null) lblPronounce.setText(wordToShow.getPronunciation());
        if (txtAMore != null) {
            txtAMore.setText(wordToShow.getExplain());

            if (!LoginState.isTryUser) txtAMore.setEditable(true);
            else txtAMore.setEditable(false);
        }

        if (DictionaryApplication.markedWords.contains(wordToShow)) this.tglMark.setSelected(true);

        // Event for toggle marked word
        tglMark.selectedProperty().addListener(e -> {
            if (tglMark.isSelected()) {
                DictionaryApplication.markedWords.add(wordToShow);
            } else {
                DictionaryApplication.markedWords.remove(wordToShow);
            }
        });

        // Detail editted
        txtAMore.textProperty().addListener(e -> {
            wordToShow.setExplain(txtAMore.getText());
            DictionaryApplication.isChanged = true;
        });
    }

    /**
     * Override function control
     *
     * @param event event to handle
     */
    @Override
    public void control(ActionEvent event) {
    }

    /////////// Other methods ////////////

    /**
     * Set a word dialog on apMain
     *
     * @param word word to show
     */
    public static void callWordDialog(AnchorPane parent, Word word, boolean disableBehind) {
        if (!isOnWordDialog) {
            if (parent != null) {
                WordDialog wordDialog = new WordDialog(word);
                ObservableList<Node> childrenList = parent.getChildren();

                new Thread(() -> {
                    Algorithms.createSoundFile(word.getTarget());
                }).start();

                // Come in word dialog
                childrenList.add(wordDialog.root);

                for (int i = 0; i < childrenList.size() - 1; ++i) {
                    if (disableBehind) childrenList.get(i).setDisable(true);
                    Effects.makeGuassianBlur(childrenList.get(i), 4.0);
                }

                Node child = childrenList.get(childrenList.size() - 1);

                child.setVisible(true);
                Effects.makeFadeTransition(child, 500, 0.0, 1.0, ev -> {
                    child.setDisable(false);
                });

                // Get out of word dialog
                JFXButton btnDone = (JFXButton) childrenList.get(childrenList.size() - 1).lookup("#btnDone");
                btnDone.setOnMouseClicked(action -> {
                    closeWordDialog(childrenList, child, parent.lookup("#txtFSearch"));
                });

                // Listen settings
                JFXButton btnListen = (JFXButton) child.lookup("#btnListen");
                Label lblInfoLoad = (Label) child.lookup("#lblInfoLoad");
                btnListen.requestFocus();

                lblInfoLoad.setTextAlignment(TextAlignment.CENTER);

                // Load speech of current word and effect to the button listen up to connection
                new Thread(() -> {
                    lblInfoLoad.setText("Click to listen and save to use in offline.");
                    lblInfoLoad.setStyle("-fx-text-fill: #444444");

                    if (btnListen != null) {
                        btnListen.setVisible(true);

                        Effects.makeFadeTransition(btnListen, 1000, btnListen.getOpacity(), 0.5, e -> btnListen.setDisable(false));
                    }
                }).start();

                if (btnListen != null) btnListen.setOnMouseClicked(e -> {
                    DictionaryManagement.doNetworkChecker();

                    changeStatus(lblInfoLoad, word, "Loading and saving speech, click to check again", "-fx-text-fill: #CFAE08", () -> DictionaryApplication.isGoodConnection);
                    changeStatus(lblInfoLoad, word, "Loaded and saved successfully", "-fx-text-fill: #05CB7D", () -> checkFileSpeechExisted(word));
                    changeStatus(lblInfoLoad, word, "Failed to connect to sever!", "-fx-text-fill: #D21D04", () -> !(DictionaryApplication.isGoodConnection || checkFileSpeechExisted(word)));

                    if (DictionaryApplication.isGoodConnection || checkFileSpeechExisted(word)) {
                        if (lblInfoLoad.getText().equalsIgnoreCase("Failed to connect to sever!")
                                || lblInfoLoad.getText().equalsIgnoreCase("Loading and saving speech...")) {
                            if (DictionaryApplication.isGoodConnection) {
                                if (checkFileSpeechExisted(word))
                                    changeStatus(lblInfoLoad, word, "Loaded and saved successfully", "-fx-text-fill: #05CB7D", () -> checkFileSpeechExisted(word));
                                else {
                                    new Thread(() -> Algorithms.createSoundFile(word.getTarget())).start();
                                }
                            }
                        }
                    } else {
                        if (!checkFileSpeechExisted(word)) {
                            if (!lblInfoLoad.getText().equalsIgnoreCase("Failed to connect to sever!"))
                                changeStatus(lblInfoLoad, word, "Failed to connect to sever!", "-fx-text-fill: #D21D04", () -> true);

                            DictionaryManagement.createAlert(Alert.AlertType.ERROR, "Failed to connect to sever!", "Please check your internet connection to load and save this word's speech offline", true);
                        }
                    }

                    // Play sound
                    if (speechRun != null) speechRun.run();
                    else {
                        System.out.println("Nothing for run speech!!!");
                    }

                    // Re-load the button effect to prevent user from spamming click
                    btnListen.setDisable(true);
                    Effects.makeFadeTransition(btnListen, 500, btnListen.getOpacity(), 0.0,
                            ev -> {
                                Effects.makeFadeTransition(btnListen, 500, btnListen.getOpacity(), 0.5, event -> {
                                    if (checkFileSpeechExisted(word)) {
                                        Effects.makeFadeTransition(btnListen, 500, btnListen.getOpacity(), 1.0, null);
                                    }

                                    btnListen.setDisable(false);
                                });
                            });
                });
                else {
                    System.out.println("Button is null");
                }

                // Button remove
                JFXButton btnRemove = (JFXButton) child.lookup("#btnRemove");
                if (LoginState.isTryUser) Effects.makeFadeTransition(btnRemove, 500, 1.0, 0.5, null);

                btnRemove.setOnMouseClicked(e -> {
                    if (!LoginState.isTryUser) {
                        try {
                            DictionaryApplication.getInstance().dictionary.getWordsContainer().remove(word);
                            DictionaryApplication.markedWords.remove(word);

                            DictionaryApplication.historyData.add("Removed word : ||" + word.toData());
                        } catch (Exception exp) {
                            System.out.println("Failed to remove word : " + exp);
                        }

                        DictionaryApplication.isChanged = true;
                        StateManager.getInstance().reloadState();

                        isOnWordDialog = false;
                    } else DictionaryManagement.createAlert(Alert.AlertType.ERROR, "Try user!", "This feature is not for trial version!\nPlease log in and enjoy it!", true);
                });
            }

            isOnWordDialog = true;
        }
    }

    /**
     * Close word dialog
     */
    public static void closeWordDialog(ObservableList<Node> childrenList, Node child, Node toFocus) {
        child.setDisable(true);

        Effects.makeFadeTransition(child, 500, child.getOpacity(), 0.0, ev -> {
            for (int i = 0; i < childrenList.size() - 1; ++i) {
                childrenList.get(i).setEffect(null);
                childrenList.get(i).setDisable(false);
            }

            childrenList.get(childrenList.size() - 1).setVisible(false);
            childrenList.remove(childrenList.size() - 1);

            toFocus.requestFocus();
        });

        isOnWordDialog = false;
    }

    /**
     * Set speech lambda function for word dialog
     *
     * @param speechRun input lambda
     */
    public static void setSpeechRun(DictionaryManagement.Runner speechRun) {
        WordDialog.speechRun = speechRun;
    }

    /**
     * Change status of connection
     *
     * @param status      label status to change
     * @param word        current word
     * @param destination status destination
     * @param style       style to set
     * @param tester      test function
     */
    private static void changeStatus(Label status, Word word, String destination, String style, DictionaryManagement.Tester tester) {
        if (tester.test()) {
            Effects.makeFadeTransition(status, 100, status.getOpacity(), 0.0, actionEvent -> {
                status.setText(destination);
                status.setStyle(style);
                Effects.makeFadeTransition(status, 250, status.getOpacity(), 1.0, null);
            });
        }
    }

    /**
     * Check file speech of a word exist
     *
     * @param word word to check
     * @return whether file speech exist
     */
    private static boolean checkFileSpeechExisted(Word word) {
        return Algorithms.checkFileExist(AudioPlayer.filename + word.getTarget().replaceAll("\\s+", "") + ".mp3");
    }
}


