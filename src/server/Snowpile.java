/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author mrowlie
 */
public class Snowpile {
    static final float MAX_X = 40;
    static final float MAX_Y = 40;
    static final float MAX_AMOUNT = 10;
    static final float TIME_ALIVE = 20;
    
    static ArrayList<Snowpile> snowpiles = new ArrayList();
    
    static Random rand = new Random();
    
    float x;
    float y;
    float timeAlive;
    
    public Snowpile() {
        this.x = (rand.nextFloat() * MAX_X) - (MAX_X / 2);
        this.y = (rand.nextFloat() * MAX_Y) - (MAX_Y / 2);
        this.timeAlive = Snowpile.TIME_ALIVE;
    }
    
    public static void update(float tpf) {
        if (Snowpile.snowpiles.size() < Snowpile.MAX_AMOUNT) {
            Snowpile.snowpiles.add(new Snowpile());
        }
        
        for(Snowpile pile : Snowpile.snowpiles) {
            pile.timeAlive -= tpf;
            if(pile.timeAlive <= 0) {
                Snowpile.snowpiles.remove(pile);
            }
        }
        
    }
    
}
