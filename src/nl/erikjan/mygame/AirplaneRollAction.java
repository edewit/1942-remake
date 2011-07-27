package nl.erikjan.mygame;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;

/**
 *
 * @author edewit
 */
public class AirplaneRollAction extends KeyInputAction {
    public static final int RIGHT = 0;
    public static final int LEFT = 1;

    //the node to manipulate
    private Airplane airplane;
    private int direction;
    private int modifier = 1;

    /**
     * create a new action with the vehicle to turn.
     * @param vehicle the vehicle to turn
     */
    public AirplaneRollAction(Airplane airplane, int direction) {
        this.airplane = airplane;
        this.direction = direction;
    }

    /**
     * turn the vehicle by its turning speed. If the vehicle is traveling
     * backwards, swap direction.
     */
    public void performAction(InputActionEvent evt) {
        //affect the direction
        if(direction == LEFT) {
            modifier = 1;
        } else if(direction == RIGHT) {
            modifier = -1;
        }
        
        airplane.setRotateOn(modifier);
    }
}