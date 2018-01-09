/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Anton
 */
public abstract class MovingEntity extends Node{
    public static final float DIRECTION_CORRECTION_SPEED = 5f;
    public static final float CORRECTION_SPEED = 10f;
    
    protected Vector3f truePosition = this.getLocalTranslation();
    protected Vector3f trueDirection = new Vector3f();
    protected Vector3f localDirection = new Vector3f();
    
    protected boolean truePositionReached = true;
    
    
    public void setTruePosition(Vector3f truePosition)
    {
        if(!this.getLocalTranslation().equals(truePosition))
        {
            this.truePosition = truePosition;
            truePositionReached = false;
            
        }
    }
    
    public void setTrueDirection(Vector3f trueDirection)
    {
        this.trueDirection = trueDirection.normalize();
    }
    
    public void setRotation(Quaternion rotation)
    {
        this.setLocalRotation(rotation);
    }
    
    public abstract void correctPosition(float tpf);
    
    public void correctDirection(float tpf)
    {
        Vector3f chaseDirection = 
                truePosition.subtract(this.getLocalTranslation());
        
        if(!localDirection.normalize().equals(chaseDirection.normalize()))
        {
            float fraction = (tpf*DIRECTION_CORRECTION_SPEED)/chaseDirection.length();
            chaseDirection.normalizeLocal();
            
            if(fraction >= 1f) localDirection = chaseDirection;
            else
            {
                localDirection.interpolateLocal(chaseDirection, fraction);
                //localDirection.normalizeLocal();
            }
        }   
    }
    
}
