/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import packets.Packet.*;

/**
 *
 * @author Anton
 */
public class NetRead implements MessageListener<Client>
{

    @Override
    public void messageReceived(Client source, Message m) 
    {
        if(m instanceof TimeSync)
        {
            
        } else if(m instanceof AuthPlayer) {
            AuthPlayer p = (AuthPlayer)m;
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
    
}
