/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import static com.jme3.math.FastMath.PI;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Anton
 */
public abstract class MovingEntity extends Node{
    public static final float ACCELERATION = 5f;
    
    protected Vector3f truePosition;
    protected Vector3f trueDirection;
    protected Vector3f localDirection;
    
    protected float speed;
    
    protected boolean truePositionReached;
    
    public void setTruePosition(Vector3f truePosition)
    {
        this.truePosition = truePosition;
        truePositionReached = false;
    }
    
    public void setTrueDirection(Vector3f trueDirection)
    {
        this.trueDirection = trueDirection;
    }
    
    public void setSpeed(float speed)
    {
        this.speed = speed;
    }
    
    public void setLocalDirection(Vector3f localDirection)
    {
        this.localDirection = localDirection;
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
        
        if(!localDirection.normalize().equals(chaseDirection))
        {
            float fraction = (tpf*ACCELERATION)/chaseDirection.length();
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
