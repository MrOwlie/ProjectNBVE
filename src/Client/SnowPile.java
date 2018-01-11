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

/**
 *
 * @author mrowlie
 */
public class SnowPile {
    
    
    public SnowPile(AssetManager assetManager, Node node, float x, float z) {
        Sphere s = new Sphere(10, 10, 2);
        Geometry geom = new Geometry("Sphere", s);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Textures/snowmound.png"));
        geom.setMaterial(mat);
        geom.setLocalTranslation(x, 0, z);
        node.attachChild(geom);
    }
}
