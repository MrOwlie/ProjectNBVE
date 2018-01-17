/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;

/**
 *
 * @author Anton
 */
public class Snowball extends MovingEntity{
    public final float SPEED = 60f;
    public final float MASS = 10f;
    
    private RigidBodyControl controller;
    
    public Snowball(Vector3f startPos, Vector3f direction)
    {
        this.setLocalTranslation(startPos);
        this.direction = direction;
        
        controller = new RigidBodyControl(new SphereCollisionShape(0.25f), MASS);
        this.addControl(controller);
        Main.bulletAppState.getPhysicsSpace().add(controller);
        Main.refRootNode.attachChild(this);
        controller.setLinearVelocity(direction.normalize().mult(SPEED));
        
        Modeling.addEntity(this);
    }
    
    
    @Override
    public void setViewDirection(Vector3f dir) {
        
    }

    @Override
    public Vector3f getViewDirection() {
        return Vector3f.UNIT_X;
    }

    @Override
    public void update(float tpf) {
        direction = controller.getLinearVelocity();
    }
    
}
