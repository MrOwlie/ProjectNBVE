/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.ArrayList;
import java.util.Random;
import packets.Packet.DespawnSnowpile;
import packets.Packet.SpawnSnowpile;
/**
 *
 * @author mrowlie
 */
public class Snowpile {
    static final float MAX_X = 10;
    static final float MAX_Y = 10;
    static final float MAX_AMOUNT = 60;
    static final float TIME_ALIVE = 20;
    
    static ArrayList<Snowpile> snowpiles = new ArrayList();
    
    static Random rand = new Random();
    
    static int idCounter = 0;
    
    int id;
    
    float x;
    float y;
    float timeAlive;
    
    public Snowpile() {
        this.x = (rand.nextFloat() * MAX_X) - (MAX_X / 2);
        this.y = (rand.nextFloat() * MAX_Y) - (MAX_Y / 2);
        this.timeAlive = Snowpile.TIME_ALIVE;
        this.id = Snowpile.idCounter++;
    }
    
    public static void update(float tpf) {
        if (Snowpile.snowpiles.size() < Snowpile.MAX_AMOUNT) {
            Snowpile newPile = new Snowpile();
            Snowpile.snowpiles.add(newPile);
            Networking.server.broadcast(new SpawnSnowpile(newPile.id, newPile.x, newPile.y));
        }
        
        for(Snowpile pile : Snowpile.snowpiles) {
            pile.timeAlive -= tpf;
            if(pile.timeAlive <= 0) {
                Snowpile.snowpiles.remove(pile);
                Networking.server.broadcast(new DespawnSnowpile(pile.id));
                break;
            }
        }
        
    }
    
}
