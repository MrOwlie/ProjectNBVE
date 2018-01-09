/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import static Client.Player.CYLINDER_HEIGHT;
import static Client.Player.CYLINDER_RADIUS;
import static Client.Player.MASS;
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
    
    public static Spatial playerModel;
    private BetterCharacterControl controller;
    
    public RemotePlayer(Vector3f startPos) 
    {
        this.setLocalTranslation(startPos);
        this.attachChild(playerModel.clone());
        
        controller = new BetterCharacterControl(CYLINDER_RADIUS, CYLINDER_HEIGHT, MASS);
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
            
            if(distance < tpf*CORRECTION_SPEED)
            {
                controller.warp(truePosition);
                localDirection = trueDirection;
                truePositionReached = true;
            }
            else
            {
                controller.warp(localDirection.mult(CORRECTION_SPEED));
            }
        }
    }
    
}