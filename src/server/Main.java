package server;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;

/**
 * @author mrowlie
 */
public class Main extends SimpleApplication {

    public static Node refRootNode;
    public static BulletAppState bulletAppState;
    private static Main app;

    static Networking net;
    
    
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
        net = new Networking();
        refRootNode = rootNode;
        bulletAppState = new BulletAppState();
        net = new Networking();
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
