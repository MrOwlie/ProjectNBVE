/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Spatial;

/**
 *
 * @author mrowlie
 */
public class Map {
    AssetManager assetManager;
    
    
    public Map(AssetManager assetManager) {
        this.assetManager = assetManager;
        Spatial sceneModel = assetManager.loadModel("Scenes/MainScene.j3o");
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(sceneModel);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);
        

        //rootNode.attachChild(geom);
        //rootNode.attachChild(sceneModel);
    }
    
    
}
