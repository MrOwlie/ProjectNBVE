/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
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
    
    public static final float UPDATE_FREQUENCY = 0.025f;
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
                player.setForwardAndLeft(newOrientation.getForward(), newOrientation.getLeft());
                player.setPosition(newOrientation.getPosition());
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
                System.out.println("broadcasting");
                if(entity instanceof Player)
                {
                    Player player = (Player)entity;
                    Networking.server.broadcast(Filters.notEqualTo(player.getConnection()), new UpdateEntity(entity.getLocalTranslation(), entity.direction, entity.getViewDirection(), entity.getEntityId()));
                }
                else
                {
                    Networking.server.broadcast(new UpdateEntity(entity.getLocalTranslation(), entity.direction, entity.getViewDirection(), entity.getEntityId()));
                }
            }           
        }
        
        //Update snowpiles
        Snowpile.update(tpf);
        
        update = false;
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
    
    public static void addPlayerUpdate(PlayerOrientation playerUpdate, HostedConnection source)
    {
        Player player = (Player)entities.get(playerUpdate.getEntityId());
        try
        {           
            if(player.getConnection().equals(source))
            {
                playerUpdateQueue.add(playerUpdate);
            }
            else
            {
                System.out.println("The source didn't have permission to update that player");
            }
        }
        
        catch(Exception e)
        {
            System.out.println("The player could not be updated");
        }
    }
}
