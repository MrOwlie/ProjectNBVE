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
    
    ////Entity types
    public static final int PLAYER = 1;
    /////
    
    public static final float DIRECTION_CORRECTION_SPEED = 5f;
    public static final float CORRECTION_SPEED = 8f;
    
    protected Vector3f truePosition;
    protected Vector3f trueDirection = new Vector3f();
    protected Vector3f localDirection = new Vector3f();
    
    protected boolean truePositionReached = true;
    
    protected int entityId;
    
    public MovingEntity(int entityId)
    {
        this.entityId = entityId;
    }
    
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
    
    public void update(float tpf)
    {
        correctDirection(tpf);
        correctPosition(tpf);
    }
    
    protected abstract void correctPosition(float tpf);
    
    public abstract void setViewDirection(Vector3f dir);
    
    public abstract Vector3f getViewDirection();
    
    protected void correctDirection(float tpf)
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
