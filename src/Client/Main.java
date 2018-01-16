package Client;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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
    
    RigidBodyControl landscape;
    Spatial sceneModel;
    
    LoginScreen loginScreen;
    static Nifty nifty;
    Boolean isLoggedIn;
    
    Client myClient;
    Modeling myModel;
    ChaseCamera chaseCam;
    
    
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
        
        RemotePlayer.playerModel = assetManager.loadModel("/Models/Ninja.mesh.xml");
        RemotePlayer.playerModel.scale(0.025f);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Models/Ninja.jpg"));
        RemotePlayer.playerModel.setMaterial(mat);
        
        this.isLoggedIn = false;
        initiateClient();
        refRootNode = rootNode;
        refInputManager = inputManager;
        refFlyCam = flyCam;
        refCam = cam;
        
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
        
        SnowPile pile = new SnowPile(assetManager, rootNode, 20f, 20f);
        
        initiateMap();
        initiateControlls();
        initiatePlayer();
    }

    @Override
    public void simpleUpdate(float tpf) 
    {
        //if(this.isLoggedIn){
            myModel.update(tpf);
            
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
    }
    
    private void initiateControlls()
    {
        inputManager.addMapping("W", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("S", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("A", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("D", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "W", "A", "S", "D", "Jump");
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
                System.out.println("Not null");
            }
        }        
    };
    
    
}
