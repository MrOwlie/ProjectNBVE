/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import com.jme3.network.Client;
import com.jme3.network.Message;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

/**
 *
 * @author Anton
 */
public class NetWrite implements Runnable
{
    static ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<Message>();
    private Client myClient;
    private boolean exit;
    
    public NetWrite(Client myClient)
    {
        this.myClient = myClient;
    }

    @Override
    public void run() 
    {
        while(!exit){
            if(!messageQueue.isEmpty())
            {
                Message message = messageQueue.remove();
                myClient.send(message);
            } 
            
            else 
            {
                try 
                {
                    Thread.sleep((long) 10);
                } 
                catch (InterruptedException ex) 
                {
                    System.out.println("Sleeping thread got interrupted");
                }
            }
        }
    }
    
    public static void addMessage(Message message)
    {
        messageQueue.add(message);   
    }
    
    
    
}
