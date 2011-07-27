package nl.erikjan.mygame;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;

/**
 *
 * @author edewit
 */
public class ForwardAndBackwardAction extends KeyInputAction {

    public static final int FORWARD = 0;
    public static final int BACKWARD = 1;
    //the node to manipulate
    private Airplane node;
    private int direction;

    /**
     * The vehicle to accelerate is supplied during construction.
     * @param node the vehicle to speed up.
     * @param direction Constant either FORWARD or BACKWARD
     */
    public ForwardAndBackwardAction(Airplane node, int direction) {
        this.node = node;
        this.direction = direction;
    }

    /**
     * the action calls the vehicle's accelerate or brake command which adjusts its velocity.
     */
    public void performAction(InputActionEvent evt) {
        if (direction == FORWARD) {
            node.setVelocity(20);
        } else if (direction == BACKWARD) {
            node.setVelocity(-20);
        }
    }
}