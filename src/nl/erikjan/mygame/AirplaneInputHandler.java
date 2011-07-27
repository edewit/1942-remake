package nl.erikjan.mygame;

import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.action.KeyNodeBackwardAction;
import com.jme.input.action.KeyNodeForwardAction;

/**
 *
 * @author edewit
 */
public class AirplaneInputHandler extends InputHandler{

    //the vehicle we are going to control
    private Airplane airplane;

    @Override
    public void update(float time) {
        if ( !isEnabled() ) return;

        super.update(time);
        airplane.update(time);
    }

    /**
     * Supply the node to control and the api that will handle input creation.
     * @param vehicle the node we wish to move
     * @param api the library that will handle creation of the input.
     */
    public AirplaneInputHandler(Airplane airplane, String api) {
        this.airplane = airplane;
        setKeyBindings(api);
        setActions(airplane);

    }

    /**
     * creates the keyboard object, allowing us to obtain the values of a keyboard as keys are
     * pressed. It then sets the actions to be triggered based on if certain keys are pressed (WSAD).
     * @param api the library that will handle creation of the input.
     */
    private void setKeyBindings(String api) {
        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();

        keyboard.set("forward", KeyInput.KEY_UP);
        keyboard.set("backward", KeyInput.KEY_DOWN);
        keyboard.set("turnRight", KeyInput.KEY_RIGHT);
        keyboard.set("turnLeft", KeyInput.KEY_LEFT);
    }

    /**
     * assigns action classes to triggers. These actions handle moving the node forward, backward and
     * rotating it. It also creates an action for drifting that is not assigned to key trigger, this
     * action will occur each frame.
     * @param node the node to control.
     */
    private void setActions(Airplane node) {
        KeyNodeForwardAction forward = new KeyNodeForwardAction(node, 20f);
        addAction(forward, "forward", true);
        KeyNodeBackwardAction backward = new KeyNodeBackwardAction(node, 15f);
        addAction(backward, "backward", true);
        AirplaneRollAction rotateLeft = new AirplaneRollAction(node, AirplaneRollAction.LEFT);
        addAction(rotateLeft, "turnLeft", true);
        AirplaneRollAction rotateRight = new AirplaneRollAction(node, AirplaneRollAction.RIGHT);
        addAction(rotateRight, "turnRight", true);
    }
}
