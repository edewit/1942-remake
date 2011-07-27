package nl.erikjan.mygame;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.NodeHandler;
import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.CameraNode;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainPage;
import com.jmex.terrain.util.FaultFractalHeightMap;
import com.jmex.terrain.util.ProceduralTextureGenerator;
import javax.swing.ImageIcon;

/**
 *
 * @author edewit
 */
public class LevelTest extends SimpleGame {

    private CameraNode camNode;
    private TerrainPage page;

    public static void main(String[] args) {
        LevelTest app = new LevelTest();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();

    }

    @Override
    protected void simpleInitGame() {
        rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);

        DirectionalLight dl = new DirectionalLight();
        dl.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dl.setDirection(new Vector3f(1, -0.5f, 1));
        dl.setEnabled(true);
        lightState.attach(dl);

        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        cam.update();

        camNode = new CameraNode("Camera Node", cam);
        camNode.setLocalTranslation(new Vector3f(0, 250, -20));
        camNode.updateWorldData(0);
        input = new NodeHandler(camNode, 150, 1);
        rootNode.attachChild(camNode);
        display.setTitle("Terrain Test");
        display.getRenderer().setBackgroundColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 1));

        DirectionalLight dr = new DirectionalLight();
        dr.setEnabled(true);
        dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dr.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        dr.setDirection(new Vector3f(0.5f, -0.5f, 0).normalizeLocal());

        CullState cs = display.getRenderer().createCullState();
        cs.setCullFace(CullState.Face.Back);
        cs.setEnabled(true);
        rootNode.setRenderState(cs);

        lightState.attach(dr);

        FaultFractalHeightMap heightMap = new FaultFractalHeightMap(257, 32, 0, 255,
                0.75f);
        Vector3f terrainScale = new Vector3f(10, 1, 10);
        heightMap.setHeightScale(0.001f);
        page = new TerrainPage("Terrain", 33, heightMap.getSize(), terrainScale,
                heightMap.getHeightMap());

        page.setDetailTexture(1, 16);
        rootNode.attachChild(page);

        ProceduralTextureGenerator pt = new ProceduralTextureGenerator(heightMap);
        pt.addTexture(new ImageIcon(getClass().getClassLoader().getResource(
                "grassb.png")), -128, 0, 128);
        pt.addTexture(new ImageIcon(getClass().getClassLoader().getResource(
                "dirt.jpg")), 0, 128, 255);
        pt.addTexture(new ImageIcon(getClass().getClassLoader().getResource(
                "highest.jpg")), 128, 255, 384);

        pt.createTexture(512);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        Texture t1 = TextureManager.loadTexture(
                pt.getImageIcon().getImage(),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear,
                true);
        ts.setTexture(t1, 0);

        Texture t2 = TextureManager.loadTexture(getClass().getClassLoader().
                getResource(
                "jmetest/data/texture/Detail.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        ts.setTexture(t2, 1);
        t2.setWrap(Texture.WrapMode.Repeat);

        t1.setApply(Texture.ApplyMode.Combine);
        t1.setCombineFuncRGB(Texture.CombinerFunctionRGB.Modulate);
        t1.setCombineSrc0RGB(Texture.CombinerSource.CurrentTexture);
        t1.setCombineOp0RGB(Texture.CombinerOperandRGB.SourceColor);
        t1.setCombineSrc1RGB(Texture.CombinerSource.PrimaryColor);
        t1.setCombineOp1RGB(Texture.CombinerOperandRGB.SourceColor);

        t2.setApply(Texture.ApplyMode.Combine);
        t2.setCombineFuncRGB(Texture.CombinerFunctionRGB.AddSigned);
        t2.setCombineSrc0RGB(Texture.CombinerSource.CurrentTexture);
        t2.setCombineOp0RGB(Texture.CombinerOperandRGB.SourceColor);
        t2.setCombineSrc1RGB(Texture.CombinerSource.Previous);
        t2.setCombineOp1RGB(Texture.CombinerOperandRGB.SourceColor);
        rootNode.setRenderState(ts);

//        FogState fs = display.getRenderer().createFogState();
//        fs.setDensity(0.5f);
//        fs.setEnabled(true);
//        fs.setColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f));
//        fs.setEnd(1000);
//        fs.setStart(500);
//        fs.setDensityFunction(FogState.DensityFunction.Linear);
//        fs.setQuality(FogState.Quality.PerVertex);
//        rootNode.setRenderState(fs);
    }
}
