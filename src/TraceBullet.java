import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.intersection.PickData;
import com.jme.intersection.TrianglePickResults;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.SwitchNode;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.effects.particles.ParticleController;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleMesh;

/**
 * @author mazander
 */
public class TraceBullet extends SimpleGame {

	private static float BARREL_LENGTH = 3f;

	private final TrianglePickResults pickResults = new TrianglePickResults();

	private final Quaternion quat = new Quaternion();

	private final Vector3f vec = new Vector3f();

	private final Ray ray = new Ray();

	private float gunAngle = 0;

	private Node gun;

	private Node scene;

	private SwitchNode tracerEffect;

	private ParticleMesh hitParticles;

	public static void main(String[] args) {
		TraceBullet app = new TraceBullet();
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();
	}

	protected void simpleUpdate() {
		// rotate gun
		vec.set(0, 0, 1);
		gunAngle += timer.getTimePerFrame();
		quat.fromAngleNormalAxis(gunAngle, vec);
		gun.setLocalRotation(quat);

		// calculate hit ray origin and direction
		vec.set(gun.getWorldTranslation());
		Vector3f directionOfGunBarrel = gun.getWorldRotation().getRotationColumn(0);
		vec.scaleAdd(BARREL_LENGTH * 0.1f, directionOfGunBarrel, vec);
		ray.setOrigin(vec);
		ray.setDirection(directionOfGunBarrel);

		// get distance from barrel to hit point
		scene.findPick(ray, pickResults);
		float distance = 1000f;
		if (pickResults.getNumber() > 0) {
			PickData data = pickResults.getPickData(0);
			distance = data.getDistance();
			pickResults.clear();
		}

		// update hit particles
		if (distance < 1000f) {
			vec.set(distance, 0, 0);
			hitParticles.getParticleController().setRepeatType(ParticleController.RT_WRAP);
			hitParticles.getParticleController().setActive(true);
			hitParticles.setOriginOffset(vec);
		} else {
			hitParticles.getParticleController().setRepeatType(ParticleController.RT_CLAMP);
		}

		// update tracers
		vec.set(1f, 1f, distance);
		tracerEffect.setLocalScale(vec);
		tracerEffect.setActiveChild((tracerEffect.getActiveChild() + 1) % 16);
	}

	protected void simpleInitGame() {
		display.setTitle("Tracer Bullets Test");

		// init effects
		tracerEffect = createTracerBullets(16, 0.1f);
		tracerEffect.setLocalTranslation(BARREL_LENGTH, 0, 0);
		hitParticles = createHitParks(1.5f, new ColorRGBA(1.0f, 0.312f, 0.121f, 1.0f));

		// create gun and attach effects to it
		Cylinder barrel = new Cylinder("GunBarrel", 4, 12, 0.2f, BARREL_LENGTH, false);
		barrel.setLocalRotation(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
		barrel.setLocalTranslation(BARREL_LENGTH / 2, 0, 0);
		gun = new Node("GunNode");
		gun.attachChild(new Cylinder("GunBody", 4, 12, 1f, 1f, true));
		gun.attachChild(barrel);
		gun.attachChild(tracerEffect);
		gun.attachChild(hitParticles);
		gun.setModelBound(new BoundingBox());
		gun.updateModelBound();
		rootNode.attachChild(gun);

		// create random boxes in a circle form
		scene = new Node();
		for (float a = 0; a < FastMath.TWO_PI; a += 0.3f) {
			float r = FastMath.rand.nextFloat() * 10f + 10f;
			Box box = new Box("RandomBox", new Vector3f(), 1f, 1f, 1f);
			box.setLocalTranslation(r * FastMath.cos(a), r * FastMath.sin(a), 0);
			box.setModelBound(new BoundingBox());
			box.updateModelBound();
			scene.attachChild(box);
		}
		rootNode.attachChild(scene);

		// init input and picking
		input = new FirstPersonHandler(cam, 10f, 1f);
		pickResults.setCheckDistance(true);
	}

	/**
	 * SwitchNode containing random cylinders on positive unit x-axis.
	 */
	private static SwitchNode createTracerBullets(int count, float maxThickness) {
		SwitchNode node = new SwitchNode("TracerBullets");

		for (int i = 0; i < count; i++) {
			float height = 0.3f + 0.7f * FastMath.nextRandomFloat();
			float offset = (1.0f - height) * FastMath.nextRandomFloat();
			float thickness = 0.5f + 0.5f * FastMath.nextRandomFloat();
			thickness *= maxThickness;
			Cylinder cyl = new Cylinder("Cylinder", 2, 5, thickness, height, false);
			cyl.setLocalTranslation(0, 0, 0.5f + offset);
			cyl.setCastsShadows(false);
			node.attachChild(cyl);
		}

		Renderer renderer = DisplaySystem.getDisplaySystem().getRenderer();
		CullState cs = renderer.createCullState();
		cs.setCullFace(CullState.Face.Back);

		BlendState as = renderer.createBlendState();
		as.setBlendEnabled(true);
		as.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		as.setDestinationFunction(BlendState.DestinationFunction.One);

		ColorRGBA color = new ColorRGBA(1f, 0.15f, 0.15f, 0.2f);
		MaterialState mat = renderer.createMaterialState();
		mat.setAmbient(color);
		mat.setEmissive(color);
		mat.setDiffuse(color);

		node.setLightCombineMode(Spatial.LightCombineMode.Replace);
		node.setTextureCombineMode(Spatial.TextureCombineMode.Off);
		node.setRenderState(mat);
		node.setRenderState(as);
		node.setRenderState(cs);
		node.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		node.setLocalRotation(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
		node.updateRenderState();
		return node;
	}

	private static ParticleMesh createHitParks(float size, ColorRGBA color) {
		ParticleMesh particles = ParticleFactory.buildParticles("HitSparks", 40);
		Renderer renderer = DisplaySystem.getDisplaySystem().getRenderer();

		BlendState as = renderer.createBlendState();
		as.setBlendEnabled(true);
		as.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		as.setDestinationFunction(BlendState.DestinationFunction.One);

		ZBufferState zs = renderer.createZBufferState();
		zs.setWritable(false);
		zs.setEnabled(true);

		TextureState ts = renderer.createTextureState();
		ts.setTexture(TextureManager.loadTexture(TraceBullet.class.getClassLoader().getResource(
				"jmetest/data/texture/spark.jpg"), Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear));

		particles.setRenderState(zs);
		particles.setRenderState(as);
		particles.setRenderState(ts);
		particles.setEmissionDirection(new Vector3f(0.0f, 0.0f, 1.0f));
		particles.setMaximumAngle(FastMath.PI);
		particles.setMinimumAngle(0);
		particles.setMinimumLifeTime(50.0f);
		particles.setMaximumLifeTime(200.0f);
		particles.setStartSize(size);
		particles.setEndSize(0.1f);
		particles.setStartColor(new ColorRGBA(color.r, color.g, color.b, 1f));
		particles.setEndColor(new ColorRGBA(color.r, color.g, color.b, 0f));
		particles.setReleaseRate(30);
		particles.setReleaseVariance(0.0f);
		particles.setInitialVelocity(0.04f);
		particles.getParticleGeometry().setCastsShadows(false);
		ParticleController particleController = particles.getParticleController();
		particleController.setActive(false);
		particles.warmUp(40);
		return particles;
	}
}