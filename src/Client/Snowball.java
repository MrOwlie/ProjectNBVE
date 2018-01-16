/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author Anton
 */
public class Snowball extends MovingEntity
{
    public final float SPEED = 12f;
    public final float MASS = 2f;
    
    public static Spatial snowballModel;
    
    private RigidBodyControl controller;
    
    public Snowball(Vector3f startPos, int entityId)
    {
        super(entityId);
        this.setLocalTranslation(startPos);
        truePosition = startPos;
        Main.refRootNode.attachChild(this);
        
        controller = new RigidBodyControl(MASS);
        this.addControl(controller);
        Main.bulletAppState.getPhysicsSpace().add(controller);
        
        Modeling.addEntity(this, entityId);
    }
    
    @Override
    protected void correctPosition(float tpf) {
        
    }

    @Override
    public void setViewDirection(Vector3f dir) {
        
    }
    
}
