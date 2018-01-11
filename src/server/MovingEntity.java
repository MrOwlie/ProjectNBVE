/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Anton
 */
public abstract class MovingEntity extends Node 
{
    private static int nextId = 0;
    
    protected Vector3f direction = new Vector3f();
    protected int entityId;
    
    public MovingEntity()
    {
        entityId = nextId++;
    }
    
    public int getEntityId()
    {
        return entityId;
    }
    
    public abstract void update(float tpf);
    
}
