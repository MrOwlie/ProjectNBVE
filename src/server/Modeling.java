/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.jme3.network.Message;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Anton
 */
public class Modeling 
{
    static ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<Message>();
    
    public static final float UPDATE_FREQUENCY = 0.1f;
    private ArrayList<MovingEntity> entities = new ArrayList<>();
    private float timeSinceLastUpdate;
    private boolean update;
    
    public void update (float tpf)
    {
        timeSinceLastUpdate += tpf;
        if(timeSinceLastUpdate >= UPDATE_FREQUENCY)
        {
            update = true;
            timeSinceLastUpdate = 0f;
        }
        for(MovingEntity entity : entities)
        {
            entity.update(tpf);
            if(update)
            {
                // Send update to ALL clients
            }             
        }
    }
    
    public void addEntity(MovingEntity entity)
    {
        entities.add(entity);
    }
}
