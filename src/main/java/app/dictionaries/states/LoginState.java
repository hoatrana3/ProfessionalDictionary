package app.dictionaries.states;

import app.AppMain;
import app.dictionaries.StateManager;
import app.dictionaries.utilities.DictionaryManagement;
import app.dictionaries.utilities.elements.Effects;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginState extends BaseState {
    private static LoginState instance = new LoginState();

    public static String adminUser = "double_h";
    public static String adminPassword = "double_h";

    public static boolean isTryUser = true;

    @FXML
    private JFXPasswordField txtFPass;

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnLogin;

    @FXML
    private AnchorPane apLogin;

    @FXML
    private Pane pnLoged;

    @FXML
    private Pane pnRippler;

    @FXML
    private VBox vbxLogin;

    @FXML
    private JFXTextField txtFUser;

    @FXML
    private JFXButton btnTry;

    /**
     * Default constructor
     */
    public LoginState() {
        super();
    }

    /**
     * Get instance singleton
     *
     * @return instance
     */
    public static LoginState getInstance() {
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
        //////// Setup validator for username and password input //////////

        if (isTryUser) {
            pnRippler.setVisible(true);
            pnRippler.setDisable(false);
            pnLoged.setVisible(false);
            pnLoged.setDisable(true);
        } else {
            pnLoged.setVisible(true);
            pnLoged.setDisable(false);
            pnRippler.setVisible(false);
            pnRippler.setDisable(true);
        }

        // Username validator
        ValidatorBase validatorUserEmpty = new RequiredFieldValidator();
        validatorUserEmpty.setMessage("No username given!");

        ValidatorBase validatorWrongUser = new ValidatorBase() {
            @Override
            protected void eval() {
                if (txtFUser.getText().toLowerCase().compareTo(adminUser.toLowerCase()) == 0) this.hasErrors.set(false);
                else this.hasErrors.set(true);
            }
        };
        validatorWrongUser.setMessage("Wrong username");

        txtFUser.getValidators().addAll(validatorUserEmpty, validatorWrongUser);
        txtFUser.focusedProperty().addListener(e -> {
            if (txtFUser.isFocused()) {
                txtFUser.resetValidation();
            } else {
                txtFUser.validate();
            }
        });

        // Password validator
        ValidatorBase validatorPassEmpty = new RequiredFieldValidator();
        validatorPassEmpty.setMessage("No password given!");

        ValidatorBase validatorWrongPass = new ValidatorBase() {
            @Override
            protected void eval() {
                if (txtFPass.getText().toLowerCase().compareTo(adminPassword.toLowerCase()) == 0)
                    this.hasErrors.set(false);
                else this.hasErrors.set(true);
            }
        };
        validatorWrongPass.setMessage("Wrong password");

        txtFPass.getValidators().addAll(validatorUserEmpty, validatorWrongPass);
        txtFPass.focusedProperty().addListener(e -> {
            if (txtFPass.isFocused()) {
                txtFPass.resetValidation();
            } else {
                txtFPass.validate();
            }
        });

        ///////// Key event for all ///////////
        apLogin.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case ESCAPE:
                    if (txtFUser.getText().isEmpty() && txtFPass.getText().isEmpty()) {
                        Effects.makeFadeTransition(apLogin, 300, 1.0, 0.0, e -> {
                            AppMain.clean();
                        });
                    } else {
                        txtFUser.setText("");
                        txtFPass.setText("");
                    }
                    break;
                case DOWN:
                    txtFUser.requestFocus();
                    break;
                case ENTER:
                    if (isTryUser) {
                        if ((txtFUser.getText().toLowerCase().compareTo(adminUser.toLowerCase()) == 0)
                                && (txtFPass.getText().toLowerCase().compareTo(adminPassword.toLowerCase()) == 0)) {
                            isTryUser = false;
                            StateManager.getInstance().changeState(MainState.getInstance(), null);
                        } else {
                            if (!btnTry.isFocused()) DictionaryManagement.createAlert(Alert.AlertType.ERROR, "Wrong username or password", "If you don't have your account, please contact us via email contacts@doubleh.global.com.\n"
                                    + "You still can use this dictionary with trial version.\nEnjoy!", true);
                        }
                    } else {
                        StateManager.getInstance().changeState(MainState.getInstance(), null);
                    }
                    break;
            }
        });

        txtFUser.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case UP:
                    apLogin.requestFocus();
                    break;
                case DOWN:
                    txtFPass.requestFocus();
                    break;
            }
        });

        txtFPass.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case UP:
                    txtFUser.requestFocus();
                    break;
                case DOWN:
                     btnLogin.requestFocus();
                    break;
            }
        });

        btnLogin.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case RIGHT:
                    btnTry.requestFocus();
                    break;
                case ENTER:
                    if ((txtFUser.getText().compareTo(adminUser) == 0)
                            && (txtFPass.getText().compareTo(adminPassword) == 0)) {
                        isTryUser = false;
                        StateManager.getInstance().changeState(MainState.getInstance(), null);
                    } else {
                        DictionaryManagement.createAlert(Alert.AlertType.ERROR, "Wrong username or password", "If you don't have your account, please contact us via email contacts@doubleh.global.com.\n"
                                + "You still can use this dictionary with trial version.\nEnjoy!", true);
                    }
                    break;
            }
        });

        btnTry.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case LEFT:
                    btnLogin.requestFocus();
                    break;
                case ENTER:
                    isTryUser = true;
                    StateManager.getInstance().changeState(MainState.getInstance(), null);
                    break;
            }
        });

        ////////// Add rippler //////////
        apLogin.getChildren().add(Effects.makeRippler(pnRippler, Paint.valueOf("#FFFFE8"), JFXRippler.RipplerMask.RECT));
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
            root = FXMLLoader.load(new URL("file:" + AppMain.pathToDirectory + ("resources/UI/LoginState/LoginUI.fxml").replaceAll("/", File.separator)));
            if (root.getStylesheets().isEmpty())
                root.getStylesheets().add("file:" + AppMain.pathToDirectory + ("resources/UI/MainState/LoginStyle.css").replaceAll("/", File.separator));
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
        btnLogin.setOnMouseClicked(e -> {
            if ((txtFUser.getText().compareTo(adminUser) == 0)
                    && (txtFPass.getText().compareTo(adminPassword) == 0)) {
                isTryUser = false;
                StateManager.getInstance().changeState(MainState.getInstance(), null);
            } else {
                DictionaryManagement.createAlert(Alert.AlertType.ERROR, "Wrong username or password", "If you don't have your account, please contact us via email contacts@doubleh.global.com.\n"
                        + "You still can use this dictionary with trial version.\nEnjoy!", true);
            }
        });

        btnBack.setOnMouseClicked(e -> {
            isTryUser = false;
            StateManager.getInstance().changeState(MainState.getInstance(), null);
        });

        btnTry.setOnMouseClicked(e -> {
            isTryUser = true;
            StateManager.getInstance().changeState(MainState.getInstance(), null);
        });
    }
}
