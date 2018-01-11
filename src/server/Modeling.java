/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.jme3.network.Message;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import packets.Packet.PlayerOrientation;

/**
 *
 * @author Anton
 */
public class Modeling 
{
    private static ConcurrentLinkedQueue<PlayerOrientation> playerUpdateQueue = new ConcurrentLinkedQueue<PlayerOrientation>();
    
    public static final float UPDATE_FREQUENCY = 0.1f;
    private ArrayList<MovingEntity> entities = new ArrayList<>();
    private float timeSinceLastUpdate;
    private boolean update;
    
    public void update (float tpf)
    {
        while(!playerUpdateQueue.isEmpty())
        {
            PlayerOrientation newOrientation = playerUpdateQueue.remove();
        }
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
    
    public static void addPlayerUpdate(Message playerupdate)
    {
        playerUpdateQueue.add(playerupdate);
    }
}
