/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.jme3.math.Vector3f;
import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import packets.Packet;
import packets.Packet.Authenticate;
import packets.Packet.KeyPressed;
import packets.Packet.PlayerOrientation;
import packets.Packet.ThrowSnowball;
import packets.Packet.Reload;

/**
 *
 * @author fredr
 */
public class Networking implements MessageListener<HostedConnection>, ConnectionListener {
    public static Server server;
    
    public Networking() {
        
        
        try {
            Networking.server = Network.createServer("UCS", 1, 2000, 2000);
            Networking.server.addMessageListener(this);
            Networking.server.start();
        } catch (IOException ex) {
            Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void messageReceived(HostedConnection source, Message m) {
        if(m instanceof Authenticate){
            for(Snowpile pile : Snowpile.snowpiles) {
                source.send(new Packet.SpawnSnowpile(pile.id, pile.x, pile.z, pile.y));
            }
            Authenticate packet = (Authenticate) m;
            System.out.println(packet.getUsername() + "  :  " + packet.getPassword());
            Player.authenticate(packet.getUsername(), packet.getPassword(), source);
            for(Player player: Player.players) {
                if(source == player.connection) {
                    source.send(new Packet.UpdateGUI(player.hp, player.ammo, player.exp, player.level));
                }
            }
        }
        
        else if(m instanceof KeyPressed) {
            KeyPressed keyPressed = (KeyPressed)m;
            Player player = Modeling.getPlayer(keyPressed.getEntityId());
            if(player != null)
            {
            }
            System.out.println("Key pressed: " + ((KeyPressed) m).getKey());
        }
        
        else if(m instanceof PlayerOrientation) {
            Modeling.addPlayerUpdate((PlayerOrientation)m, source);
        }
        
        else if(m instanceof ThrowSnowball){
            ThrowSnowball throwSnowball = (ThrowSnowball)m;
            Player player = Modeling.getPlayer(throwSnowball.getEntityId());
            System.out.println("ThrowSnowball recieved");
            if(player != null)
            {
                System.out.println("Player was found");
                player.throwSnowball(throwSnowball.getDirection());
            }
        }
        
        else if(m instanceof Reload) {
            System.out.println("Recieved Reload Message!");
            for(Player player : Player.players) {
                if(player.ammo < 3) {
                    System.out.println("I NEED AMMO");
                    if(source == player.getConnection()) {
                        System.out.println("CONNECTION MATCH");
                        for(Snowpile pile : Snowpile.snowpiles) {
                            System.out.println("DISTANCE: " + player.getLocalTranslation().distance(new Vector3f(pile.x, 0, pile.z)));
                            if(player.getLocalTranslation().distance(new Vector3f(pile.x, pile.y, pile.z)) < 15f) {
                                System.out.println("ADDING AMMO AND REMOVING PILE..");
                                player.reload();
                                Networking.server.broadcast(new Packet.DespawnSnowpile(pile.id));
                                Snowpile.snowpiles.remove(pile);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void connectionAdded(Server server, HostedConnection conn) {
        //Player joins game
        System.out.println("Player joined game.");
        
    }

    @Override
    public void connectionRemoved(Server server, HostedConnection conn) {
        for(Player player: Player.players) {
            if(player.connection == conn) {
                Networking.server.broadcast(new Packet.DestroyEntity(player.entityId));
                Modeling.removeEntity(player.entityId);
            }
        }
    }
}
