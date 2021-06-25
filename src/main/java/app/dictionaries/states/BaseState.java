package app.dictionaries.states;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public abstract class BaseState implements Initializable {
    protected Scene stateScene;

    /**
     * Default constructor
     */
    public BaseState() {
        stateScene = null;
    }

    /**
     * get variables function
     */
    public abstract void getVariables(Parent root);

    /**
     * Get state scene
     *
     * @return scene of current state
     */
    public Scene getStateScene() {
        return stateScene;
    }

    /**
     * Enter function
     */
    public abstract void enter();

    /**
     * Control function - events handler of state
     *
     * @param event event to handle
     */
    @FXML
    public abstract void control(ActionEvent event);
}
