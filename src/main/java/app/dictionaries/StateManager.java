package app.dictionaries;

import app.dictionaries.states.BaseState;
import app.dictionaries.states.LoginState;
import app.dictionaries.utilities.DictionaryManagement;
import app.dictionaries.utilities.elements.Effects;
import javafx.stage.Stage;

import java.util.Stack;

/**
 * Class to control states in application
 */
public class StateManager {
    private static StateManager instance = new StateManager();
    private static Stack<BaseState> states;

    public Stage primaryStage = null;

    /**
     * Get instance singleton
     *
     * @return instace
     */
    public static StateManager getInstance() {
        return instance;
    }

    /**
     * Default constructor
     */
    private StateManager() {
        states = new Stack<>();
        states.push(LoginState.getInstance());
        states.lastElement().enter();
    }

    /**
     * Change state function
     *
     * @param nextState next function to change
     */
    public void changeState(BaseState nextState, DictionaryManagement.Runner runner) {
        states.lastElement().getStateScene().getRoot().setDisable(true);

        Effects.makeFadeTransition(states.lastElement().getStateScene().getRoot(), 350, 1.0, 0.0, e -> {
            if (nextState == null) {
                states.pop();
                if (states.size() > 0) {
                    states.lastElement().enter();
                }
            } else {
                states.push(nextState);
                states.lastElement().enter();
            }

            states.lastElement().getStateScene().getRoot().setDisable(false);

            if (runner != null) runner.run();
            else {
                this.primaryStage.setScene(states.lastElement().getStateScene());
                Effects.makeFadeTransition(states.lastElement().getStateScene().getRoot(), 350, 0.0, 1.0, null);
            }
        });
    }

    /**
     * Reload current state
     */
    public void reloadState() {
        BaseState current = getCurrentState();
        changeState(null, () -> changeState(current, null));
    }

    /**
     * Get current state
     *
     * @return current state
     */
    public BaseState getCurrentState() {
        return states.lastElement();
    }
}
