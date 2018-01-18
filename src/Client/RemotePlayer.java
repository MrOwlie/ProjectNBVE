/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import static Client.Player.CYLINDER_HEIGHT;
import static Client.Player.CYLINDER_RADIUS;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author Anton
 */
public class RemotePlayer extends MovingEntity
{   
    public static final float SPEED = 8f;
    public static final float TIME_NOT_MOVING_THRESHOLD = 0.1f;
    public static final float MOVEMENT_THRESHOLD = 0.005f;
    public static final float DISTANCE_THRESHOLD = 0.005f;
    
    public static Spatial playerModel;
    
    private CharacterControl controller;
    private AudioNode stepAudio;
    
    private float timeWithNoMovement;
    public RemotePlayer(Vector3f startPos, int entityId) 
    {
        super(entityId);
        
        
        this.setLocalTranslation(startPos);
        truePosition = startPos;
        Spatial model = playerModel.clone();
        model.setLocalTranslation(Vector3f.UNIT_Y.negate().mult(CYLINDER_HEIGHT/2f));
        this.attachChild(model);
        
        stepAudio = new AudioNode(Main.refAssetManager, "Sounds/snow_footsteps.wav", DataType.Stream);
        stepAudio.setLooping(true);  
        stepAudio.setPositional(true);
        stepAudio.setVolume(0.5f);
        this.attachChild(stepAudio);
        
        controller = new CharacterControl(new CylinderCollisionShape(new Vector3f(CYLINDER_RADIUS, CYLINDER_HEIGHT/2f, 0f),1), 1f);
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
        }
        
        else
        {
            float distance = truePosition.subtract(this.getLocalTranslation()).length();
            
            if(distance < MOVEMENT_THRESHOLD)stepAudio.stop();
            
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
            

        }
        
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
