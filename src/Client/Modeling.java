/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import com.jme3.math.Vector3f;
import com.jme3.network.Message;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import packets.Packet.SpawnEntity;
import packets.Packet.UpdateEntity;
import packets.Packet.AuthPlayer;
import packets.Packet;

/**
 *
 * @author Anton
 */
public class Modeling
{
    private static ConcurrentLinkedQueue<Message> playerUpdateQueue = new ConcurrentLinkedQueue<Message>();
    private static HashMap<Integer, MovingEntity> entities = new HashMap<Integer, MovingEntity>();
    private float currentTpf = 0f;
    private float nextTpf = 0f;
    private boolean running = true;
    
    public void update(float tpf) 
    {
        if(!playerUpdateQueue.isEmpty())
        {
            handleMessage(playerUpdateQueue.remove());
        }
        
        for(MovingEntity entity : entities.values())
        {
            entity.update(tpf);
        }
    }
    
    public static void addEntity(MovingEntity entity, int entityId)
    {
        entities.put(entityId, entity);
    }
    
    public static void addMessage(Message message)
    {
        playerUpdateQueue.add(message);
    }
    
    private void handleMessage(Message message)
    {
        if(message instanceof UpdateEntity)
        {
            UpdateEntity updateEntity = (UpdateEntity)message;
            MovingEntity entity = entities.get(updateEntity.getEntityId());
            if(entity != null) {
                System.out.println("ID: " + updateEntity.getEntityId() + ", POS: " + updateEntity.getTruePos() + ", DIR: " + updateEntity.getTrueDir());
                entity.setTruePosition(updateEntity.getTruePos());
                entity.setTrueDirection(updateEntity.getTrueDir());
                entity.setLocalRotation(updateEntity.getEntityOrientation());
                
            }
        }
        
        else if(message instanceof SpawnEntity)
        {
            SpawnEntity spawnEntity = (SpawnEntity)message;
            
            if(spawnEntity.getEntityType() == Packet.PLAYER)
            {
                new RemotePlayer(spawnEntity.getPos(), spawnEntity.getEntityId());
            }
        }
        
        else if(message instanceof AuthPlayer)
        {
            AuthPlayer p = (AuthPlayer)message;
            Main.localPlayer = new Player(
                    p.getLevel(),
                    p.getExp(),
                    p.getAmmo(),
                    new Vector3f(p.getX(), p.getY(), p.getZ()),
                    Main.refCam,
                    p.getEntityId()                    
            );
        }
    }
    
    public synchronized void updateTpf(float tpf)
    {
        this.nextTpf += tpf;
    }
    
    private synchronized void setCurrentTpf()
    {
        currentTpf = nextTpf;
        nextTpf = 0f;
    }
    
    private synchronized float getNextTpf()
    {
        return nextTpf;
    }
}
