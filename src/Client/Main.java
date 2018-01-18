package Client;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.textfield.builder.TextFieldBuilder;
import packets.Packet.*;
import packets.Packet.KeyPressed;


/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    //Constans
    public static final String NAME = "UCS";
    public static final String DEFAULT_SERVER = "mrowlie.asuscomm.com";
    public static final int PORT = 2000;
    public static final int VERSION = 1;
    //
    public static Player localPlayer;
    //
    public static Node refRootNode;
    public static InputManager refInputManager;
    public static FlyByCamera refFlyCam;
    public static BulletAppState bulletAppState;
    public static Camera refCam;
    public static AssetManager refAssetManager;
    public static Node refGuiNode;
    public static Main refMain;
    
    RigidBodyControl landscape;
    Spatial sceneModel;
    
    LoginScreen loginScreen;
    static Nifty nifty;
    Boolean isLoggedIn;
    
    Client myClient;
    Modeling myModel;
    ChaseCamera chaseCam;
    
    PointLight testLight;
    PointLight testLight2;
    public static Node sunAndMoonNode;
    Node sun;
    Node moon;
    //public NiftyJmeDisplay niftyDisplay;
    //public Nifty nifty;
    
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        Serializer.registerClass(Authenticate.class);
        Serializer.registerClass(AuthPlayer.class);
        Serializer.registerClass(PlayerOrientation.class);
        Serializer.registerClass(UpdateEntity.class);
        Serializer.registerClass(KeyPressed.class);
        Serializer.registerClass(SpawnEntity.class);
        Serializer.registerClass(SpawnSnowpile.class);
        Serializer.registerClass(DespawnSnowpile.class);
        Serializer.registerClass(ThrowSnowball.class);
        Serializer.registerClass(Reload.class);
        Serializer.registerClass(DestroyEntity.class);
        Serializer.registerClass(UpdateGUI.class);
        Serializer.registerClass(Death.class);
        
        
        RemotePlayer.playerModel = assetManager.loadModel("/Models/Ninja.mesh.xml");
        RemotePlayer.playerModel.scale(0.05f);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", assetManager.loadTexture("Models/Ninja.jpg"));
        RemotePlayer.playerModel.setMaterial(mat);
        
        Sphere snowball = new Sphere(32, 32, 0.25f);
        Geometry geomSnowball = new Geometry("Snowball", snowball);
        Material matsSnowball = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matsSnowball.setColor("Color", ColorRGBA.White);
        geomSnowball.setMaterial(matsSnowball);
        Snowball.snowballModel = geomSnowball;
        
        this.isLoggedIn = false;
        initiateClient();
        refRootNode = rootNode;
        refInputManager = inputManager;
        refFlyCam = flyCam;
        refCam = cam;
        refAssetManager = assetManager;
        refGuiNode = guiNode;
        refMain = this;
        
        bulletAppState = new BulletAppState();
        myModel = new Modeling();
        stateManager.attach(bulletAppState);
        this.flyCam.setMoveSpeed(333);
        

        loginScreen = new LoginScreen();
        
        //flyCam.setDragToRotate(true);
        /*//GUI Stuffs
        LoginScreen loginScreen = new LoginScreen();
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(this.assetManager, this.inputManager, this.audioRenderer, this.viewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("GUI/chat.xml", "chat");
        guiViewPort.addProcessor(niftyDisplay);
        nifty.gotoScreen("screen0");*/
        
        NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
            assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        flyCam.setDragToRotate(true);

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");

        // <screen>
        nifty.addScreen("Screen_ID", new ScreenBuilder("Hello Nifty Screen"){{
            controller(loginScreen); // Screen properties

            // <layer>
            layer(new LayerBuilder("Layer_ID") {{
                childLayoutVertical(); // layer properties, add more...
                valignBottom();
                // <panel>
                panel(new PanelBuilder("Panel_ID") {{
                   childLayoutVertical(); // panel properties, add more...
                   alignRight();
                    // GUI elements
                    control(new TextFieldBuilder("Username", ""){{
                        id("username");
                        alignRight();
                        height("15px");
                        width("100px");
                        focusable(true);
                        valignBottom();
                    }});
                    control(new TextFieldBuilder("Password", ""){{
                        id("password");
                        passwordChar("*".charAt(0));
                        alignRight();
                        height("15px");
                        width("100px");
                        focusable(true);
                        
                        valignBottom();
                    }});
                    control(new ButtonBuilder("login", "Login"){{
                        
                        alignRight();
                        height("15px");
                        width("100px");
                        
                        interactOnClick("authenticate()");
                        
                        valignBottom();
                    }});

                    //.. add more GUI elements here

                }});
                // </panel>
              }});
            // </layer>
          }}.build(nifty));
        // </screen>

        nifty.gotoScreen("Screen_ID"); // start the screen
        
        //Do shiet
        Box b = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mats = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mats.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mats);
        
        initiateMap();
        initiateControlls();
        initiatePlayer();
    }

    @Override
    public void simpleUpdate(float tpf) 
    {
        //if(this.isLoggedIn){
            myModel.update(tpf);
            
            sunAndMoonNode.rotate(0f, 0f, tpf*FastMath.PI/90f);
            
            testLight.setPosition(sun.getWorldTranslation());
            testLight2.setPosition(moon.getWorldTranslation());
        //}
        //System.out.println(player.getLocalTranslation());
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    private void initiatePlayer()
    {
        
    }
    private void initiateClient()
    {
        try
        {
            NetRead netRead =  new NetRead();
            myClient = Network.connectToServer(NAME, VERSION, DEFAULT_SERVER, PORT, PORT);
            myClient.addMessageListener(netRead);
            myClient.start();
            Thread netWriteThread = new Thread(new NetWrite(myClient));
            netWriteThread.start();
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
        
        sunAndMoonNode = new Node("SunAndMoon");
        
        Sphere sphere = new Sphere(32, 32, 20f);
        Geometry geomSphere = new Geometry("Snowball", sphere);
        Material matsSphere = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        //matsSnowball.setColor("Color", ColorRGBA.White);
        geomSphere.setMaterial(matsSphere);
        
        testLight = new PointLight();
        testLight.setColor(ColorRGBA.White);
        testLight.setRadius(700f);
        testLight.setPosition(Vector3f.UNIT_Y.mult(500f));
        sun = new Node("Sun");
        sun.setLocalTranslation(Vector3f.UNIT_Y.mult(500f));
        
        testLight2 = new PointLight();
        testLight2.setColor(ColorRGBA.Blue.mult(0.5f));
        testLight2.setRadius(700f);
        testLight2.setPosition(Vector3f.UNIT_Y.negate().mult(500f));
        moon = new Node("Moon");
        moon.setLocalTranslation(Vector3f.UNIT_Y.negate().mult(500f));
        
        rootNode.addLight(testLight);
        rootNode.addLight(testLight2);
        
        Geometry testGeom = geomSphere.clone();
        testGeom.setLocalTranslation(Vector3f.UNIT_Y.mult(40f));
        
        Geometry testGeom2 = geomSphere.clone();
        testGeom2.setLocalTranslation(Vector3f.UNIT_Y.negate().mult(40f));
        
        AmbientLight sunAmbient = new AmbientLight();
        sunAmbient.setColor(ColorRGBA.Yellow.mult(0.3f));
        sun.addLight(sunAmbient);
        sun.attachChild(testGeom);
        
        AmbientLight moonAmbient = new AmbientLight();
        moonAmbient.setColor(ColorRGBA.Blue.mult(0.3f));
        moon.addLight(moonAmbient);
        moon.attachChild(testGeom2);
        
        sunAndMoonNode.attachChild(sun);
        sunAndMoonNode.attachChild(moon);
        rootNode.attachChild(sunAndMoonNode);
        
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.1f));
        rootNode.addLight(al);
    }
    
    private void initiateControlls()
    {
        inputManager.addMapping("W", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("S", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("A", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("D", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Throw", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reload", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addListener(actionListener, "W", "A", "S", "D", "Jump", "Throw", "Reload");
    }
    private final ActionListener actionListener = new ActionListener()
    {
        @Override
        public void onAction(String name, boolean isPressed, float tpf)
        {
            System.out.println("Input");
            if(localPlayer != null)
            {
                localPlayer.input(name, isPressed);
                //myClient.send(new KeyPressed(name, isPressed, localPlayer.entityId));
                System.out.println("Input\nName : "+name+" isPressed : "+isPressed);
            }
        }        
    };
    
    
}
