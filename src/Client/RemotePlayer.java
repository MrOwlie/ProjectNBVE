/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import static Client.Player.CYLINDER_HEIGHT;
import static Client.Player.CYLINDER_RADIUS;
import static Client.Player.MASS;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author Anton
 */
public class RemotePlayer extends MovingEntity
{   
    public static final float SPEED = 8f;
    private static final float TIME_NOT_MOVING_THRESHOLD = 0.1f;
    private static final float MOVEMENT_THRESHOLD = 0.01f;
    
    public static Spatial playerModel;
    
    private BetterCharacterControl controller;
    private AudioNode stepAudio;
    
    private float timeWithNoMovement;
    public RemotePlayer(Vector3f startPos, int entityId) 
    {
        super(entityId);
        
        
        this.setLocalTranslation(startPos);
        truePosition = startPos;
        this.attachChild(playerModel.clone());
        
        stepAudio = new AudioNode(Main.refAssetManager, "Sounds/snow_footsteps.wav", DataType.Stream);
        stepAudio.setLooping(true);  
        stepAudio.setPositional(true);
        stepAudio.setVolume(3);
        this.attachChild(stepAudio);
        
        controller = new BetterCharacterControl(CYLINDER_RADIUS, CYLINDER_HEIGHT, MASS);
        controller.setGravity(new Vector3f(0f,1f,0f));
        Main.bulletAppState.getPhysicsSpace().add(controller);
        Main.refRootNode.attachChild(this);
        Modeling.addEntity(this, entityId);
        this.addControl(controller);
    }

    @Override
    public void correctPosition(float tpf) 
    {
        if(truePositionReached)
        {
            controller.setWalkDirection(trueDirection.mult(SPEED));
            if(trueDirection.equals(Vector3f.ZERO)) timeWithNoMovement += tpf;
            else timeWithNoMovement = 0f;
        }
        
        else
        {
            float distance = truePosition.subtract(this.getLocalTranslation()).length();
            
            if(distance < tpf*CORRECTION_SPEED)
            {
                controller.warp(truePosition);
                localDirection = trueDirection;
                truePositionReached = true;
            }
            else
            {
                controller.warp(this.getLocalTranslation().add(localDirection.mult(CORRECTION_SPEED*tpf)));
            }
            
            if(distance < MOVEMENT_THRESHOLD)timeWithNoMovement += tpf;
            else timeWithNoMovement = 0f;
        }
        
        if(timeWithNoMovement < TIME_NOT_MOVING_THRESHOLD && controller.isOnGround()) stepAudio.play();
        else stepAudio.stop();
    }

    @Override
    public void setViewDirection(Vector3f dir) {
        controller.setViewDirection(dir.negate());
    }

    @Override
    public void destroyEntity() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
