/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import java.util.ArrayList;

/**
 *
 * @author mrowlie
 */
public class Snowpile {
    
    public static ArrayList<Snowpile> snowpiles = new ArrayList();
    
    int id;
    
    Geometry geom;
    Material snow;
    
    
    
    public Snowpile(AssetManager assetManager, Node node, int id, float x, float z) {
        Sphere s = new Sphere(10, 10, 2);
        
        this.id = id;
        
        geom = new Geometry("Sphere", s);
        snow = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        snow.setTexture("ColorMap", assetManager.loadTexture("Textures/snowmound.png"));
        geom.setMaterial(snow);
        node.attachChild(geom);
        geom.setLocalTranslation(x, 0, z);
    }
}
