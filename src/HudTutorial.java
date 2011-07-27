
import com.jme.input.KeyInput;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.FloatBuffer;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;

import com.jme.image.Texture;
import com.jme.input.KeyInput;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Quad;

import com.jme.scene.state.BlendState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

public class HudTutorial extends SimpleGame {

   private static final float MAXIMUM = 100f;

   private Quaternion rotQuat = new Quaternion();

   private Vector3f axis = new Vector3f(1, 1, 0);

   private Cylinder cylinder;

   private float angle = 0;

   private Node hudNode;

   private Quad gauge;

   private int textureWidth;

   private int textureHeight;

   private void setGauge(int value) {
      value %= (int) MAXIMUM;
      FloatBuffer texCoords = BufferUtils.createVector2Buffer(4);
      float relCoord = 0.5f - ((float) value / MAXIMUM) * 0.5f;
      texCoords.put(relCoord).put(getVForPixel(56));
      texCoords.put(relCoord).put(getVForPixel(63));
      texCoords.put(relCoord + 0.5f).put(getVForPixel(63));
      texCoords.put(relCoord + 0.5f).put(getVForPixel(56));
      gauge.setTextureCoords(new TexCoords(texCoords, 0));
   }

   public static void main(String[] args) {
      HudTutorial app = new HudTutorial();
      app.setConfigShowMode(SimpleGame.ConfigShowMode.AlwaysShow);
      app.start();
   }

   protected void simpleInitGame() {
      display.setTitle("HUD Tutorial 3");


      /* create a rotating cylinder so we have something in the background */
      cylinder = new Cylinder("Cylinder", 6, 18, 5, 10);
      cylinder.setModelBound(new BoundingBox());
      cylinder.updateModelBound();

      MaterialState ms = display.getRenderer().createMaterialState();
      ms.setAmbient(new ColorRGBA(1f, 0f, 0f, 1f));
      ms.setDiffuse(new ColorRGBA(1f, 0f, 0f, 1f));

      ms.setEnabled(true);
      cylinder.setRenderState(ms);
      cylinder.updateRenderState();

      rootNode.attachChild(cylinder);

      hudNode = new Node();
      hudNode.setCullHint(Spatial.CullHint.Never);
      hudNode.setLightCombineMode(Spatial.LightCombineMode.Off);
      Quad progressBar = new Quad("ProgressBar", 10.0f, 15.0f);
      progressBar.setRenderQueueMode(Renderer.QUEUE_ORTHO);
      progressBar.setColorBuffer(null);
      progressBar.setDefaultColor(new ColorRGBA(1f, 0f, 0f, 1f));
      progressBar.setRenderState(ms);
      progressBar.updateRenderState();
      hudNode.attachChild(progressBar);

      rootNode.attachChild(hudNode);

      hudNode = new Node("hudNode");

      Quad hudQuad = new Quad("hud", 36f, 12f);
      gauge = new Quad("gauge", 32f, 8f);

      hudNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);

      hudNode.setLocalTranslation(new Vector3f(display.getWidth() / 2, display.getHeight() / 2, 0));

      TextureState ts = display.getRenderer().createTextureState();
      final URL resource = getClass().getResource("/hudtutorial3.png");
      ts.setTexture(TextureManager.loadTexture(resource, Texture.MinificationFilter.BilinearNearestMipMap, Texture.MagnificationFilter.Bilinear, 1.0f, true));

//        try {
//			ts.setTexture(TextureManager.loadTexture(new URL("http://www.jmonkeyengine.com/wiki/lib/exe/fetch.php/hudtutorial3.png"), Texture.MinificationFilter.BilinearNearestMipMap,
//			        Texture.MagnificationFilter.Bilinear, 0.0f, true));
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}

      textureWidth = ts.getTexture().getImage().getWidth();
      textureHeight = ts.getTexture().getImage().getHeight();
      ts.setEnabled(true);

      FloatBuffer texCoords = BufferUtils.createVector2Buffer(4);
      texCoords.put(getUForPixel(0)).put(getVForPixel(0));
      texCoords.put(getUForPixel(0)).put(getVForPixel(10));
      texCoords.put(getUForPixel(34)).put(getVForPixel(10));
      texCoords.put(getUForPixel(34)).put(getVForPixel(0));
      hudQuad.setTextureCoords(new TexCoords(texCoords, 0));

      BlendState as = display.getRenderer().createBlendState();
      as.setBlendEnabled(true);

      as.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
      as.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
      as.setTestEnabled(false);
      as.setEnabled(true);

      hudNode.setLightCombineMode(Spatial.LightCombineMode.Off);
      hudNode.updateRenderState();

      hudNode.attachChild(hudQuad);
      hudNode.attachChild(gauge);

      hudNode.setRenderState(ts);
      hudNode.setRenderState(as);
      hudNode.updateRenderState();
      setGauge(0);
      rootNode.attachChild(hudNode);
   }

   protected void simpleUpdate() {
      /* recalculate rotation for the cylinder */
      if (timer.getTimePerFrame() < 1) {
         angle = angle + timer.getTimePerFrame();
      }
      //setGauge((int)cam.getLocation().length());

      KeyInput key = KeyInput.get();

      if (key.isKeyDown(KeyInput.KEY_0)) {
         rotQuat.fromAngleAxis(angle, axis);
         cylinder.setLocalRotation(rotQuat);

      }

   }

   private float getUForPixel(int xPixel) {
      return (float) xPixel / textureWidth;
   }

   private float getVForPixel(int yPixel) {
      return 1f - (float) yPixel / textureHeight;
   }
}

