/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import packets.Packet.PlayerOrientation;
import packets.Packet.UpdateEntity;

/**
 *
 * @author Anton
 */
public class Modeling 
{
    private static ConcurrentLinkedQueue<PlayerOrientation> playerUpdateQueue = new ConcurrentLinkedQueue<PlayerOrientation>();
    
    public static final float UPDATE_FREQUENCY = 1000f;
    private static HashMap<Integer, MovingEntity> entities = new HashMap<Integer, MovingEntity>();
    private float timeSinceLastUpdate;
    private boolean update;
    
    public void update (float tpf)
    {
        if(!playerUpdateQueue.isEmpty())
        {
            PlayerOrientation newOrientation = playerUpdateQueue.remove();
            int entityId = newOrientation.getEntityId();        
            Player player = (Player)entities.get(entityId);
            if(player != null)
            {
                player.setLocalRotation(newOrientation.getModelOrientation());
                player.setForwardAndLeft(newOrientation.getForward(), newOrientation.getLeft());
            }
        }
        
        timeSinceLastUpdate += tpf;
        if(timeSinceLastUpdate >= UPDATE_FREQUENCY)
        {
            update = true;
            timeSinceLastUpdate = 0f;
        }
        
        for(MovingEntity entity : entities.values())
        {
            entity.update(tpf);
            if(update)
            {
              Networking.server.broadcast(new UpdateEntity(entity.getLocalTranslation(), entity.direction, entity.getLocalRotation(), entity.getEntityId()));
            }             
        }
    }
    
    public static void addEntity(MovingEntity entity)
    {
        entities.put(entity.getEntityId(), entity);
    }
    
    public static Collection<MovingEntity> getEntities()
    {
        return entities.values();
    }
    
    public static Player getPlayer(int entityId)
    {
        return (Player)entities.get(entityId);
    }
    
    public static void removeEntity(int entityId)
    {
        entities.remove(entityId);
    }
    
    public static void addPlayerUpdate(PlayerOrientation playerUpdate)
    {
        playerUpdateQueue.add(playerUpdate);
    }
}
