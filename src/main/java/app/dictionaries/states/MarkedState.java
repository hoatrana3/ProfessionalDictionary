package app.dictionaries.states;

import app.AppMain;
import app.dictionaries.DictionaryApplication;
import app.dictionaries.StateManager;
import app.dictionaries.states.general.AddDialog;
import app.dictionaries.states.general.WordDialog;
import app.dictionaries.utilities.DictionaryManagement;
import app.dictionaries.utilities.elements.Effects;
import app.dictionaries.utilities.elements.Word;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class MarkedState extends BaseState {
    private static MarkedState instance = new MarkedState();

    @FXML
    private VBox vbxContain;

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXTreeTableView<Word> ttvList;

    @FXML
    private AnchorPane apMarked;

    @FXML
    private JFXTextField txtFSearch;

    /**
     * Default constructor
     */
    public MarkedState() {
        super();
    }

    /**
     * Get instance singleton
     *
     * @return instance
     */
    public static MarkedState getInstance() {
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
        //////// Setup for table tree view ////////

        ttvList.setPlaceholder(new Label("Nothing marked..."));

        //// Create columns ////
        JFXTreeTableColumn<Word, String> targetCol = new JFXTreeTableColumn<>("Word");
        targetCol.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getValue().getTarget()));
        targetCol.setPrefWidth(300);

        JFXTreeTableColumn<Word, String> pronCol = new JFXTreeTableColumn<>("Pronunciation");
        pronCol.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getValue().getPronunciation()));
        pronCol.setPrefWidth(300);

        // Make list
        ObservableList<Word> wordsMarked = FXCollections.observableArrayList();
        wordsMarked.addAll(DictionaryApplication.markedWords);

        // Make root
        final TreeItem<Word> rootTree = new RecursiveTreeItem<Word>(wordsMarked, RecursiveTreeObject::getChildren);

        // Event item selected
        ttvList.setOnMouseClicked(event -> {
            Word word = null;

            try {
                word = ttvList.getSelectionModel().getSelectedItem().getValue();
            } catch (NullPointerException ne) {
                System.out.println(ne);
            }

            if ((word != null) && (event.getClickCount() == 2)) {
                WordDialog.callWordDialog(apMarked, word, true);
                apMarked.requestFocus();
            }
        });

        // Add all into table
        ttvList.getColumns().setAll(targetCol, pronCol);
        ttvList.setRoot(rootTree);
        ttvList.setShowRoot(false);

        ///////// Key event for all ///////////
        apMarked.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case ESCAPE:
                    if (WordDialog.isOnWordDialog) {
                        if (WordDialog.isOnWordDialog) WordDialog.closeWordDialog(apMarked.getChildren(), apMarked.getChildren().get(apMarked.getChildren().size() - 1), txtFSearch);
                    } else {
                        if (txtFSearch.getText().isEmpty()) {
                            try {
                                StateManager.getInstance().changeState(null, null);
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        } else txtFSearch.setText("");
                    }
                    break;
            }
        });

        txtFSearch.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case DOWN:
                    if (ttvList.getCurrentItemsCount() != 0) {
                        if (txtFSearch.getText().isEmpty()) ttvList.getSelectionModel().select(0);
                        else
                            ttvList.getSelectionModel().select(ttvList.getSelectionModel().getSelectedIndex() + 1);

                        Word word = null;
                        try {
                            word = ttvList.getSelectionModel().getSelectedItem().getValue();
                        } catch (NullPointerException ne) {
                            ne.printStackTrace();
                        }

                        if (word != null) {
                            txtFSearch.setText(word.getTarget());
                        }
                        if (ttvList.getSelectionModel().getSelectedIndex() >= 6)
                            ttvList.scrollTo(ttvList.getSelectionModel().getSelectedIndex() - 5);
                    }
                    break;
                case UP:
                    ttvList.getSelectionModel().select(ttvList.getSelectionModel().getSelectedIndex() - 1);

                    Word word = null;
                    try {
                        word = ttvList.getSelectionModel().getSelectedItem().getValue();
                    } catch (NullPointerException ne) {
                        ne.printStackTrace();
                    }

                    if (word != null) {
                        txtFSearch.setText(word.getTarget());
                    }
                    if (ttvList.getSelectionModel().getSelectedIndex() >= 6)
                        ttvList.scrollTo(ttvList.getSelectionModel().getSelectedIndex() - 5);
                    else ttvList.scrollTo(0);
                    break;
                case ENTER:
                    word = null;

                    try {
                        word = ttvList.getTreeItem(ttvList.getSelectionModel().getSelectedIndex()).getValue();
                    } catch (NullPointerException ne) {
                        System.out.println(ne);
                    }

                    if (word != null) WordDialog.callWordDialog(apMarked, word, true);
                    break;
                case ESCAPE:
                    apMarked.requestFocus();
                    break;
                default:
                    txtFSearch.end();
                    ttvList.setPredicate(wordTreeItem -> (wordTreeItem.getValue().getTarget().toLowerCase().contains(txtFSearch.getText().trim().toLowerCase())));

                    break;
            }
        });


        ttvList.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case UP:
                    if (ttvList.getSelectionModel().getFocusedIndex() == 0) {
                        txtFSearch.requestFocus();
                    } else {
                        Word word = null;

                        try {
                            int index = ttvList.getSelectionModel().getSelectedIndex();
                            word = ttvList.getTreeItem(index - 1).getValue();
                        } catch (NullPointerException ne) {
                            System.out.println(ne);
                        }

                        if (word != null) txtFSearch.setText(word.getTarget());
                    }
                    break;
                case DOWN:
                    if (ttvList.getSelectionModel().getFocusedIndex() == ttvList.getCurrentItemsCount() - 1) {
                        btnBack.requestFocus();
                    } else {
                        Word word = null;

                        try {
                            int index = ttvList.getSelectionModel().getSelectedIndex();
                            word = ttvList.getTreeItem(index + 1).getValue();
                        } catch (NullPointerException ne) {
                            System.out.println(ne);
                        }

                        if (word != null) txtFSearch.setText(word.getTarget());
                    }
                    break;
                case ENTER:
                    Word word = null;

                    try {
                        word = ttvList.getSelectionModel().getSelectedItem().getValue();
                    } catch (NullPointerException ne) {
                        System.out.println(ne);
                    }

                    if (word != null) WordDialog.callWordDialog(apMarked, word, true);

                    apMarked.requestFocus();
                    break;
                case ESCAPE:
                    apMarked.requestFocus();
                    break;
            }
        });

        ////////// Add rippler //////////
        apMarked.getChildren().add(Effects.makeRippler(vbxContain, Paint.valueOf("#9A9EC2"), JFXRippler.RipplerMask.RECT));
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
            root = FXMLLoader.load(new URL("file:" + AppMain.pathToDirectory + ("resources/UI/MarkedState/MarkedUI.fxml").replaceAll("/", File.separator)));
            if (root.getStylesheets().isEmpty())
                root.getStylesheets().add("file:" + AppMain.pathToDirectory + ("resources/UI/MarkedState/MarkedStyle.css").replaceAll("/", File.separator));
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
    }
}
