/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packets;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author mrowlie
 */
public class Packet {
    
    
    abstract public static class MyAbstractMessage extends AbstractMessage {
        
        public MyAbstractMessage() {
        }
    }

    @Serializable
    public static class TimeSync extends MyAbstractMessage {
        protected float time;
        
        public TimeSync() {
            
        }
        
        public TimeSync(float time) {
            this.time = time;
        }
        
        public float getTime(){
            return this.time;
        }
        
    }
    @Serializable
    public static class TimeDiff extends MyAbstractMessage {
        protected float diff;
        
        public TimeDiff() {
            
        }
        
        public TimeDiff(float diff) {
            this.diff = diff;
        }
        
        public float getDiff() {
            return this.diff;
        }
        
    }
    
}