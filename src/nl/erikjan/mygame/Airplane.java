package nl.erikjan.mygame;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;

/**
 * Airplane will be a node that handles the movement of a vehicle in the
 * game. It has parameters that define its acceleration and speed as well
 * as braking. The turn speed defines what kind of handling it has, and the
 * weight will define things such as friction for drifting, how fast it falls
 * etc.
 */
public class Airplane extends Node {
    public static final Vector3f LOOP_AXIS = new Vector3f(-1, 0, 0);
    private static final float LEAN_BUFFER = 0.05f;

    private Spatial model;
    private float velocity;

    private int lean;
    private float leanAngle;
    private Quaternion leanQuaternion = new Quaternion();
    private Quaternion loopQuaternion = new Quaternion();
    private boolean doLoop;
    private float loopAngle;

    /**
     * Basic constructor takes the model that represents the graphical
     * aspects of this Airplane.
     * @param id the id of the airplane
     * @param model the model representing the graphical aspects.
     */
    public Airplane(String id, Spatial model) {
        super(id);
        setModel(model);
    }

    /**
     * update applies the translation to the vehicle based on the time passed.
     * @param time the time between frames
     */
    public void update(float time) {
        localTranslation.addLocal(new Vector3f(lean * time * 20, 0, 0));
        processLean(time);

        if (doLoop) {
            if (time < 1) {
                loopAngle = loopAngle + (time * 1.5f);
                if (loopAngle > FastMath.TWO_PI) {
                    loopAngle = 0;
                    doLoop = false;
                }
            }

            loopQuaternion.fromAngleAxis(loopAngle, LOOP_AXIS);
            setLocalRotation(loopQuaternion);
        }
    }

    /**
     * Convience method that determines if the vehicle is moving or not. This is
     * given if the velocity is approximately zero, taking float point rounding
     * errors into account.
     * @return true if the vehicle is moving, false otherwise.
     */
    public boolean vehicleIsMoving() {
        return velocity > FastMath.FLT_EPSILON || velocity < -FastMath.FLT_EPSILON;
    }

    /**
     * processlean will adjust the angle of the bike model based on
     * a lean factor. We angle the bike rather than the Vehicle, as the
     * Vehicle is worried about position about the terrain.
     * @param time the time between frames
     */
    private void processLean(float time) {
        //check if we are leaning at all
        if (lean != 0) {
            if (lean == -1 && leanAngle < 0) {
                leanAngle += -lean * 2 * time;
            } else if (lean == 1 && leanAngle > 0) {
                leanAngle += -lean * 2 * time;
            } else {
                leanAngle += -lean * time;
            }
            //max lean is 1 and -1
            if (leanAngle > 1) {
                leanAngle = 1;
            } else if (leanAngle < -1) {
                leanAngle = -1;
            }
        } else { //we are not leaning, so right ourself back up.
            if (leanAngle < LEAN_BUFFER && leanAngle > -LEAN_BUFFER) {
                leanAngle = 0;
            } else if (leanAngle < -FastMath.FLT_EPSILON) {
                leanAngle += time;
            } else if (leanAngle > FastMath.FLT_EPSILON) {
                leanAngle -= time;
            } else {
                leanAngle = 0;
            }
        }

        leanQuaternion.fromAngleAxis(leanAngle, Vector3f.UNIT_Z);
        model.setLocalRotation(leanQuaternion);

        lean = 0;
    }

    /**
     * retrieves the model Spatial of this vehicle.
     * @return the model Spatial of this vehicle.
     */
    public Spatial getModel() {
        return model;
    }

    /**
     * sets the model spatial of this vehicle. It first
     * detaches any previously attached models.
     * @param model the model to attach to this vehicle.
     */
    public void setModel(Spatial model) {
        detachChild(model);
        this.model = model;
        attachChild(model);
    }

    /**
     * retrieves the velocity of this vehicle.
     * @return the velocity of this vehicle.
     */
    public float getVelocity() {
        return velocity;
    }

    /**
     * set the velocity of this vehicle
     * @param velocity the velocity of this vehicle
     */
    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public void setRotateOn(int modifier) {
        lean = modifier;
    }

    /**
     * @return the doLoop
     */
    public boolean isDoLoop() {
        return doLoop;
    }

    /**
     * @param doLoop the doLoop to set
     */
    public void setDoLoop(boolean doLoop) {
        this.doLoop = doLoop;
    }
}
