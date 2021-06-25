package app.dictionaries.states.general;

import app.AppMain;
import app.dictionaries.DictionaryApplication;
import app.dictionaries.StateManager;
import app.dictionaries.states.BaseState;
import app.dictionaries.states.LoginState;
import app.dictionaries.utilities.DictionaryManagement;
import app.dictionaries.utilities.elements.Algorithms;
import app.dictionaries.utilities.elements.AudioPlayer;
import app.dictionaries.utilities.elements.Effects;
import app.dictionaries.utilities.elements.Word;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.voicerss.tts.*;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

/**
 * WordDialog
 */
public class AddDialog extends BaseState {
    public VBox root;

    public static Word resultWord;

    @FXML
    private JFXToggleButton tglMark;

    @FXML
    private JFXTextField txtFPronounce;

    @FXML
    private JFXTextField txtFTarget;

    private static FileChooser fileChooser;
    private static File fileChosen;

    private static String pathToAudio;

    public static boolean isOnAddDialog = false;

    /**
     * Constructor
     *
     * @param target initial target
     */
    public AddDialog(String target) {
        resultWord = new Word();
        resultWord.setTarget(target);

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
        txtFTarget = (JFXTextField) root.lookup("#txtFTarget");
        txtFPronounce = (JFXTextField) root.lookup("#txtFPronounce");

        tglMark = (JFXToggleButton) root.lookup("#tglMark");
    }

    /**
     * Override function enter
     */
    @Override
    public void enter() {
        try {
            root = FXMLLoader.load(new URL("file:" + AppMain.pathToDirectory + ("resources/UI/General/AddDialogUI.fxml").replaceAll("/", File.separator)));
            if (root.getStylesheets().isEmpty())
                root.getStylesheets().add("file:" + AppMain.pathToDirectory + ("resources/UI/General/AddDialogStyle.css").replaceAll("/", File.separator));
        } catch (Exception e) {
            e.printStackTrace();
        }

        root.setVisible(false);

        getVariables(root);

        if (txtFTarget != null) txtFTarget.setText(resultWord.getTarget());

        if (txtFPronounce != null) txtFPronounce.setText("/" + resultWord.getTarget() + "/");

        if (DictionaryApplication.markedWords.contains(resultWord)) this.tglMark.setSelected(true);

        // Event for toggle marked word
        tglMark.selectedProperty().addListener(e -> {
            if (tglMark.isSelected()) {
                DictionaryApplication.markedWords.add(resultWord);
            } else {
                DictionaryApplication.markedWords.remove(resultWord);
            }
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
     * Set a add dialog on apMain
     *
     * @param target target to show
     */
    public static void callAddDialog(AnchorPane parent, String target, boolean disableBehind) {
        if (!isOnAddDialog) {
            fileChooser = new FileChooser();
            fileChosen = null;

            if (parent != null) {
                AddDialog addDialog = new AddDialog(target);
                ObservableList<Node> childrenList = parent.getChildren();

                // Come in add dialog
                childrenList.add(addDialog.root);

                for (int i = 0; i < childrenList.size() - 1; ++i) {
                    if (disableBehind) childrenList.get(i).setDisable(true);
                    Effects.makeGuassianBlur(childrenList.get(i), 4.0);
                }

                Node child = childrenList.get(childrenList.size() - 1);

                child.setVisible(true);
                Effects.makeFadeTransition(child, 500, 0.0, 1.0, ev -> {
                    child.setDisable(false);
                });

                // Get out of add dialog
                JFXButton btnClose = (JFXButton) childrenList.get(childrenList.size() - 1).lookup("#btnClose");
                btnClose.setOnMouseClicked(action -> {
                    closeAddDialog(childrenList, child, parent.lookup("#txtFSearch"));
                });

                // file chooser settings
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.mp4", "*.wav", "*.ogg")
                );

                // Audio chooser settings
                JFXButton btnAudio = (JFXButton) child.lookup("#btnAudio");
                btnAudio.setOnMouseClicked(e -> chooseAudio(btnAudio));

                // Save settings
                JFXButton btnSave = (JFXButton) child.lookup("#btnSave");
                JFXTextArea txtAMore = (JFXTextArea) child.lookup("#txtAMore");
                JFXTextField txtFPronounce = (JFXTextField) child.lookup("#txtFPronounce");
                JFXTextField txtFTarget = (JFXTextField) child.lookup("#txtFTarget");

                txtFTarget.requestFocus();

                btnSave.setOnMouseClicked(e -> {
                    resultWord.setExplain(txtAMore.getText());
                    resultWord.setPronunciation(txtFPronounce.getText());
                    resultWord.setTarget(Algorithms.standardlize(txtFTarget.getText()));

                    saveNewWord(resultWord);

                    StateManager.getInstance().reloadState();

                    isOnAddDialog = false;
                    DictionaryApplication.isChanged = true;
                });

                txtAMore.textProperty().addListener(text -> {
                    resultWord.setExplain(txtAMore.getText());
                });
            }

            isOnAddDialog = true;
        }
    }

    /**
     * Close word dialog
     */
    public static void closeAddDialog(ObservableList<Node> childrenList, Node child, Node toFocus) {
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

        isOnAddDialog = false;
    }

    /**
     * Choose file and scan
     */
    private static void chooseAudio(JFXButton btnAudio) {
        fileChosen = fileChooser.showOpenDialog(btnAudio.getParent().getParent().getScene().getWindow());

        if (fileChosen != null) {
            AudioPlayer.getInstance().play(fileChosen.getAbsolutePath());
            pathToAudio = fileChosen.getAbsolutePath();
        }
    }

    /**
     * Save new word into dictionary
     */
    private static void saveNewWord(Word newWord) {
        if (DictionaryApplication.getInstance().dictionary.getWordsContainer().contains(newWord)) {
            DictionaryManagement.createAlert(Alert.AlertType.ERROR, "Cannot add this word", "This word already exist! Please check again...", true);
            return;
        }

        if (newWord.getTarget().isEmpty() || newWord.getExplain().isEmpty()) {
            DictionaryManagement.createAlert(Alert.AlertType.ERROR, "Cannot add this word", "Information is empty! Please fill it...", true);
            return;
        }

        DictionaryApplication.getInstance().dictionary.getWordsContainer().add(newWord);
        new Thread(() -> {
            DictionaryApplication.historyData.add("Added new word : ||" + newWord.toData());

            if (fileChosen != null) {
                try {
                    System.out.println("Copying audio file into resource folder...");
                    Files.copy(fileChosen.toPath(), new File(AppMain.pathToDirectory + ("resources/music/").replaceAll("/", File.separator) + fileChosen.getName().replaceAll("\\s+", "") + ".mp3").toPath());
                } catch (IOException ioe) {
                    DictionaryManagement.createAlert(Alert.AlertType.ERROR, "Can't save audio file!", "There was something wrong when saving your word's audio, please try again!", false);
                }
            }
        }).start();
    }
}


