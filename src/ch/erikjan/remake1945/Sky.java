package ch.erikjan.remake1945;


import com.jme.image.Texture;
import com.jme.renderer.Renderer;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.state.CullState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;

/**
 *
 * @author edewit
 */
public class Sky extends Skybox {

   public Sky(Renderer renderer) {
        super("skybox", 10, 10, 10);

        Texture up = TextureManager.loadTexture(getClass().getClassLoader().getResource("6.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear);
        setTexture(Skybox.Face.Up, up);
        preloadTextures();

        CullState cullState = renderer.createCullState();
        cullState.setCullFace(CullState.Face.None);
        cullState.setEnabled(true);
        setRenderState(cullState);

        ZBufferState zState = renderer.createZBufferState();
        zState.setEnabled(false);
        setRenderState(zState);

        setLightCombineMode(Spatial.LightCombineMode.Off);
        setCullHint(Spatial.CullHint.Never);
        setTextureCombineMode(TextureCombineMode.Replace);
        updateRenderState();

        lockBounds();
        lockMeshes();
   }


}
