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
import packets.Packet.KeyReleased;

/**
 *
 * @author fredr
 */
public class Networking implements MessageListener<HostedConnection>, ConnectionListener {
    Server server;
    
    public Networking() {
        
        
        
        try {
            this.server = Network.createServer("snowball", 0, 2000, 2000);
            this.server.start();
        } catch (IOException ex) {
            Logger.getLogger(Networking.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void messageReceived(HostedConnection source, Message m) {
        if(m instanceof Authenticate){
            Authenticate packet = (Authenticate) m;
            Player.authenticate(packet.getUsername(), packet.getPassword(), source);
        }
        
        else if(m instanceof KeyPressed) {
            System.out.println("Key pressed: " + ((KeyPressed) m).getKey());
        }else if(m instanceof KeyReleased) {
            
        }
    }

    @Override
    public void connectionAdded(Server server, HostedConnection conn) {
        //Player joins game
        
    }

    @Override
    public void connectionRemoved(Server server, HostedConnection conn) {
        
    }
}
