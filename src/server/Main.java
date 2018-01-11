package server;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;

/**
 * @author mrowlie
 */
public class Main extends SimpleApplication {

    private static Main app;

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
