/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import packets.Packet.Authenticate;
import packets.Packet.KeyPressed;
import packets.Packet.PlayerOrientation;
import packets.Packet.ThrowSnowball;

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
        System.out.println("message recieved.");
        if(m instanceof Authenticate){
            Authenticate packet = (Authenticate) m;
            System.out.println(packet.getUsername() + "  :  " + packet.getPassword());
            Player.authenticate(packet.getUsername(), packet.getPassword(), source);
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
            if(player != null)
            {
                player.throwSnowball(throwSnowball.getDirection());
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
        
    }
}
