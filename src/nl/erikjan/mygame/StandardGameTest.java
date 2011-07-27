package nl.erikjan.mygame;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.game.StandardGame;
import com.jmex.game.state.BasicGameState;
import com.jmex.game.state.GameStateManager;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 *
 * @author edewit
 */
public class StandardGameTest {

   public static void main(String[] args) {
      StandardGame game = new StandardGame("TestGame");
      game.start();	// Start the game thread

      BasicGameState gameState = new BasicGameState("state");
//      DebugGameState gameState = new DebugGameState();
      GameStateManager.getInstance().attachChild(gameState);
      gameState.setActive(true);	// Activate it

      TextureState ts = game.getDisplay().getRenderer().createTextureState();
      ts.setEnabled(true);
      ts.setTexture(TextureManager.loadTexture(StandardGameTest.class.getClassLoader().getResource("fokker.jpg"),
              Texture.MinificationFilter.Trilinear,
              Texture.MagnificationFilter.Bilinear));

      Airplane plane = buildPlayer(ts);
      gameState.getRootNode().attachChild(plane);
      gameState.getRootNode().updateGeometricState(0, true);
      gameState.getRootNode().updateRenderState();

      final Camera cam = game.getCamera();
      cam.setLocation(cam.getLocation().add(new Vector3f(0, 250, -100)));
      cam.lookAt(plane.getWorldTranslation(), new Vector3f(0, 1, 0));
      cam.update();

      final Renderer renderer = game.getDisplay().getRenderer();

      GameTaskQueueManager.getManager().update(new Callable<Object>() {

         public Object call() throws Exception {
                Skybox skybox = buildSkyBox(renderer);
                skybox.getLocalTranslation().set(cam.getLocation());
                skybox.updateGeometricState(0.0f, true);
                return skybox;
         }
      });
//      gameState.getRootNode().attachChild(buildSkyBox(renderer));
   }

   private static Airplane buildPlayer(TextureState ts) {
      URL model = StandardGameTest.class.getClassLoader().getResource("fokker.jme");
      Spatial ship = null;
      try {
         ship = (Spatial) BinaryImporter.getInstance().load(model.openStream());
      } catch (IOException e) {
         e.printStackTrace();
      }
      ship.setLocalScale(.2f);
      ship.setRenderState(ts);
      ship.setModelBound(new BoundingBox());
      ship.updateModelBound();

      Airplane plane = new Airplane("player", ship);

      ship.setLocalTranslation(0, -40, 0);
      plane.setLocalTranslation(0, 40, 0);
      return plane;
      //plane.setRenderQueueMode(Renderer.QUEUE_OPAQUE);

//        input = new AirplaneInputHandler(plane, settings.getRenderer());

//        cam.setLocation(cam.getLocation().add(new Vector3f(0, 250, -100)));
//        cam.lookAt(ship.getWorldTranslation(), new Vector3f(0, 1, 0));
//        cam.update();
   }

   private static Skybox buildSkyBox(Renderer renderer) {
      Skybox skybox = new Skybox("skybox", 10, 10, 10);

      Texture up = TextureManager.loadTexture(StandardGameTest.class.getClassLoader().getResource("jmetest/data/skybox1/6.jpg"),
              Texture.MinificationFilter.BilinearNearestMipMap,
              Texture.MagnificationFilter.Bilinear);
      skybox.setTexture(Skybox.Face.Up, up);
      //skybox.preloadTextures();

      CullState cullState = renderer.createCullState();
      cullState.setCullFace(CullState.Face.None);
      cullState.setEnabled(true);
      skybox.setRenderState(cullState);

      ZBufferState zState = renderer.createZBufferState();
      zState.setEnabled(false);
      skybox.setRenderState(zState);

      skybox.setLightCombineMode(Spatial.LightCombineMode.Off);
      skybox.setCullHint(Spatial.CullHint.Never);
      skybox.setTextureCombineMode(TextureCombineMode.Replace);
      skybox.updateRenderState();

      skybox.lockBounds();
//      skybox.lockMeshes();
      return skybox;
   }
}
