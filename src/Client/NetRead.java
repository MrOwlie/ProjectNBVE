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
            Modeling.addMessage(m);
            Main.nifty.gotoScreen("none");
        } else if(m instanceof UpdateEntity) {
            Modeling.addMessage(m);
            
        } else if(m instanceof SpawnEntity) {
            Modeling.addMessage(m);
        }
    }
    
}
