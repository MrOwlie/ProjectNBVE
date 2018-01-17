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
    public final float SPEED = 60f;
    public final float MASS = 10f;
    
    public static Spatial snowballModel;
    
    private RigidBodyControl controller;
    
    public Snowball(Vector3f startPos, int entityId)
    {
        super(entityId);
        this.setLocalTranslation(startPos);
        truePosition = startPos;
        Main.refRootNode.attachChild(this);
        this.attachChild(snowballModel.clone());
                
        controller = new RigidBodyControl(MASS);
        this.addControl(controller);
        Main.bulletAppState.getPhysicsSpace().add(controller);
        
        Modeling.addEntity(this, entityId);

        CORRECTION_SPEED = 60f;
        DIRECTION_CORRECTION_SPEED = 60f;
    }
    
    @Override
    protected void correctPosition(float tpf) 
    {
        System.out.println("Correct pos : "+truePosition);
        if(truePositionReached)
        {
            controller.setLinearVelocity(trueDirection.mult(SPEED));
        }
        
        else
        {
            controller.setLinearVelocity(Vector3f.ZERO);
            
            float distance = truePosition.subtract(this.getLocalTranslation()).length();
            
            if(distance < tpf*CORRECTION_SPEED)
            {
                controller.setPhysicsLocation(truePosition);
                localDirection = trueDirection;
                truePositionReached = true;
            }
            else
            {
                controller.setPhysicsLocation(this.getLocalTranslation().add(localDirection.mult(CORRECTION_SPEED*tpf)));
            }
        }
    }

    @Override
    public void setViewDirection(Vector3f dir) {
        
    }

    @Override
    public void destroyEntity() {
        Modeling.removeEntity(entityId);
        Main.refRootNode.detachChild(this);
        Main.bulletAppState.getPhysicsSpace().remove(controller);
    }
    
}
