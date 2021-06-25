package app.dictionaries.states;

import app.AppMain;
import app.dictionaries.StateManager;
import app.dictionaries.utilities.DictionaryManagement;
import app.dictionaries.utilities.elements.Algorithms;
import app.dictionaries.utilities.elements.Effects;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ImgReaderState extends BaseState {
    private static ImgReaderState instance = new ImgReaderState();

    @FXML
    private ImageView imgChosen;

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnBrowser;

    @FXML
    private JFXTextArea txtAResult;

    @FXML
    private HBox hbxImgReader;

    @FXML
    private JFXTextField txtFPath;

    @FXML
    private AnchorPane apImgReader;

    @FXML
    private JFXTextArea txtAScan;

    private final FileChooser fileChooser = new FileChooser();
    private File fileChosen;

    /**
     * Default constructor
     */
    public ImgReaderState() {
        super();
    }

    /**
     * Get instance singleton
     *
     * @return instance
     */
    public static ImgReaderState getInstance() {
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
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.bmp", "*.tiff", "*.gif")
        );

        ///////// Key event for all ///////////
        apImgReader.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case ENTER:
                    chooseAndScan();
                    break;
                case ESCAPE:
                    try {
                        StateManager.getInstance().changeState(null, null);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    break;
            }
        });

        txtAScan.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case RIGHT:
                    txtFPath.requestFocus();
                    break;
            }
        });

        txtAResult.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case LEFT:
                    btnBrowser.requestFocus();
                    break;
            }
        });

        btnBrowser.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case ENTER:
                    chooseAndScan();
                    break;
                case DOWN:
                    btnBack.requestFocus();
                    break;
            }
        });

        txtFPath.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case DOWN:
                    btnBack.requestFocus();
                    break;
                case RIGHT:
                    btnBrowser.requestFocus();
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
        apImgReader.getChildren().add(Effects.makeRippler(hbxImgReader, Paint.valueOf("#1db8c3"), JFXRippler.RipplerMask.RECT));
    }

    /**
     * Override function getVariables
     */
    public void getVariables(Parent root) {
        txtFPath = (JFXTextField) root.lookup("#txtFPath");
        imgChosen = (ImageView) root.lookup("#imgChosen");

        txtAScan = (JFXTextArea) root.lookup("#txtAScan");
    }

    /**
     * Override function enter
     */
    @Override
    public void enter() {
        Parent root = null;

        try {
            root = FXMLLoader.load(new URL("file:" + AppMain.pathToDirectory + ("resources/UI/ImgReaderState/ImgReaderUI.fxml").replaceAll("/", File.separator)));
            if (root.getStylesheets().isEmpty())
                root.getStylesheets().add("file:" + AppMain.pathToDirectory + ("resources/UI/ImgReaderState/ImgReaderStyle.css").replaceAll("/", File.separator));
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

        btnBrowser.setOnMouseClicked(e -> {
            chooseAndScan();
        });
    }

    /**
     * Do scan image then translate
     */
    private void doScanAndTranslate() {
        new Thread(() -> {
            if (txtAScan != null) txtAScan.setText(Algorithms.getDocumentFromImage(fileChosen.getAbsolutePath()));
            txtAResult.setText("Translating... Please wait!");

            new Thread(() -> {
                try {
                    txtAResult.setText(Algorithms.callUrlAndParseResult("en", "vi", txtAScan.getText()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }).start();
    }

    /**
     * Choose file and scan
     */
    private void chooseAndScan() {
        fileChosen = fileChooser.showOpenDialog(btnBrowser.getParent().getScene().getWindow());

        if (fileChosen != null) {
            if (txtFPath != null) txtFPath.setText(fileChosen.getAbsolutePath());

            try {
                BufferedImage bufferedImage = ImageIO.read(fileChosen);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                imgChosen.setImage(image);
                imgChosen.setFitHeight(200 * (image.getHeight() / image.getWidth()));
                imgChosen.setFitWidth(200);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (txtAScan != null) txtAScan.setText("Scanning image... Please wait!");
            doScanAndTranslate();
        }
    }
}
