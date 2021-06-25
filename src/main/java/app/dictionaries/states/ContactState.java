package app.dictionaries.states;

import app.AppMain;
import app.dictionaries.StateManager;
import app.dictionaries.utilities.elements.Effects;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRippler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class ContactState extends BaseState {
    private static ContactState instance = new ContactState();

    @FXML
    private AnchorPane apContact;

    @FXML
    private VBox vbxContain;

    @FXML
    private JFXButton btnNMH;

    @FXML
    private JFXButton btnTBH;

    @FXML
    private JFXButton btnBack;

    /**
     * Default constructor
     */
    public ContactState() {
        super();
    }

    /**
     * Get instance singleton
     *
     * @return instance
     */
    public static ContactState getInstance() {
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
        ///////// Key event for all ///////////
        apContact.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case ESCAPE:
                    try {
                        StateManager.getInstance().changeState(null, null);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    break;
            }
        });

        btnTBH.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case RIGHT:
                    btnNMH.requestFocus();
                    break;
                case ENTER:
                    callFacebook("https://www.facebook.com/hoatrana3");
                    break;
                case DOWN:
                    btnBack.requestFocus();
                    break;
            }
        });

        btnNMH.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case LEFT:
                    btnTBH.requestFocus();
                    break;
                case ENTER:
                    callFacebook("https://www.facebook.com/ngohoang34");
                    break;
                case DOWN:
                    btnBack.requestFocus();
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
        apContact.getChildren().add(Effects.makeRippler(vbxContain, Paint.valueOf("#FFDEBD"), JFXRippler.RipplerMask.RECT));
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
            root = FXMLLoader.load(new URL("file:" + AppMain.pathToDirectory + ("resources/UI/ContactState/ContactUI.fxml").replaceAll("/", File.separator)));
            if (root.getStylesheets().isEmpty())
                root.getStylesheets().add("file:" + AppMain.pathToDirectory + ("resources/UI/ContactState/ContactStyle.css").replaceAll("/", File.separator));
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

        btnNMH.setOnMouseClicked(e -> callFacebook("https://www.facebook.com/ngohoang34"));
        btnTBH.setOnMouseClicked(e -> callFacebook("https://www.facebook.com/hoatrana3"));
    }

    /**
     * Call default browser to open authors' facebook
     *
     * @param link link to facebook
     */
    private void callFacebook(String link) {
        try {
            Desktop.getDesktop().browse(new URI(link));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
