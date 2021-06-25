package app.dictionaries.utilities.elements;

import com.jfoenix.controls.JFXRippler;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

public class Effects {

    /**
     * Make fade transition to node
     *
     * @param node              node to fade
     * @param durationMillis    duration as milliseconds
     * @param valueBegin        value of opacity to begin
     * @param valueEnd          destination value of opacity
     * @param eventEventHandler function to run when finished
     * @return FadeTransition
     */
    public static FadeTransition makeFadeTransition(Node node, double durationMillis, double valueBegin, double valueEnd, EventHandler<ActionEvent> eventEventHandler) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(durationMillis), node);
        fadeTransition.setFromValue(valueBegin);
        fadeTransition.setToValue(valueEnd);
        fadeTransition.setOnFinished(eventEventHandler);
        fadeTransition.play();

        return fadeTransition;
    }

    /**
     * Make scale transition to node
     *
     * @param node              node to scale
     * @param durationMillis    duration as milliseconds
     * @param startX            start scale X
     * @param startY            start scale Y
     * @param endX              end scale X
     * @param endY              end scale Y
     * @param eventEventHandler function to run when finished
     * @return ScaleTransition
     */
    public static ScaleTransition makeScaleTransition(Node node, double durationMillis, double startX, double startY, double endX, double endY, EventHandler<ActionEvent> eventEventHandler) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(durationMillis), node);
        scaleTransition.setFromX(startX);
        scaleTransition.setFromY(startY);
        scaleTransition.setToX(endX);
        scaleTransition.setToY(endY);
        scaleTransition.setOnFinished(eventEventHandler);
        scaleTransition.play();

        return scaleTransition;
    }

    /**
     * Make rippler to node
     *
     * @param node  node to set rippler
     * @param color color of rippler
     * @param type  type of rippler
     * @return JFXRippler
     */
    public static JFXRippler makeRippler(Node node, Paint color, JFXRippler.RipplerMask type) {
        JFXRippler rippler = new JFXRippler(node);
        rippler.setMaskType(type);
        rippler.setRipplerFill(color);

        return rippler;
    }

    /**
     * Make gaussian blur effect to node
     *
     * @param node   node to set blur
     * @param radius radius of blur
     * @return GaussianBlur
     */
    public static GaussianBlur makeGuassianBlur(Node node, double radius) {
        GaussianBlur gaussianBlur = new GaussianBlur(radius);
        node.setEffect(gaussianBlur);

        return gaussianBlur;
    }
}
