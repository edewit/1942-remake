package nl.erikjan.mygame;

import com.jme.bounding.BoundingBox;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Quad;
import com.jme.util.geom.BufferUtils;

/**
 *
 * @author edewit
 */
public class Shot extends Quad implements UpdatableSpatial {
    private static final int TIME_TO_LIVE = 4;
    private float timeAlive;

    public Shot(Vector3f location) {
        super("shot1", 1, 3);
        getLocalTranslation().set(location);
        getLocalRotation().set(new Quaternion(new float[]{180, 0, 0}));
        ColorRGBA[] colors = new ColorRGBA[4];
        colors[0] = ColorRGBA.orange;
        colors[1] = ColorRGBA.orange;
        colors[2] = ColorRGBA.yellow;
        colors[3] = ColorRGBA.yellow;
        setColorBuffer(BufferUtils.createFloatBuffer(colors));
        setModelBound(new BoundingBox());
        updateModelBound();
    }


    public void update(float time) {
        getLocalTranslation().addLocal(new Vector3f(0, 0, 80 * time));
        timeAlive += time;
    }

    public boolean isAlive() {
        return timeAlive < TIME_TO_LIVE;
    }
}
