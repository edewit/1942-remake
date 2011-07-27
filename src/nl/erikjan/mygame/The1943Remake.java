package nl.erikjan.mygame;

import ch.erikjan.remake1945.Sky;
import com.jme.app.AbstractGame.ConfigShowMode;
import com.jme.app.SimplePassGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.DirectionalLight;
import com.jme.math.Plane;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.pass.RenderPass;
import com.jme.renderer.pass.ShadowedRenderPass;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.effects.water.WaterRenderPass;
import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.List;

/**
 * @author Erik Jan de Wit
 */
public class The1943Remake extends SimplePassGame {

    private static final float SHOT_INTERVAL = 0.3f;
    private Airplane plane;
    private WaterRenderPass waterEffectRenderPass;
    private Sky skybox;
    private Quad waterQuad;
    private float farPlane = 1000.0f;
    private float textureScale = 0.02f;
    private float lastShot;

    @Override
    protected void cleanup() {
        super.cleanup();
        waterEffectRenderPass.cleanup();
    }

    public static void main(String[] args) {
        The1943Remake app = new The1943Remake();
        app.setConfigShowMode(ConfigShowMode.ShowIfNoConfig);
        app.start();
    }

    public The1943Remake() {
        stencilBits = 1;
    }

    protected void simpleInitGame() {
        KeyBindingManager.getKeyBindingManager().set("fire", KeyInput.KEY_LMENU);
        KeyBindingManager.getKeyBindingManager().set("loop", KeyInput.KEY_SPACE);

        buildPlayer();

        rootNode.attachChild(new Enemy("test", display.getRenderer()));

        Node reflectedNode = new Node("reflectNode");

//        buildSkyBox();
        skybox = new Sky(display.getRenderer());
        reflectedNode.attachChild(skybox);

        rootNode.attachChild(reflectedNode);

        waterEffectRenderPass = new WaterRenderPass(cam, 4, false, true);
        // setting to default value just to show
        waterEffectRenderPass.setWaterPlane(new Plane(new Vector3f(0.0f, 1.0f, 0.0f), 0.0f));

        waterQuad = new Quad("waterQuad", 1, 1);
        waterQuad.setLocalTranslation(0, -20, 0);
        FloatBuffer normBuf = waterQuad.getNormalBuffer();
        normBuf.clear();
        normBuf.put(0).put(1).put(0);
        normBuf.put(0).put(1).put(0);
        normBuf.put(0).put(1).put(0);
        normBuf.put(0).put(1).put(0);

        waterEffectRenderPass.setWaterEffectOnSpatial(waterQuad);
        rootNode.attachChild(waterQuad);

        waterEffectRenderPass.setReflectedScene(reflectedNode);
        waterEffectRenderPass.setSkybox(skybox);
        pManager.add(waterEffectRenderPass);

        ShadowedRenderPass sPass = new ShadowedRenderPass();
        sPass.add(rootNode);
        sPass.addOccluder(plane);
        //sPass.addOccluder(enemy);
        sPass.setRenderShadows(true);
        sPass.setLightingMethod(ShadowedRenderPass.LightingMethod.Modulative);
        sPass.setShadowColor(ColorRGBA.gray);
        pManager.add(sPass);

        RenderPass rPass = new RenderPass();
        rPass.add(statNode);
        pManager.add(rPass);

        CullState cullState = display.getRenderer().createCullState();
        cullState.setCullFace(CullState.Face.Back);
        cullState.setEnabled(true);

        rootNode.setRenderState(cullState);
//        rootNode.setCullHint(Spatial.CullHint.Never);
        rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);

        buildLights();
    }

    private void buildLights() {
        DirectionalLight dr = new DirectionalLight();
        dr.setEnabled(true);
        dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dr.setAmbient(new ColorRGBA(.2f, .2f, .2f, .3f));
        dr.setDirection(new Vector3f(0.3f, -0.4f, -0.1f).normalizeLocal());
        dr.setShadowCaster(true);

        CullState cs = display.getRenderer().createCullState();
        cs.setCullFace(CullState.Face.Back);
        cs.setEnabled(true);
        rootNode.setRenderState(cs);

        lightState.attach(dr);
    }

    @Override
    protected void simpleUpdate() {
        float interpolation = timer.getTimePerFrame();
        input.update(interpolation);

        updateAnimated(rootNode.getChildren(), interpolation);

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("fire")
                && !plane.isDoLoop() && (timer.getTimeInSeconds() - SHOT_INTERVAL > lastShot)) {
            Shot s = new Shot(plane.getModel().getWorldTranslation());
            lastShot = timer.getTimeInSeconds();
            s.updateRenderState();
            rootNode.attachChild(s);
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("loop")) {
            plane.setDoLoop(true);
        }

        skybox.getLocalTranslation().set(cam.getLocation());
        skybox.updateGeometricState(0.0f, true);

        //water effect
        Vector3f transVec = new Vector3f(cam.getLocation().x,
                waterEffectRenderPass.getWaterHeight(), cam.getLocation().z);

        setTextureCoords(0, transVec.x, -transVec.z, textureScale);
        setVertexCoords(transVec.x, transVec.y, transVec.z);
    }

    private void buildPlayer() {
        URL model = getClass().getClassLoader().getResource("fokker.jme");
        Spatial ship = null;
        try {
            ship = (Spatial) BinaryImporter.getInstance().load(model.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        ship.setLocalScale(.2f);
        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(TextureManager.loadTexture(getClass().getClassLoader().getResource("fokker.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear));
        ship.setRenderState(ts);
        ship.setModelBound(new BoundingBox());
        ship.updateModelBound();

        plane = new Airplane("player", ship);

        ship.setLocalTranslation(0, -40, 0);
        plane.setLocalTranslation(0, 40, 0);
        rootNode.attachChild(plane);
        rootNode.updateGeometricState(0, true);
        //plane.setRenderQueueMode(Renderer.QUEUE_OPAQUE);

        input = new AirplaneInputHandler(plane, settings.getRenderer());

        cam.setLocation(cam.getLocation().set(new Vector3f(0, 200, -100)));
        cam.lookAt(ship.getWorldTranslation(), new Vector3f(0, 1, 0));
        cam.update();

    }

    private void setVertexCoords(float x, float y, float z) {
        FloatBuffer vertBuf = waterQuad.getVertexBuffer();
        vertBuf.clear();

        vertBuf.put(x - farPlane).put(y).put(z - farPlane);
        vertBuf.put(x - farPlane).put(y).put(z + farPlane);
        vertBuf.put(x + farPlane).put(y).put(z + farPlane);
        vertBuf.put(x + farPlane).put(y).put(z - farPlane);
    }

    private void setTextureCoords(int buffer, float x, float y,
            float textureScale) {
        x *= textureScale * 0.5f;
        y *= textureScale * 0.5f;
        textureScale = farPlane * textureScale;
        FloatBuffer texBuf;
        texBuf = waterQuad.getTextureCoords(buffer).coords;
        texBuf.clear();
        texBuf.put(x).put(textureScale + y);
        texBuf.put(x).put(y);
        texBuf.put(textureScale + x).put(y);
        texBuf.put(textureScale + x).put(textureScale + y);
    }

    private void updateAnimated(List<Spatial> children, float interpolation) {
        if (children != null) {
            for (Iterator<Spatial> it = children.iterator(); it.hasNext();) {
                Spatial spatial = it.next();
                if (spatial instanceof UpdatableSpatial) {
                    UpdatableSpatial s = (UpdatableSpatial) spatial;
                    if (!s.isAlive()) {
                        it.remove();
                    } else {
                        ((UpdatableSpatial) spatial).update(interpolation);
                    }
                }

                if (spatial instanceof Node) {
                    updateAnimated(((Node) spatial).getChildren(), interpolation);
                }
            }
        }
    }
}