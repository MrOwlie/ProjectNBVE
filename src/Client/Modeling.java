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
import packets.Packet.SpawnSnowpile;
import packets.Packet.DespawnSnowpile;
import packets.Packet;
import packets.Packet.DestroyEntity;

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
        while(!playerUpdateQueue.isEmpty())
        {
            handleMessage(playerUpdateQueue.remove());
        }
        
        for(MovingEntity entity : entities.values())
        {
            entity.update(tpf);
        }
        if(Main.localPlayer != null)
        {
            Main.localPlayer.update(tpf);
        }
    }
    
    public static void addEntity(MovingEntity entity, int entityId)
    {
        entities.put(entityId, entity);
    }
    
    public static void removeEntity(int entityId)
    {
        entities.remove(entityId);
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
                entity.setTruePosition(updateEntity.getTruePos());
                entity.setTrueDirection(updateEntity.getTrueDir());
                entity.setViewDirection(updateEntity.getViewDirection());
                
            }
        }
        
        else if(message instanceof SpawnEntity)
        {
            SpawnEntity spawnEntity = (SpawnEntity)message;
            
            if(spawnEntity.getEntityType() == Packet.PLAYER)
            {
                new RemotePlayer(spawnEntity.getPos(), spawnEntity.getEntityId());
            }
            
            else if(spawnEntity.getEntityType() == Packet.SNOWBALL)
            {
                new Snowball(spawnEntity.getPos(), spawnEntity.getEntityId());
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
        
        else if (message instanceof SpawnSnowpile) {
            SpawnSnowpile p = (SpawnSnowpile) message;
            Snowpile newPile = new Snowpile(Main.refAssetManager, Main.refRootNode, p.getId(), p.getX(), p.getZ());
            Snowpile.snowpiles.add(newPile);
        }
        
        else if (message instanceof DespawnSnowpile) {
            DespawnSnowpile p = (DespawnSnowpile) message;
            for(Snowpile pile : Snowpile.snowpiles) {
                if(pile.id == p.getId()) {
                    Main.refRootNode.detachChild(pile.geom);
                    Snowpile.snowpiles.remove(pile);
                    break;
                }
            }
        }
        
        else if (message instanceof DestroyEntity){
            DestroyEntity destroyEntity = (DestroyEntity)message;
            MovingEntity entity = entities.get(destroyEntity.getEntityId());
            if(entity != null) entity.destroyEntity();
        }
    }
    

}
