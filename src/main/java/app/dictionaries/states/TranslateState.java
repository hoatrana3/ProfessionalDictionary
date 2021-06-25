package app.dictionaries.states;

import app.AppMain;
import app.dictionaries.DictionaryApplication;
import app.dictionaries.StateManager;
import app.dictionaries.utilities.DictionaryManagement;
import app.dictionaries.utilities.elements.Algorithms;
import app.dictionaries.utilities.elements.Effects;
import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class TranslateState extends BaseState {
    private static TranslateState instance = new TranslateState();

    @FXML
    private JFXButton btnTranslate;

    @FXML
    private JFXButton btnBack;

    @FXML
    private AnchorPane apTranslate;

    @FXML
    private HBox hbxContain;

    @FXML
    private JFXCheckBox cbxSync;

    @FXML
    private JFXTextArea txtAInput;

    @FXML
    private JFXTextArea txtAOutput;

    @FXML
    private JFXComboBox<String> cbbType;

    private int typeOption;

    /**
     * Default constructor
     */
    public TranslateState() {
        super();
        typeOption = -1;
    }

    /**
     * Get instance singleton
     *
     * @return instance
     */
    public static TranslateState getInstance() {
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
        txtAOutput.setEditable(false);
        txtAOutput.setFocusTraversable(false);

        cbbType.getItems().addAll("VI - ENG", "ENG - VI");
        cbbType.valueProperty().addListener(e -> {
            if (cbbType.getValue().compareTo("VI - ENG") == 0) typeOption = 0;
            else typeOption = 1;

            if (!(txtAInput.getText().isEmpty() || txtAOutput.getText().isEmpty())) {
                String temp = txtAInput.getText();
                txtAInput.setText(txtAOutput.getText());
                txtAOutput.setText(temp);
            }
        });

        cbxSync.selectedProperty().addListener(e -> {
            if (cbxSync.isSelected()) callTranslate();
        });

        txtAInput.textProperty().addListener(e -> {
            if (cbxSync.isSelected()) callTranslate();
        });

        ///////// Key event for all ///////////
        apTranslate.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case ESCAPE:
                    try {
                        StateManager.getInstance().changeState(null, null);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    break;
                case ENTER:
                    callTranslate();
                    break;
            }
        });

        txtAInput.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case DOWN:
                    txtAOutput.requestFocus();
                    break;
                case LEFT:
                    btnTranslate.requestFocus();
                    break;
                case ENTER:
                    callTranslate();
                    break;
            }
        });

        txtAOutput.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case UP:
                    txtAInput.requestFocus();
                    break;
                case LEFT:
                    btnBack.requestFocus();
                    break;
            }
        });

        btnTranslate.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case ENTER:
                    callTranslate();
                    break;
                case DOWN:
                    cbbType.requestFocus();
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

        ////////// Add rippler //////////
        apTranslate.getChildren().add(Effects.makeRippler(hbxContain, Paint.valueOf("#AFF6FE"), JFXRippler.RipplerMask.RECT));
    }

    /**
     * Override function getVariables
     */
    public void getVariables(Parent root) {
    }

    /**
     * Override function enter
     */
    @Override
    public void enter() {
        Parent root = null;

        try {
            root = FXMLLoader.load(new URL("file:" + AppMain.pathToDirectory + ("resources/UI/TranslateState/TranslateUI.fxml").replaceAll("/", File.separator)));
            if (root.getStylesheets().isEmpty())
                root.getStylesheets().add("file:" + AppMain.pathToDirectory + ("resources/UI/TranslateState/TranslateStyle.css").replaceAll("/", File.separator));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.stateScene = new Scene(root);

        getVariables(root);

        if (!DictionaryApplication.isGoodConnection)
            DictionaryManagement.createAlert(Alert.AlertType.WARNING, "Cannot connect to sever", "Internet connection is required for translating!", false);
    }

    /**
     * Override function control
     *
     * @param event event to handle
     */
    @FXML
    @Override
    public void control(ActionEvent event) {
        btnBack.setOnMouseClicked(e -> {
            try {
                StateManager.getInstance().changeState(null, null);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        btnTranslate.setOnMouseClicked(e -> {
            callTranslate();
        });
    }

    /**
     * Call translate current input
     */
    public void callTranslate() {
        String input = txtAInput.getText().replaceAll("\n", System.getProperty("line.separator"));

        if (typeOption != 0 && typeOption != 1) {
            DictionaryManagement.createAlert(Alert.AlertType.WARNING, "Cannot translate now!", "You haven't change the settings yet...", true);
        } else {
            txtAOutput.setText("Translating... Please wait!");

            new Thread(() -> {
                String threadOut = "";

                try {
                    if (typeOption == 0) threadOut = Algorithms.callUrlAndParseResult("vi", "en", input);
                    else if (typeOption == 1) threadOut = Algorithms.callUrlAndParseResult("en", "vi", input);
                } catch (Exception expt) {
                    expt.printStackTrace();
                }

                txtAOutput.setText(threadOut);
            }).start();
        }
    }
}
