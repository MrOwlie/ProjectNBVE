/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import static com.jme3.math.FastMath.PI;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Anton
 */
public abstract class MovingEntity extends Node{
    public static final float ANGULAR_VELOCITY = 2*PI/30f;
    
    protected Vector3f truePosition;
    protected Vector3f trueDirection;
    protected Vector3f localDirection;
    protected Vector3f chaseDirection;
    
    protected float speed;
    
    public void setTruePosition(Vector3f truePosition)
    {
        this.truePosition = truePosition;
        this.chaseDirection = truePosition.subtract(this.getLocalTranslation()).normalize();
    }
    
    public void setTrueVelocity(Vector3f trueDirection)
    {
        this.trueDirection = trueDirection;
    }
    
    public void setSpeed(float speed)
    {
        this.speed = speed;
    }
    
    public abstract void correctPosition(float tpf);
    
    public void correctDirection(float tpf)
    {
        if(!localDirection.equals(chaseDirection))
        {
            float fraction = (tpf*ANGULAR_VELOCITY)/
                            (localDirection.angleBetween(chaseDirection));
            if(fraction >= 1f) localDirection = chaseDirection.clone();
            else
            {
                localDirection.interpolateLocal(chaseDirection, fraction);
                localDirection.normalizeLocal();
            }
        }   
    }
    
}
