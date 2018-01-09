package Client;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;


/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    //Constans
    public static final String NAME = "MultiDisk";
    public static final String DEFAULT_SERVER = "mrowlie.asuscomm.com";
    public static final int PORT = 2000;
    public static final int VERSION = 1;
    //
    public static Node refRootNode;
    public static BulletAppState bulletAppState;
    
    RigidBodyControl landscape;
    Spatial sceneModel;
    
    Client myClient;
    Modeling myModel = new Modeling();
    ChaseCamera chaseCam;
    Player player;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        refRootNode = rootNode;
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        Box b = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
        
        initiateMap();
        initiatePlayer();
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, -9.81f, 0));
    }

    @Override
    public void simpleUpdate(float tpf) 
    {
        player.update();
        myModel.update(tpf);
        System.out.println(player.getLocalTranslation());
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    private void initiatePlayer()
    {
        flyCam.setEnabled(true);
        player = new Player(new Vector3f(0f,20f,0f), cam);
        myModel.addEntity(player);
        chaseCam = new ChaseCamera(cam, player, inputManager);
        chaseCam.setMaxDistance(1f);
        chaseCam.setMinDistance(1f);
        chaseCam.setDragToRotate(false);
    }
    private void initiateClient()
    {
        try
        {
            NetRead netRead =  new NetRead();
                myClient = Network.connectToServer(NAME, VERSION, DEFAULT_SERVER, PORT, PORT);
                myClient.addMessageListener(netRead);
                myClient.start(); 
        }
        catch(Exception e)
        {
            System.out.println("ERROR CONNECTING");
            System.out.println(e.getMessage());
        }
    }
   
    private void initiateMap()
    {
        //Loading the map and adding collision to it.
        sceneModel = assetManager.loadModel("Scenes/MainScene.j3o");
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(sceneModel);
        landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);
        bulletAppState.getPhysicsSpace().add(landscape);
        rootNode.attachChild(sceneModel);
    }
    
    private void initiateControlls()
    {
        inputManager.addMapping("W", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("S", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("A", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("D", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addListener(actionListener, "W", "A", "S", "D");
    }
    private final ActionListener actionListener = new ActionListener()
    {
        @Override
        public void onAction(String name, boolean isPressed, float tpf)
        {
            player.input(name, isPressed);
        }        
    };
    
    
}
