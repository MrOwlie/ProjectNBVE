/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author Anton
 */
public class Snowball extends MovingEntity
{
    public final float SPEED = 160f;
    public final float MASS = 10f;
    
    public static Spatial snowballModel;
    
    private RigidBodyControl controller;
    
    public Snowball(Vector3f startPos, int entityId)
    {
        super(entityId);
        CORRECTION_SPEED = 160f;
        DIRECTION_CORRECTION_SPEED = 160f;
        
        
        
        this.setLocalTranslation(startPos);
        truePosition = startPos;
        Main.refRootNode.attachChild(this);
        this.attachChild(snowballModel.clone());
                
        controller = new RigidBodyControl(MASS);
        //controller.setKinematicSpatial(true);
        this.addControl(controller);
        //controller.setKinematic(true);
        Main.bulletAppState.getPhysicsSpace().add(controller);
        
        
        Modeling.addEntity(this, entityId);
        
        AudioNode throwAudio = new AudioNode(Main.refAssetManager, "Sounds/throw.wav", DataType.Buffer);
        this.attachChild(throwAudio);
        throwAudio.setDirectional(true);
        throwAudio.setVolume(2);
        throwAudio.playInstance();
        
    }
    
    @Override
    protected void correctPosition(float tpf) 
    {
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
        AudioNode impactSound = new AudioNode(Main.refAssetManager, "Sounds/snowball_hit.wav", DataType.Buffer);
        impactSound.setDirectional(true);
        impactSound.setVolume(2);
        impactSound.setLocalTranslation(truePosition);
        Main.refRootNode.attachChild(impactSound);
        impactSound.playInstance();
        
        
        Modeling.removeEntity(entityId);
        Main.refRootNode.detachChild(this);
        Main.bulletAppState.getPhysicsSpace().remove(controller);
    }

    @Override
    public void warp(Vector3f v) {
        controller.setPhysicsLocation(v);
    }
    
}
