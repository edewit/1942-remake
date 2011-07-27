package nl.erikjan.mygame;

import com.jme.bounding.BoundingBox;
import com.jme.curve.CatmullRomCurve;
import com.jme.curve.CurveController;
import com.jme.image.Texture;
import com.jme.intersection.BoundingCollisionResults;
import com.jme.intersection.CollisionResults;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.effects.particles.ParticleMesh;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.ObjToJme;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 *
 * @author edewit
 */
public class Enemy extends Airplane implements UpdatableSpatial {

    private CollisionResults results = new EnemyCollisionResult();
    private Vector3f endPoint;

    public Enemy(String id, Renderer renderer) {
        super(id, null);

        URL model = getClass().getClassLoader().getResource("fokker.jme");
        Spatial ship = null;
        try {
            ship = (Spatial) BinaryImporter.getInstance().load(model.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ship.setLocalScale(.2f);
        TextureState ts = renderer.createTextureState();
        ts.setEnabled(true);
        ts.setTexture(TextureManager.loadTexture(getClass().getClassLoader().getResource("fokker.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear));
        ship.setRenderState(ts);
        ship.setNormalsMode(NormalsMode.AlwaysNormalize);
        ship.setModelBound(new BoundingBox());
        ship.updateModelBound();

//        ship.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.PI, Vector3f.UNIT_Z));
        setModel(ship);

        Vector3f[] points = new Vector3f[6];
        points[0] = new Vector3f(110, 0, 110);
        points[1] = new Vector3f(110, 0, 50);
        points[2] = new Vector3f(-20, 0, 10);
        points[3] = new Vector3f(20, 0, -20);
        points[4] = new Vector3f(-90, 0, -90);
        points[5] = new Vector3f(0, 0, 110);
        endPoint = points[5];

        CatmullRomCurve curve = new CatmullRomCurve("Curve", points);
        curve.setSteps(512);
//        ship.setLocalTranslation(points[0]);
//        curve.setCullHint(CullHint.Always);

        CurveController curveController = new CurveController(curve, ship);
        ship.addController(curveController);
        curveController.setRepeatType(Controller.RT_CLAMP);
        curveController.setSpeed(0.05f);
        curveController.setAutoRotation(true);
        curveController.setUpVector(new Vector3f(0, 0.5f, 0));

        attachChild(curve);

        ExplosionFactory.warmup();
    }

    @Override
    public void update(float time) {
        results.clear();
        calculateCollisions(getParent(), results);
    }

    public boolean isAlive() {
        return !endPoint.equals(getModel().getLocalTranslation());
    }

    private class EnemyCollisionResult extends BoundingCollisionResults {

        @Override
        public void processCollisions() {
            for (int i = 0; i < getNumber(); i++) {
                Spatial target = getCollisionData(i).getTargetMesh();
                if (target instanceof Shot) {
                    ParticleMesh explosion = ExplosionFactory.getSmallExplosion();
                    explosion.setOriginOffset(getModel().getLocalTranslation());
                    explosion.forceRespawn();
                    detachChild(getModel());
                    setModel(explosion);
                }
            }
        }
    }
}
