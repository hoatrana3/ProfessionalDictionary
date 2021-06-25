package app.dictionaries.states;

import app.AppMain;
import app.dictionaries.DictionaryApplication;
import app.dictionaries.StateManager;
import app.dictionaries.states.general.AddDialog;
import app.dictionaries.states.general.WordDialog;
import app.dictionaries.utilities.DictionaryManagement;
import app.dictionaries.utilities.elements.Algorithms;
import app.dictionaries.utilities.elements.Effects;
import app.dictionaries.utilities.elements.Word;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
//import sun.jvm.hotspot.memory.DictionaryEntry;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Class for main state
 */
public class MainState extends BaseState {
    private static MainState instance = new MainState();

    @FXML
    private JFXDrawer drwMenu;

    @FXML
    private JFXButton btnSearch;

    @FXML
    private JFXTextField txtFSearch;

    @FXML
    private JFXListView<Word> lvwWords;

    @FXML
    private HBox hbxHints;

    @FXML
    private JFXButton btnTranslate;

    @FXML
    private JFXButton btnMarked;

    @FXML
    private VBox vbxMenu;

    @FXML
    private HBox hbxContain;

    @FXML
    private JFXButton btnLogin;

    @FXML
    private JFXButton btnContact;

    @FXML
    private Label lblAddWord;

    @FXML
    private JFXButton btnImg;

    @FXML
    private AnchorPane apMain;

    @FXML
    private JFXCheckBox cbxShowAll;

    @FXML
    private JFXHamburger hbgMenu;

    @FXML
    private VBox vbxSearchContain;

    @FXML
    private JFXButton btnHistory;

    private static ArrayList<Label> hintsLabel = new ArrayList<>();

    /**
     * Default constructor
     */
    public MainState() {
        super();
    }

    /**
     * Get instance singleton
     *
     * @return instance
     */
    public static MainState getInstance() {
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
        ////////// Text field, search bar, check box and list view event /////////

        // Listview event
        if (lvwWords.getItems().isEmpty()) {
            lvwWords.setVisible(false);
            lvwWords.setDisable(true);
        } else {
            lvwWords.setVisible(true);
            lvwWords.setDisable(false);
        }

        // List view words on items clicked
        lvwWords.setOnMouseClicked(event -> {
            Word word = null;

            try {
                word = lvwWords.getSelectionModel().getSelectedItem();
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            }

            if (word != null) {
                txtFSearch.setText(word.getTarget());
                txtFSearch.resetValidation();
                endHints(hbxHints, lvwWords, lblAddWord);


                if (event.getClickCount() == 2) WordDialog.callWordDialog(apMain, word, true);
            }
        });

        // Checkbox show all event
        cbxShowAll.selectedProperty().addListener(select -> {
            if (cbxShowAll.isSelected()) {
                Effects.makeFadeTransition(lblAddWord, 500, lblAddWord.getOpacity(), 0.0, ev -> {
                    lblAddWord.setDisable(true);
                    lblAddWord.setVisible(false);
                });

                lvwWords.setVisible(true);
                Effects.makeFadeTransition(lvwWords, 500, lvwWords.getOpacity(), 1.0, e -> {
                    lvwWords.setDisable(false);
                });

                txtFSearch.resetValidation();

                if (lvwWords.getItems().isEmpty()) {
                    lvwWords.setItems(FXCollections.observableArrayList(DictionaryApplication.getInstance().dictionary.getWordsContainer()));
                }
            } else if (txtFSearch.getText().isEmpty() || Algorithms.lookUpInList(new ArrayList<>(DictionaryApplication.getInstance().dictionary.getWordsContainer()), txtFSearch.getText()) < 0) {
                lvwWords.setDisable(true);
                Effects.makeFadeTransition(lvwWords, 500, lvwWords.getOpacity(), 0.0, e -> {
                    lvwWords.setVisible(false);
                });
            }
        });

        // Setup validator for search bar
        ValidatorBase validatorEmpty = new RequiredFieldValidator();
        validatorEmpty.setMessage("No word given!");

        ValidatorBase validatorNotExist = new ValidatorBase() {
            @Override
            protected void eval() {
                int posElement = Algorithms.lookUpInList(new ArrayList<>(DictionaryApplication.getInstance().dictionary.getWordsContainer()), txtFSearch.getText());

                if (posElement < 0) {
                    this.hasErrors.set(true);
                } else this.hasErrors.set(false);
            }
        };

        validatorNotExist.setMessage("This word does not exist in dictionary!");

        txtFSearch.getValidators().addAll(validatorEmpty, validatorNotExist);

        // Set filteredlist for dictionary's container
        FilteredList<Word> filter = new FilteredList<>(FXCollections.observableArrayList(DictionaryApplication.getInstance().dictionary.getWordsContainer()), null);

        // Hints word settings

        if (hbxHints != null) {
            hbxHints.setVisible(false);
            hbxHints.setDisable(true);
        }

        // Focus changed event
        txtFSearch.focusedProperty().addListener(e -> {
            if (txtFSearch.isFocused()) {
                txtFSearch.resetValidation();
                endHints(hbxHints, lvwWords, lblAddWord);
            } else {
                txtFSearch.validate();
                if (hbxHints != null)
                    hintsLabel = makeAllHintLabels(DictionaryManagement.doFuzzySearch(Algorithms.standardlize(txtFSearch.getText()), 0.5, DictionaryApplication.getInstance().dictionary), txtFSearch, apMain);

                try {
                    if (txtFSearch.getActiveValidator().getMessage().equalsIgnoreCase("This word does not exist in dictionary!"))
                        callHints(hbxHints, lvwWords, lblAddWord);
                } catch (NullPointerException npe) {
                    System.out.println("Vadilation is null : " + npe);
                }
            }
        });

        ////////// Hamburger slide and drawer //////////
        drwMenu.setSidePane(vbxMenu);

        HamburgerSlideCloseTransition hamburgerTransition = new HamburgerSlideCloseTransition(hbgMenu);
        hamburgerTransition.setRate(-1);

        hbgMenu.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            hamburgerTransition.setRate(hamburgerTransition.getRate() * -1);
            hamburgerTransition.play();

            if (drwMenu.isClosed() || drwMenu.isClosing()) {
                drwMenu.open();
            } else drwMenu.close();

            if (hamburgerTransition.getRate() == -1) {
                Effects.makeFadeTransition(vbxSearchContain, 350, vbxSearchContain.getOpacity(), 1.0, ev -> {
                    vbxSearchContain.setEffect(null);
                });
            } else {
                Effects.makeFadeTransition(vbxSearchContain, 350, vbxSearchContain.getOpacity(), 0.4, ev -> {
                    Effects.makeGuassianBlur(vbxSearchContain, 4.0);
                });
            }
        });

        ///////// Key event for all ///////////
        apMain.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case ESCAPE:
                    if (drwMenu.isOpening() || drwMenu.isOpened()) {
                        hamburgerTransition.setRate(-1);
                        hamburgerTransition.play();

                        Effects.makeFadeTransition(vbxSearchContain, 350, vbxSearchContain.getOpacity(), 1.0, ev -> {
                            vbxSearchContain.setEffect(null);
                        });

                        drwMenu.close();
                    }

                    if (drwMenu.isClosed()) {
                        if (WordDialog.isOnWordDialog || AddDialog.isOnAddDialog) {
                            if (WordDialog.isOnWordDialog) WordDialog.closeWordDialog(apMain.getChildren(), apMain.getChildren().get(apMain.getChildren().size() - 1), txtFSearch);
                            if (AddDialog.isOnAddDialog) AddDialog.closeAddDialog(apMain.getChildren(), apMain.getChildren().get(apMain.getChildren().size() - 1), txtFSearch);

                            txtFSearch.requestFocus();
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
                    break;
                case RIGHT:
                    if (drwMenu.isClosed() || drwMenu.isClosing()) {
                        hamburgerTransition.setRate(hamburgerTransition.getRate() * -1);
                        hamburgerTransition.play();

                        drwMenu.open();

                        Effects.makeFadeTransition(vbxSearchContain, 350, vbxSearchContain.getOpacity(), 0.4, ev -> {
                            Effects.makeGuassianBlur(vbxSearchContain, 4.0);
                        });
                    }

                    break;
                case ENTER:
                    if (drwMenu.isOpened()) {
                        if (btnTranslate.isFocused())
                            StateManager.getInstance().changeState(TranslateState.getInstance(), null);
                        if (btnMarked.isFocused()) StateManager.getInstance().changeState(MarkedState.getInstance(), null);
                        if (btnImg.isFocused()) StateManager.getInstance().changeState(ImgReaderState.getInstance(), null);
                        if (btnContact.isFocused()) StateManager.getInstance().changeState(ContactState.getInstance(), null);
                    }

                    break;
            }
        });

        txtFSearch.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case DOWN:
                    Word word = null;
                    lvwWords.getSelectionModel().select(lvwWords.getSelectionModel().getSelectedIndex() + 1);

                    try {
                        word = lvwWords.getSelectionModel().getSelectedItem();
                    } catch (NullPointerException ne) {
                        ne.printStackTrace();
                    }

                    if (word != null) {
                        txtFSearch.setText(word.getTarget());
                        txtFSearch.resetValidation();
                        endHints(hbxHints, lvwWords, lblAddWord);
                    }
                    if (lvwWords.getSelectionModel().getSelectedIndex() >= 9)
                        lvwWords.scrollTo(lvwWords.getSelectionModel().getSelectedIndex() - 8);
                    break;
                case UP:
                    lvwWords.getSelectionModel().select(lvwWords.getSelectionModel().getSelectedIndex() - 1);

                    word = null;
                    try {
                        word = lvwWords.getSelectionModel().getSelectedItem();
                    } catch (NullPointerException ne) {
                        ne.printStackTrace();
                    }

                    if (word != null) {
                        txtFSearch.setText(word.getTarget());
                    }
                    if (lvwWords.getSelectionModel().getSelectedIndex() >= 9)
                        lvwWords.scrollTo(lvwWords.getSelectionModel().getSelectedIndex() - 8);
                    else lvwWords.scrollTo(0);
                    break;
                case RIGHT:
                    btnSearch.requestFocus();
                    break;
                case ENTER:
                    apMain.requestFocus();
                    break;
                default:
                    txtFSearch.end();
                    String text = (txtFSearch.getText() + key.getText()).trim();
                    if (!text.isEmpty()) {
                        txtFSearch.resetValidation();
                        filter.setPredicate(s -> (s.getTarget().toUpperCase().startsWith(Algorithms.standardlize(text).toUpperCase())));
                        lvwWords.setItems(filter);

                        Effects.makeFadeTransition(lvwWords, 500, lvwWords.getOpacity(), 1.0, ev -> {
                            lvwWords.setDisable(false);
                            lvwWords.setVisible(true);
                        });
                    } else {
                        filter.setPredicate(null);

                        if (!cbxShowAll.isSelected()) {
                            Effects.makeFadeTransition(lvwWords, 500, lvwWords.getOpacity(), 0.0, ev -> {
                                lvwWords.setDisable(true);
                                lvwWords.setVisible(false);
                            });
                        }
                    }
                    break;
            }
        });

        lvwWords.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case ENTER:
                    mainStateCallWord(apMain, txtFSearch, drwMenu);
                    break;
                case UP:
                    if (lvwWords.getSelectionModel().getSelectedIndex() == 0) {
                        txtFSearch.requestFocus();
                    } else {
                        Word word = null;

                        try {
                            int index = lvwWords.getSelectionModel().getSelectedIndex();
                            word = lvwWords.getItems().get(index - 1);
                        } catch (NullPointerException ne) {
                            System.out.println(ne);
                        }

                        if (word != null) txtFSearch.setText(word.getTarget());
                    }
                    break;
                case DOWN:
                    if (lvwWords.getSelectionModel().getSelectedIndex() == lvwWords.getItems().size() - 1) {
                        btnLogin.requestFocus();
                    } else {
                        Word word = null;

                        try {
                            int index = lvwWords.getSelectionModel().getSelectedIndex();
                            word = lvwWords.getItems().get(index + 1);
                        } catch (NullPointerException ne) {
                            System.out.println(ne);
                        }

                        if (word != null) txtFSearch.setText(word.getTarget());
                    }
                    break;
            }
        });

        btnSearch.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case ENTER:
                    mainStateCallWord(apMain, txtFSearch, drwMenu);
                    break;
                case LEFT:
                    txtFSearch.requestFocus();
                    break;
            }
        });

        btnHistory.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case ENTER:
                    if (!LoginState.isTryUser) {
                        try {
                            StateManager.getInstance().changeState(HistoryState.getInstance(), null);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                    else DictionaryManagement.createAlert(Alert.AlertType.ERROR, "Try user!", "This feature is not for trial version!\nPlease log in and enjoy it!", true);
                    break;
            }
        });

        // Label add event
        lblAddWord.setOnMouseClicked(e -> {
            if (!LoginState.isTryUser) AddDialog.callAddDialog(apMain, txtFSearch.getText(), false);
            else DictionaryManagement.createAlert(Alert.AlertType.ERROR, "Try user!", "This feature is not for trial version!\nPlease log in and enjoy it!", true);
        });

        ////////// Add rippler //////////
        apMain.getChildren().add(Effects.makeRippler(hbxContain, Paint.valueOf("#3976AF"), JFXRippler.RipplerMask.RECT));
    }

    /**
     * Override function getVariables
     */
    public void getVariables(Parent root) {
        apMain = (AnchorPane) root;

        lvwWords = (JFXListView<Word>) root.lookup("#lvwWords");
        hbxHints = (HBox) root.lookup("#hbxHints");
    }

    /**
     * Override function enter
     */
    @Override
    public void enter() {
        Parent root = null;

        try {
            root = FXMLLoader.load(new URL("file:" + AppMain.pathToDirectory + ("resources/UI/MainState/MainUI.fxml").replaceAll("/", File.separator)));
            if (root.getStylesheets().isEmpty())
                root.getStylesheets().add("file:" + AppMain.pathToDirectory + ("resources/UI/MainState/MainStyle.css").replaceAll("/", File.separator));
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
        btnSearch.setOnMouseClicked(e -> {
            Word word = Algorithms.getWordInDictionary(DictionaryApplication.getInstance().dictionary, Algorithms.standardlize(txtFSearch.getText()));
            if (word != null) {
                WordDialog.callWordDialog(apMain, word, true);
            }
        });

        btnLogin.setOnMouseClicked(e -> {
            try {
                StateManager.getInstance().changeState(null, null);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        btnHistory.setOnMouseClicked(e -> {
            if (!LoginState.isTryUser) {
                try {
                    StateManager.getInstance().changeState(HistoryState.getInstance(), null);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            else DictionaryManagement.createAlert(Alert.AlertType.ERROR, "Try user!", "This feature is not for trial version!\nPlease log in and enjoy it!", true);
        });

        btnMarked.setOnMouseClicked(e -> {
            StateManager.getInstance().changeState(MarkedState.getInstance(), null);
        });

        btnTranslate.setOnMouseClicked(e -> {
            StateManager.getInstance().changeState(TranslateState.getInstance(), null);
        });

        btnImg.setOnMouseClicked(e -> {
            StateManager.getInstance().changeState(ImgReaderState.getInstance(), null);
        });

        btnContact.setOnMouseClicked(e -> {
            StateManager.getInstance().changeState(ContactState.getInstance(), null);
        });
    }

    /**
     * Get list of 5 labels suited
     *
     * @param hints       list of hints availble
     * @param searchField textfield search
     * @return 5 labels suited
     */
    private ArrayList<Label> makeAllHintLabels(ArrayList<String> hints, JFXTextField searchField, AnchorPane root) {
        HBox container = (HBox) root.lookup("#hbxHints");
        ArrayList<Label> result = new ArrayList<>();

        if (container != null) container.getChildren().remove(1, container.getChildren().size());

        for (int i = 0; i < ((hints.size() > 5) ? 5 : hints.size()); ++i) {
            Label newLabel = new Label(hints.get(i));
            newLabel.setOnMouseClicked(e -> WordDialog.callWordDialog(root, Algorithms.getWordInDictionary(DictionaryApplication.getInstance().dictionary, newLabel.getText()), true));
            result.add(newLabel);
        }

        return result;
    }

    /**
     * Call hints list
     *
     * @param hbxHints hbox container
     */
    private void callHints(HBox hbxHints, JFXListView<Word> lvwWords, Label lblAddWord) {
        if (hbxHints != null && !hbxHints.isVisible()) {
            lblAddWord.setVisible(true);
            Effects.makeFadeTransition(lblAddWord, 500, lblAddWord.getOpacity(), (!LoginState.isTryUser) ? 1.0 : 0.5, ev -> {
                lblAddWord.setDisable(false);
            });

            if (!hintsLabel.isEmpty() && (hbxHints.getChildren().size() == 1)) {
                hbxHints.getChildren().addAll(hintsLabel);
                hbxHints.setVisible(true);
                Effects.makeFadeTransition(hbxHints, 500, hbxHints.getOpacity(), 1.0, ev -> {
                    hbxHints.setDisable(false);
                });

                Effects.makeFadeTransition(lvwWords, 500, lvwWords.getOpacity(), 0.0, ev -> {
                    lvwWords.setDisable(true);
                    lvwWords.setVisible(false);
                });
            }
        }
    }

    /**
     * End hints list
     *
     * @param hbxHints hbox container
     */
    private void endHints(HBox hbxHints, JFXListView<Word> lvwWords, Label lblAddWord) {
        if (hbxHints != null) {
            Effects.makeFadeTransition(hbxHints, 500, hbxHints.getOpacity(), 0.0, ev -> {
                hbxHints.setVisible(false);
                hbxHints.setDisable(true);
            });

            Effects.makeFadeTransition(lvwWords, 500, lvwWords.getOpacity(), 1.0, ev -> {
                lvwWords.setDisable(false);
                lvwWords.setVisible(true);
            });

            Effects.makeFadeTransition(lblAddWord, 500, lblAddWord.getOpacity(), 0.0, ev -> {
                lblAddWord.setDisable(true);
                lblAddWord.setVisible(false);
            });
        }
    }

    /**
     * Main state call dialog
     *
     * @param apMain     root
     * @param txtFSearch search bar
     * @param drwMenu    drawer
     */
    private static void mainStateCallWord(AnchorPane apMain, JFXTextField txtFSearch, JFXDrawer drwMenu) {
        if (drwMenu.isClosed()) {
            Word word = Algorithms.getWordInDictionary(DictionaryApplication.getInstance().dictionary, Algorithms.standardlize(txtFSearch.getText()));
            if (word != null) {
                WordDialog.callWordDialog(apMain, word, true);
            }
        }
    }
}
