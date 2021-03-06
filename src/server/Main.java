package server;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.jme3.terrain.geomipmap.TerrainQuad;
import packets.Packet.*;

/**
 * @author mrowlie
 */
public class Main extends SimpleApplication {

    public static Node refRootNode;
    public static BulletAppState bulletAppState;
    private static Main app;
    
    RigidBodyControl landscape;
    Spatial sceneModel;
    public static TerrainQuad terrain;
    
    private Modeling myModel;
    static Networking net;
    
    public static Node sunAndMoon;
    private Node sun;
    private Node moon;
    
    
    public static void main(String[] args){
        Main application = new Main();
        AppSettings newSettings = new AppSettings(true);
        newSettings.setFrameRate(100);
        application.setSettings(newSettings);
        Main.app = application;
        application.start(JmeContext.Type.Headless);
    }

    @Override
    public void simpleInitApp() {
        Serializer.registerClass(Authenticate.class);
        Serializer.registerClass(AuthPlayer.class);
        Serializer.registerClass(KeyPressed.class);
        Serializer.registerClass(PlayerOrientation.class);
        Serializer.registerClass(UpdateEntity.class);
        Serializer.registerClass(SpawnEntity.class);
        Serializer.registerClass(SpawnSnowpile.class);
        Serializer.registerClass(DespawnSnowpile.class);
        Serializer.registerClass(ThrowSnowball.class);
        Serializer.registerClass(Reload.class);
        Serializer.registerClass(DestroyEntity.class);
        Serializer.registerClass(UpdateGUI.class);
        Serializer.registerClass(Death.class);
        Serializer.registerClass(InitiateSolarSystem.class);
        
        net = new Networking();
        refRootNode = rootNode;
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        myModel = new Modeling();
        bulletAppState.getPhysicsSpace().addCollisionListener(myModel);
        initiateMap();
        
        sun = new Node("Sun");
        sun.setLocalTranslation(Vector3f.UNIT_Y.mult(500f));
        
        moon = new Node("Moon");
        moon.setLocalTranslation(Vector3f.UNIT_Y.negate().mult(500f));
        
        sunAndMoon = new Node("SunAndMoon");
        sunAndMoon.attachChild(sun);
        sunAndMoon.attachChild(moon);
    }

    private void initiateMap()
    {
        //Loading the map and adding collision to it.
        sceneModel = assetManager.loadModel("Scenes/MainScene.j3o");
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(sceneModel);
        landscape = new RigidBodyControl(sceneShape, 0);
        Node terrainNode = (Node)sceneModel;
        terrain = (TerrainQuad)terrainNode.getChild("terrain-MainScene");
        
        sceneModel.addControl(landscape);
        bulletAppState.getPhysicsSpace().add(landscape);
        rootNode.attachChild(sceneModel);
    }
    
    @Override
    public void simpleUpdate(float tpf) 
    {
        myModel.update(tpf);
        sunAndMoon.rotate(0f, 0f, FastMath.PI*tpf/90);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
